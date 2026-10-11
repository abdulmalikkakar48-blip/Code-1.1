/**
 * Edge Function Supabase « coach » à déployer séparément.
 * Secrets requis : OPENAI_API_KEY (serveur uniquement).
 * Variables natives Supabase : SUPABASE_URL et SUPABASE_ANON_KEY.
 * Un token d'authentification est requis, et le quota de 100 appels est atomique.
 */
import { createClient } from "npm:@supabase/supabase-js@2";
import { allowedEmail, extractAnswer, InputError, normalizeInput } from "./protocol.ts";

function json(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "Content-Type": "application/json; charset=utf-8", "Cache-Control": "no-store" },
  });
}

Deno.serve(async (request) => {
  if (request.method !== "POST") return json({ error: "METHOD_NOT_ALLOWED" }, 405);
  const url = Deno.env.get("SUPABASE_URL");
  const anonKey = Deno.env.get("SUPABASE_ANON_KEY");
  const openaiKey = Deno.env.get("OPENAI_API_KEY");
  const serviceKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");
  const allowed = Deno.env.get("COACH_ALLOWED_EMAILS");
  if (!url || !anonKey || !openaiKey || !serviceKey || !allowed?.trim())
    return json({ error: "NOT_CONFIGURED" }, 503);
  const bearer = request.headers.get("Authorization") ?? "";
  if (!bearer.startsWith("Bearer ")) return json({ error: "UNAUTHORIZED" }, 401);

  const client = createClient(url, anonKey, {
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

  const { data: remaining, error: creditError } = await client.rpc("consume_coach_credit");
  if (creditError) {
    const quota = creditError.message.includes("FREE_QUOTA_EXCEEDED");
    const limited = creditError.message.includes("RATE_LIMITED");
    return json({ error: quota ? "FREE_QUOTA_EXCEEDED" : limited ? "RATE_LIMITED" : "QUOTA_ERROR" },
      quota ? 402 : limited ? 429 : 500);
  }
  let answered = false;
  try {
    const completion = await fetch("https://api.openai.com/v1/responses", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${openaiKey}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model: Deno.env.get("OPENAI_MODEL") || "gpt-4.1-mini",
        store: false,
        max_output_tokens: 220,
        instructions: "Tu es le coach de Selkie. Réponds en français en 2 à 5 phrases courtes, de manière encourageante, sans juger. Propose des actions simples pour réduire le scrolling. Ne prétends pas être un professionnel de santé.",
        input: [
          ...input.history.map(message => ({ role: message.role, content: message.text })),
          { role: "user", content: input.question },
        ],
      }),
      signal: AbortSignal.timeout(20000),
    });
    if (!completion.ok) return json({ error: "COACH_TEMPORARILY_UNAVAILABLE" }, 502);
    const result = await completion.json();
    const answer = extractAnswer(result);
    if (!answer) return json({ error: "EMPTY_RESPONSE" }, 502);
    answered = true;
    return json({ answer, questionsRemaining: remaining });
  } catch {
    return json({ error: "COACH_TEMPORARILY_UNAVAILABLE" }, 502);
  } finally {
    if (!answered) {
      // Réservation atomique avant l'appel, restitution si l'IA n'a pas répondu.
      // Cette RPC est réservée au serveur ; le téléphone ne peut pas créditer son quota.
      try {
        const admin = createClient(url, serviceKey, { auth: { persistSession: false } });
        await admin.rpc("refund_coach_credit", { target_user: user.id });
      } catch { /* Pas de journal des messages, adresses ou jetons. */ }
    }
  }
});
