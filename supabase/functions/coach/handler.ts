/**
 * Edge Function Supabase « coach » à déployer séparément.
 * Fournisseur serveur : COACH_AI_PROVIDER=openai (défaut) ou groq.
 * Secret requis : OPENAI_API_KEY ou GROQ_API_KEY, uniquement pour le fournisseur choisi.
 * Variables natives Supabase : SUPABASE_URL et SUPABASE_ANON_KEY.
 * Un token d'authentification est requis, et le quota de 100 appels est atomique.
 */
import { allowedEmail, InputError, normalizeInput } from "./protocol.ts";
import { buildCoachProviderRequest, coachProviderHeaderMatches, extractCoachProviderAnswer, resolveCoachProvider } from "./provider.ts";
import { providerCooldown, providerReportedTokens, readCapacityAdmission, reserveTokenEstimate } from "./capacity.ts";

function json(body: unknown, status = 200, retryAfter?: number): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "Content-Type": "application/json; charset=utf-8", "Cache-Control": "no-store",
      ...(retryAfter === undefined ? {} : { "Retry-After": String(retryAfter) }) },
  });
}

export interface CoachClientPort {
  auth: { getUser(token: string): Promise<{ data: { user: {
    id: string; email?: string; email_confirmed_at?: string;
  } | null }; error: unknown }> };
  rpc(name: string, args?: Record<string, unknown>): PromiseLike<{ data: unknown; error: { message: string } | null }>;
}
export interface CoachHandlerDependencies {
  readEnv(name: string): string | undefined;
  createClient(url: string, key: string, options: {
    auth: { persistSession: false }; global?: { headers: { Authorization: string } };
  }): CoachClientPort;
  fetch: typeof fetch;
  warn(code: string): void;
}
export function createCoachHandler(deps: CoachHandlerDependencies): (request: Request) => Promise<Response> {
return async (request) => {
  if (request.method !== "POST") return json({ error: "METHOD_NOT_ALLOWED" }, 405);
  const url = deps.readEnv("SUPABASE_URL");
  const anonKey = deps.readEnv("SUPABASE_ANON_KEY");
  const provider = resolveCoachProvider(name => deps.readEnv(name));
  const serviceKey = deps.readEnv("SUPABASE_SERVICE_ROLE_KEY");
  const allowed = deps.readEnv("COACH_ALLOWED_EMAILS");
  if (!url || !anonKey || !provider || !serviceKey || !allowed?.trim())
    return json({ error: "NOT_CONFIGURED" }, 503);
  if (!coachProviderHeaderMatches(provider.provider, request.headers.get("X-Selkie-Coach-Provider")))
    return json({ error: "PROVIDER_MISMATCH" }, 503);
  const bearer = request.headers.get("Authorization") ?? "";
  if (!bearer.startsWith("Bearer ")) return json({ error: "UNAUTHORIZED" }, 401);

  const client = deps.createClient(url, anonKey, {
    global: { headers: { Authorization: bearer } },
    auth: { persistSession: false },
  });
  const { data: { user }, error: authError } = await client.auth.getUser(bearer.slice(7));
  if (authError || !user) return json({ error: "UNAUTHORIZED" }, 401);
  if (!user.email_confirmed_at || !allowedEmail(user.email, allowed))
    return json({ error: "BETA_ACCESS_REQUIRED" }, 403);

  let input: ReturnType<typeof normalizeInput>;
  try {
    const reader = request.body?.getReader();
    if (!reader) return json({ error: "INVALID_JSON" }, 400);
    let size = 0;
    const chunks: Uint8Array[] = [];
    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      size += value.byteLength;
      if (size > 32768) { await reader.cancel(); return json({ error: "PAYLOAD_TOO_LARGE" }, 413); }
      chunks.push(value);
    }
    const bytes = new Uint8Array(size);
    let offset = 0;
    for (const chunk of chunks) { bytes.set(chunk, offset); offset += chunk.byteLength; }
    input = normalizeInput(JSON.parse(new TextDecoder().decode(bytes)));
  } catch (error) {
    return json({ error: error instanceof InputError ? error.message : "INVALID_JSON" }, 400);
  }

  const admin = deps.createClient(url, serviceKey, { auth: { persistSession: false } });

  const { data: remaining, error: creditError } = await client.rpc("consume_coach_credit");
  if (creditError) {
    const quota = creditError.message.includes("FREE_QUOTA_EXCEEDED");
    const limited = creditError.message.includes("RATE_LIMITED");
    return json({ error: quota ? "FREE_QUOTA_EXCEEDED" : limited ? "RATE_LIMITED" : "QUOTA_ERROR" },
      quota ? 402 : limited ? 429 : 500);
  }
  let answered = false;
  let leaseId: string | null = null;
  let outcome = "failed";
  let reportedTokens: number | null = null;
  let cooldown = 0;
  try {
    const providerRequest = buildCoachProviderRequest(provider, input);
    const { data: capacity, error: capacityError } = await admin.rpc("reserve_coach_capacity", {
      capacity_scope: provider.provider,
      token_reservation: reserveTokenEstimate(String(providerRequest.init.body)),
    });
    const admission = capacityError ? null : readCapacityAdmission(capacity);
    // Configuration absent, malformed or database failure: never contact the paid/free API anyway.
    if (!admission) {
      deps.warn("COACH_CAPACITY_ADMISSION_FAILED");
      return json({ error: "COACH_TEMPORARILY_UNAVAILABLE" }, 503, 60);
    }
    if (!admission.allowed) {
      const status = admission.error === "REQUEST_EXCEEDS_CAPACITY" ? 413
        : admission.error === "CAPACITY_NOT_CONFIGURED" || admission.error === "GLOBAL_DAILY_CAPACITY" ? 503 : 429;
      return json({ error: admission.error, retryAfterSeconds: admission.retryAfter }, status, admission.retryAfter);
    }
    leaseId = admission.leaseId;
    const completion = await deps.fetch(providerRequest.url, {
      ...providerRequest.init,
      signal: AbortSignal.timeout(20000),
    });
    if (completion.status === 429) {
      outcome = "rate_limited";
      cooldown = providerCooldown(completion.headers);
      return json({ error: "COACH_TEMPORARILY_UNAVAILABLE", retryAfterSeconds: cooldown }, 429, cooldown);
    }
    if (!completion.ok) return json({ error: "COACH_TEMPORARILY_UNAVAILABLE" }, 502);
    const result = await completion.json();
    reportedTokens = providerReportedTokens(result);
    const answer = extractCoachProviderAnswer(result, provider.provider);
    if (!answer) return json({ error: "EMPTY_RESPONSE" }, 502);
    answered = true;
    outcome = "success";
    return json({ answer, questionsRemaining: remaining });
  } catch {
    return json({ error: "COACH_TEMPORARILY_UNAVAILABLE" }, 502);
  } finally {
    if (leaseId) {
      try {
        const { error } = await admin.rpc("finish_coach_capacity", { lease_id: leaseId, result_outcome: outcome,
          reported_tokens: reportedTokens, cooldown_seconds: cooldown });
        if (error) deps.warn("COACH_CAPACITY_COMPLETION_FAILED");
      } catch { deps.warn("COACH_CAPACITY_COMPLETION_FAILED"); }
    }
    if (!answered) {
      // Réservation atomique avant l'appel, restitution si l'IA n'a pas répondu.
      // Cette RPC est réservée au serveur ; le téléphone ne peut pas créditer son quota.
      try {
        const { error } = await admin.rpc("refund_coach_credit", { target_user: user.id });
        if (error) deps.warn("COACH_CREDIT_REFUND_FAILED");
      } catch { deps.warn("COACH_CREDIT_REFUND_FAILED"); }
    }
  }
};
}
