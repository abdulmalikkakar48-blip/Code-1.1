/**
 * Edge Function Supabase « coach » à déployer séparément.
 * Secrets requis : OPENAI_API_KEY (serveur uniquement).
 * Variables natives Supabase : SUPABASE_URL et SUPABASE_ANON_KEY.
 * Un token d'authentification est requis, et le quota de 100 appels est atomique.
 */
import { createClient } from "npm:@supabase/supabase-js@2";

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
  if (!url || !anonKey || !openaiKey) return json({ error: "NOT_CONFIGURED" }, 503);
  const bearer = request.headers.get("Authorization") ?? "";
  if (!bearer.startsWith("Bearer ")) return json({ error: "UNAUTHORIZED" }, 401);

  const client = createClient(url, anonKey, {
    global: { headers: { Authorization: bearer } },
    auth: { persistSession: false },
  });
  const { data: { user }, error: authError } = await client.auth.getUser(bearer.slice(7));
  if (authError || !user) return json({ error: "UNAUTHORIZED" }, 401);

  let input: Record<string, unknown>;
  try { input = await request.json(); }
  catch { return json({ error: "INVALID_JSON" }, 400); }
  const question = input.question;
  if (typeof question !== "string" || question.trim().length < 1 || question.length > 800)
    return json({ error: "INVALID_QUESTION" }, 400);
  const context = typeof input.dayContext === "string" ? input.dayContext.slice(0, 2500) : "";

  const { data: remaining, error: creditError } = await client.rpc("consume_coach_credit");
  if (creditError) {
    const quota = creditError.message.includes("FREE_QUOTA_EXCEEDED");
    return json({ error: quota ? "FREE_QUOTA_EXCEEDED" : "QUOTA_ERROR" }, quota ? 402 : 500);
  }
  try {
    const completion = await fetch("https://api.openai.com/v1/responses", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${openaiKey}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        model: "gpt-4.1-mini",
        max_output_tokens: 220,
        instructions: "Tu es le coach de Milo. Réponds en français en 2 à 5 phrases courtes, de manière encourageante, sans juger. Propose des actions simples pour réduire le scrolling. Ne prétends pas être un professionnel de santé.",
        input: `Contexte d'utilisation fourni par l'utilisateur : ${context}\n\nQuestion : ${question.trim()}`,
      }),
      signal: AbortSignal.timeout(20000),
    });
    if (!completion.ok) return json({ error: "COACH_TEMPORARILY_UNAVAILABLE" }, 502);
    const result = await completion.json();
    const answer = (result.output ?? [])
      .flatMap((x: { content?: Array<{ type?: string; text?: string }> }) => x.content ?? [])
      .filter((part: { type?: string }) => part.type === "output_text")
      .map((part: { text?: string }) => part.text ?? "")
      .join("\n").trim();
    if (!answer) return json({ error: "EMPTY_RESPONSE" }, 502);
    return json({ answer, questionsRemaining: remaining });
  } catch {
    // Le crédit a déjà été réservé, pour éviter la double consommation concurrente.
    return json({ error: "COACH_TEMPORARILY_UNAVAILABLE" }, 502);
  }
});
