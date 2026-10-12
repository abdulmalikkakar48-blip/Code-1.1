import test from 'node:test';
import assert from 'node:assert/strict';
import { normalizeInput } from './protocol.ts';
import { resolveCoachProvider, coachProviderHeaderMatches, buildCoachProviderRequest,
  extractCoachProviderAnswer } from './provider.ts';

const read = values => name => values[name];
const openaiFixture = 'synthetic-openai-server-secret';
const groqFixture = 'synthetic-groq-server-secret';
const input = normalizeInput({
  question: 'Comment arrêter le défilement après mon cours ?',
  history: [{ role: 'user', text: 'Je veux une pause courte.' },
    { role: 'assistant', text: 'Choisis une transition adaptée.' }],
  // Values supplied by a client cannot select the provider, URL, model or transmitted tracking data.
  provider: 'openai', endpoint: 'https://attacker.example', model: 'untrusted-model',
  apiKey: 'untrusted-key', premium: true, dayContext: 'private-tracking-data',
});

test('an unset provider preserves OpenAI and never reads the other provider key', () => {
  const names = [];
  const values = { OPENAI_API_KEY: openaiFixture, GROQ_API_KEY: groqFixture };
  const config = resolveCoachProvider(name => { names.push(name); return values[name]; });
  assert.deepEqual(config, { provider: 'openai', apiKey: openaiFixture, model: 'gpt-4.1-mini' });
  assert.deepEqual(names, ['COACH_AI_PROVIDER', 'OPENAI_API_KEY', 'OPENAI_MODEL']);
});

test('Groq explicitly selects its own server key and configurable model', () => {
  const names = [];
  const values = { COACH_AI_PROVIDER: ' Groq ', GROQ_API_KEY: groqFixture,
    GROQ_MODEL: ' meta-llama/llama-4-scout-17b-16e-instruct ', OPENAI_API_KEY: openaiFixture,
    OPENAI_MODEL: 'gpt-unrelated' };
  const config = resolveCoachProvider(name => { names.push(name); return values[name]; });
  assert.deepEqual(config, { provider: 'groq', apiKey: groqFixture,
    model: 'meta-llama/llama-4-scout-17b-16e-instruct' });
  assert.deepEqual(names, ['COACH_AI_PROVIDER', 'GROQ_API_KEY', 'GROQ_MODEL']);
  assert.equal(resolveCoachProvider(read({ COACH_AI_PROVIDER: 'groq', GROQ_API_KEY: groqFixture })).model,
    'llama-3.3-70b-versatile');
  assert.equal(resolveCoachProvider(read({ OPENAI_API_KEY: openaiFixture, OPENAI_MODEL: 'gpt-4.1' })).model,
    'gpt-4.1');
  const smallModel = resolveCoachProvider(read({ COACH_AI_PROVIDER: 'groq', GROQ_API_KEY: groqFixture,
    GROQ_MODEL: 'llama-3.1-8b-instant' }));
  assert.equal(JSON.parse(buildCoachProviderRequest(smallModel, input).init.body).model,
    'llama-3.1-8b-instant');
});

test('invalid selection or missing selected credentials fails without a fallback', () => {
  for (const provider of ['', 'unsupported', 'https://attacker.example', 'groq\nopenai'])
    assert.equal(resolveCoachProvider(read({ COACH_AI_PROVIDER: provider,
      OPENAI_API_KEY: openaiFixture, GROQ_API_KEY: groqFixture })), null);
  assert.equal(resolveCoachProvider(read({ COACH_AI_PROVIDER: 'groq', OPENAI_API_KEY: openaiFixture })), null);
  assert.equal(resolveCoachProvider(read({ GROQ_API_KEY: groqFixture })), null);
  for (const key of ['', ' ', 'server\nsecret', 'server secret'])
    assert.equal(resolveCoachProvider(read({ COACH_AI_PROVIDER: 'groq', GROQ_API_KEY: key,
      OPENAI_API_KEY: openaiFixture })), null);
});

test('invalid model configuration is rejected before any provider request', () => {
  for (const model of ['', ' ', 'model\nname', 'x'.repeat(129), '{"url":"https://attacker.example"}'])
    assert.equal(resolveCoachProvider(read({ COACH_AI_PROVIDER: 'groq', GROQ_API_KEY: groqFixture,
      GROQ_MODEL: model })), null);
});

test('the consent header must match the server, with legacy requests allowed only for OpenAI', () => {
  const defaultProvider = resolveCoachProvider(read({ OPENAI_API_KEY: openaiFixture })).provider;
  assert.equal(coachProviderHeaderMatches(defaultProvider, null), true);
  assert.equal(coachProviderHeaderMatches(defaultProvider, undefined), true);
  assert.equal(coachProviderHeaderMatches(defaultProvider, 'openai'), true);
  assert.equal(coachProviderHeaderMatches(defaultProvider, 'groq'), false);
  assert.equal(coachProviderHeaderMatches('groq', 'groq'), true);
  assert.equal(coachProviderHeaderMatches('groq', 'openai'), false);
  assert.equal(coachProviderHeaderMatches('groq', null), false);
  assert.equal(coachProviderHeaderMatches('groq', undefined), false);
  for (const provider of ['openai', 'groq']) for (const header of ['', 'other', 'https://attacker.example',
    'Groq', 'groq,openai', ' groq ', 'groq\nopenai'])
    assert.equal(coachProviderHeaderMatches(provider, header), false);
});

test('OpenAI request preserves the existing Responses API privacy and output options', () => {
  const config = resolveCoachProvider(read({ OPENAI_API_KEY: openaiFixture }));
  const request = buildCoachProviderRequest(config, input);
  assert.equal(request.url, 'https://api.openai.com/v1/responses');
  assert.equal(request.init.method, 'POST');
  assert.equal(request.init.headers.Authorization, `Bearer ${openaiFixture}`);
  const body = JSON.parse(request.init.body);
  assert.deepEqual(Object.keys(body).sort(), ['input', 'instructions', 'max_output_tokens', 'model', 'store']);
  assert.equal(body.model, 'gpt-4.1-mini');
  assert.equal(body.store, false);
  assert.equal(body.max_output_tokens, 220);
  assert.match(body.instructions, /en français en 2 à 5 phrases/);
  assert.match(body.instructions, /Ne prétends pas être un professionnel de santé/);
  assert.deepEqual(body.input, [
    { role: 'user', content: 'Je veux une pause courte.' },
    { role: 'assistant', content: 'Choisis une transition adaptée.' },
    { role: 'user', content: input.question },
  ]);
});

test('Groq uses the fixed chat endpoint and bounded non-streaming conversation', () => {
  const config = resolveCoachProvider(read({ COACH_AI_PROVIDER: 'groq', GROQ_API_KEY: groqFixture,
    COACH_AI_BASE_URL: 'https://attacker.example', GROQ_BASE_URL: 'https://attacker.example' }));
  const request = buildCoachProviderRequest(config, input);
  const body = JSON.parse(request.init.body);
  const openaiBody = JSON.parse(buildCoachProviderRequest(
    resolveCoachProvider(read({ OPENAI_API_KEY: openaiFixture })), input).init.body);
  assert.equal(request.url, 'https://api.groq.com/openai/v1/chat/completions');
  assert.equal(request.init.method, 'POST');
  assert.equal(request.init.headers.Authorization, `Bearer ${groqFixture}`);
  assert.deepEqual(Object.keys(body).sort(), ['max_completion_tokens', 'messages', 'model', 'stream']);
  assert.equal(body.model, 'llama-3.3-70b-versatile');
  assert.equal(body.max_completion_tokens, 220);
  assert.equal(body.stream, false);
  assert.deepEqual(body.messages, [{ role: 'system', content: openaiBody.instructions }, ...openaiBody.input]);
});

test('client claims and server secrets cannot enter the provider body', () => {
  for (const provider of ['openai', 'groq']) {
    const config = resolveCoachProvider(read({ COACH_AI_PROVIDER: provider,
      GROQ_API_KEY: groqFixture, OPENAI_API_KEY: openaiFixture }));
    const request = buildCoachProviderRequest(config, input);
    for (const excluded of [groqFixture, openaiFixture, 'untrusted-key', 'private-tracking-data',
      'attacker.example', 'untrusted-model']) assert.equal(request.init.body.includes(excluded), false);
    assert.deepEqual(Object.keys(input).sort(), ['history', 'question']);
  }
});

test('each provider extracts only its own actual bounded text response', () => {
  const openai = { output: [{ content: [{ type: 'output_text', text: ' Une étape à la fois. ' }] }] };
  const groq = { choices: [{ message: { role: 'assistant', content: ' Une étape à la fois. ' } }] };
  assert.equal(extractCoachProviderAnswer(openai, 'openai'), 'Une étape à la fois.');
  assert.equal(extractCoachProviderAnswer(groq, 'groq'), 'Une étape à la fois.');
  assert.equal(extractCoachProviderAnswer(openai, 'groq'), '');
  assert.equal(extractCoachProviderAnswer(groq, 'openai'), '');
  for (const malformed of [null, [], {}, { choices: [] }, { choices: [null] },
    { choices: [{ message: { role: 'user', content: 'prompt echo' } }] },
    { choices: [{ message: { content: 'missing assistant role' } }] },
    { choices: [{ message: { role: 'assistant', content: null } }] },
    { choices: [{ message: { role: 'assistant', refusal: 'not text' } }] },
    { choices: [{ message: { role: 'assistant', content: [{ text: 'unexpected shape' }] } }] },
    { choices: [{ message: { role: 'assistant', content: ' ' } }] },
    { choices: [{ message: { role: 'assistant', content: 'x'.repeat(4001) } }] }])
    assert.equal(extractCoachProviderAnswer(malformed, 'groq'), '');
});
