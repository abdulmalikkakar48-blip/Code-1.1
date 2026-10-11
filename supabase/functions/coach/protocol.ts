export class InputError extends Error {}

export function normalizeInput(raw: unknown): {
  question: string;
  history: Array<{ role: "user" | "assistant"; text: string }>;
} {
  if (!raw || typeof raw !== "object" || Array.isArray(raw)) throw new InputError("INVALID_QUESTION");
  const input = raw as Record<string, unknown>;
  if (typeof input.question !== "string" || input.question.length > 800 || !input.question.trim())
    throw new InputError("INVALID_QUESTION");
  const history = input.history ?? [];
  if (!Array.isArray(history) || history.length > 12) throw new InputError("INVALID_HISTORY");
  const normalized = history.map<{ role: "user" | "assistant"; text: string }>((message: unknown) => {
    if (!message || typeof message !== "object") throw new InputError("INVALID_HISTORY");
    const entry = message as Record<string, unknown>;
    if ((entry.role !== "user" && entry.role !== "assistant") ||
      typeof entry.text !== "string" || !entry.text.trim() || entry.text.length > 2000)
      throw new InputError("INVALID_HISTORY");
    return { role: entry.role, text: entry.text.trim() };
  });
  return { question: input.question.trim(), history: normalized };
}

export function allowedEmail(email: string | undefined, configured: string): boolean {
  if (!email) return false;
  const list = configured.split(",").map(value => value.trim().toLowerCase()).filter(Boolean);
  return list.includes(email.toLowerCase());
}

export function extractAnswer(raw: unknown): string {
  if (!raw || typeof raw !== "object") return "";
  const output = (raw as { output?: unknown }).output;
  if (!Array.isArray(output)) return "";
  const answer = output.flatMap(item => Array.isArray(item?.content) ? item.content : [])
    .filter(item => item?.type === "output_text" && typeof item.text === "string")
    .map(item => item.text).join("\n").trim();
  return answer.length <= 4000 ? answer : "";
}
