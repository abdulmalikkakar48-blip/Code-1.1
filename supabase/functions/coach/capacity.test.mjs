import test from 'node:test';
import assert from 'node:assert/strict';
import { reserveTokenEstimate, providerReportedTokens, providerCooldown, readCapacityAdmission } from './capacity.ts';

test('reservation includes UTF-8 bytes, framing and bounded output rather than counting characters', () => {
  const ascii = reserveTokenEstimate('hello');
  const multilingual = reserveTokenEstimate('🌊你好');
  assert.ok(multilingual > ascii);
  assert.ok(multilingual >= new TextEncoder().encode('🌊你好').length + 220);
  assert.ok(reserveTokenEstimate('x'.repeat(32000)) > 32000);
});
test('reported usage accepts only a bounded non-negative safe integer', () => {
  assert.equal(providerReportedTokens({ usage: { total_tokens: 123 } }), 123);
  for (const value of [-1, 1.5, '123', NaN, Infinity, 1000000001, Number.MAX_SAFE_INTEGER])
    assert.equal(providerReportedTokens({ usage: { total_tokens: value } }), null);
  for (const value of [null, {}, { usage: {} }]) assert.equal(providerReportedTokens(value), null);
});
test('429 cooldown uses seconds or HTTP date, with a bounded default on invalid headers', () => {
  const now = Date.parse('2026-10-13T12:00:00Z');
  assert.equal(providerCooldown(new Headers({ 'retry-after': '120' }), now), 120);
  assert.equal(providerCooldown(new Headers({ 'retry-after': '1.2' }), now), 2);
  assert.equal(providerCooldown(new Headers({ 'retry-after': 'Tue, 13 Oct 2026 12:02:00 GMT' }), now), 120);
  assert.equal(providerCooldown(new Headers({ 'retry-after': '999999' }), now), 86400);
  for (const value of ['-5', 'garbage', 'Tue, 13 Oct 2026 11:00:00 GMT'])
    assert.equal(providerCooldown(new Headers({ 'retry-after': value }), now), 60);
  assert.equal(providerCooldown(new Headers(), now), 60);
});
test('malformed or missing admission results fail closed', () => {
  const leaseId = '00000000-0000-0000-0000-000000000001';
  assert.deepEqual(readCapacityAdmission({ allowed: true, leaseId }), { allowed: true, leaseId });
  assert.deepEqual(readCapacityAdmission({ allowed: false, error: 'GLOBAL_DAILY_CAPACITY', retryAfter: 80000 }),
    { allowed: false, error: 'GLOBAL_DAILY_CAPACITY', retryAfter: 80000 });
  for (const value of [null, {}, { allowed: true }, { allowed: true, leaseId: 'untrusted' },
    { allowed: false, error: 'untrusted', retryAfter: 60 },
    { allowed: false, error: 'GLOBAL_MINUTE_CAPACITY', retryAfter: -1 }])
    assert.equal(readCapacityAdmission(value), null);
});
