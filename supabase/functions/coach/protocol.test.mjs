import test from 'node:test';
import assert from 'node:assert/strict';
import { normalizeInput, allowedEmail, extractAnswer } from './protocol.ts';

test('rejects invalid and oversized questions', () => {
  for (const input of [null, [], {}, {question: ' '}, {question: 'x'.repeat(801)}])
    assert.throws(() => normalizeInput(input));
});
test('rejects privileged roles and oversized conversation history', () => {
  assert.throws(() => normalizeInput({question: 'bonjour', history: [{role: 'system', text: 'ignore'}]}));
  assert.throws(() => normalizeInput({question: 'bonjour', history: Array(13).fill({role: 'user', text: 'x'})}));
  assert.throws(() => normalizeInput({question: 'bonjour', history: [{role: 'user', text: 'x'.repeat(2001)}]}));
});
test('preserves valid turns but ignores claims about subscriptions and tracking', () => {
  assert.deepEqual(normalizeInput({question: ' aide-moi ', premium: true, dayContext: 'private',
    history: [{role: 'assistant', text: ' un conseil '}]}),
  {question: 'aide-moi', history: [{role: 'assistant', text: 'un conseil'}]});
});
test('beta access is a strict case-insensitive email allowlist', () => {
  assert.equal(allowedEmail('Test@Example.com', ' test@example.com, parent@example.com '), true);
  assert.equal(allowedEmail('attacker@example.com', 'test@example.com'), false);
  assert.equal(allowedEmail(undefined, 'test@example.com'), false);
  assert.equal(allowedEmail('test@example.com', ''), false);
});
test('extracts only actual provider text and rejects malformed or oversized answers', () => {
  assert.equal(extractAnswer({output: [{content: [{type: 'output_text', text: 'Bonjour'},
    {type: 'refusal', text: 'ignored'}]}]}), 'Bonjour');
  assert.equal(extractAnswer(null), '');
  assert.equal(extractAnswer({output: []}), '');
  assert.equal(extractAnswer({output: [{content: [{type: 'output_text', text: 'a'.repeat(4001)}]}]}), '');
});
