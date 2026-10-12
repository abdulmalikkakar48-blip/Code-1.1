import { extractAnswer, type normalizeInput } from "./protocol.ts";

export type CoachAIProvider = "openai" | "groq";
export type CoachInput = ReturnType<typeof normalizeInput>;

export interface CoachProviderConfig {
  readonly provider: CoachAIProvider;
  readonly apiKey: string;
  readonly model: string;
}

const COACH_INSTRUCTIONS = "Tu es le coach de Selkie. Réponds en français en 2 à 5 phrases courtes, de manière encourageante, sans juger. Propose des actions simples pour réduire le scrolling. Ne prétends pas être un professionnel de santé.";

/** Only this server configuration selects a provider; request data cannot change it. */
export function resolveCoachProvider(
  readEnv: (name: string) => string | undefined,
): CoachProviderConfig | null {
  const configured = readEnv("COACH_AI_PROVIDER");
  const provider = configured === undefined ? "openai" : configured.trim().toLowerCase();
  if (provider !== "openai" && provider !== "groq") return null;
  const apiKey = readEnv(provider === "groq" ? "GROQ_API_KEY" : "OPENAI_API_KEY")?.trim();
  if (!apiKey || /\s/.test(apiKey)) return null;
  const configuredModel = readEnv(provider === "groq" ? "GROQ_MODEL" : "OPENAI_MODEL");
  const model = configuredModel === undefined
    ? provider === "groq" ? "llama-3.3-70b-versatile" : "gpt-4.1-mini"
    : configuredModel.trim();
  if (!/^[a-zA-Z0-9][a-zA-Z0-9_.:/-]{0,127}$/.test(model)) return null;
  return { provider, apiKey, model };
}

/** The header declares the provider shown in the client's consent; it never selects the server. */
export function coachProviderHeaderMatches(
  configured: CoachAIProvider,
  header: string | null | undefined,
): boolean {
  if (header === null || header === undefined) return configured === "openai";
  return header === configured;
}

/** Fixed destinations and bounded output; there is no secondary-provider fallback. */
export function buildCoachProviderRequest(
  config: CoachProviderConfig,
  input: CoachInput,
): { url: string; init: RequestInit } {
  const turns = [
    ...input.history.map(message => ({ role: message.role, content: message.text })),
    { role: "user", content: input.question },
  ];
  const body = config.provider === "groq" ? {
    model: config.model,
    messages: [{ role: "system", content: COACH_INSTRUCTIONS }, ...turns],
    max_completion_tokens: 220,
    stream: false,
  } : {
    model: config.model,
    store: false,
    max_output_tokens: 220,
    instructions: COACH_INSTRUCTIONS,
    input: turns,
  };
  return {
    url: config.provider === "groq"
      ? "https://api.groq.com/openai/v1/chat/completions"
      : "https://api.openai.com/v1/responses",
    init: {
      method: "POST",
      headers: { Authorization: `Bearer ${config.apiKey}`, "Content-Type": "application/json" },
      body: JSON.stringify(body),
    },
  };
}

export function extractCoachProviderAnswer(raw: unknown, provider: CoachAIProvider): string {
  if (provider === "openai") return extractAnswer(raw);
  if (!raw || typeof raw !== "object" || Array.isArray(raw)) return "";
  const choices = (raw as { choices?: unknown }).choices;
  if (!Array.isArray(choices)) return "";
  if (choices[0]?.message?.role !== "assistant") return "";
  const content = choices[0]?.message?.content;
  if (typeof content !== "string") return "";
  const answer = content.trim();
  return answer.length <= 4000 ? answer : "";
}
