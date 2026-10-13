/** Provider token accounting varies. This deliberately generous estimate is not a tokenizer guarantee. */
export function reserveTokenEstimate(body: string): number {
  return new TextEncoder().encode(body).byteLength + 1024 + 220;
}

export function providerReportedTokens(raw: unknown): number | null {
  if (!raw || typeof raw !== 'object') return null;
  const value = (raw as { usage?: { total_tokens?: unknown } }).usage?.total_tokens;
  return typeof value === 'number' && Number.isSafeInteger(value) && value >= 0 && value <= 1000000000
    ? value : null;
}

/** Honor a supplier's Retry-After when present, otherwise pause globally for one minute. */
export function providerCooldown(headers: Headers, now = Date.now()): number {
  const retry = headers.get('retry-after');
  if (!retry) return 60;
  const seconds = /^\d+(?:\.\d+)?$/.test(retry.trim())
    ? Number(retry) : (Date.parse(retry) - now) / 1000;
  return Number.isFinite(seconds) && seconds > 0 ? Math.min(86400, Math.ceil(seconds)) : 60;
}

export type CapacityAdmission =
  { allowed: true; leaseId: string } | { allowed: false; error: string; retryAfter: number };
const reasons = new Set(['CAPACITY_NOT_CONFIGURED', 'PROVIDER_COOLDOWN', 'REQUEST_EXCEEDS_CAPACITY',
  'GLOBAL_DAILY_CAPACITY', 'GLOBAL_MINUTE_CAPACITY', 'GLOBAL_CONCURRENCY_CAPACITY']);
export function readCapacityAdmission(raw: unknown): CapacityAdmission | null {
  if (!raw || typeof raw !== 'object') return null;
  const value = raw as Record<string, unknown>;
  if (value.allowed === true && typeof value.leaseId === 'string' &&
    /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(value.leaseId))
    return { allowed: true, leaseId: value.leaseId };
  if (value.allowed === false && typeof value.error === 'string' && reasons.has(value.error) &&
    typeof value.retryAfter === 'number' && Number.isSafeInteger(value.retryAfter) &&
    value.retryAfter >= 0 && value.retryAfter <= 86400)
    return { allowed: false, error: value.error, retryAfter: value.retryAfter };
  return null;
}
