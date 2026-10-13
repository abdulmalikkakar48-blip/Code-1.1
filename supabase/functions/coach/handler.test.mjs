import test from 'node:test';
import assert from 'node:assert/strict';
import { createCoachHandler } from './handler.ts';

const leaseId='00000000-0000-0000-0000-000000000001';
function fixture(overrides={}) {
  const calls=[]; const warnings=[];
  const env={SUPABASE_URL:'https://fixture.supabase.co', SUPABASE_ANON_KEY:'synthetic-public',
    SUPABASE_SERVICE_ROLE_KEY:'synthetic-service', COACH_ALLOWED_EMAILS:'fixture@example.invalid',
    COACH_AI_PROVIDER:'groq', GROQ_API_KEY:'synthetic-groq'};
  const deps={
    readEnv:name=>env[name], warn:code=>warnings.push(code),
    createClient:(url,key)=>({
      auth:{getUser:async()=>({data:{user:{id:'synthetic-user',email:'fixture@example.invalid',email_confirmed_at:'2026-10-13'}},error:null})},
      rpc:async(name,args)=>{
        calls.push({name,args});
        if (name==='consume_coach_credit') return overrides.creditError
          ? {data:null,error:{message:overrides.creditError}} : {data:99,error:null};
        if (name==='reserve_coach_capacity') return {data:overrides.admission ?? {allowed:true,leaseId},error:overrides.databaseError ? {message:'private database detail'}:null};
        return {data:null,error:overrides.finishError && name==='finish_coach_capacity' ? {message:'private error'}:null};
      },
    }),
    fetch:async(url,init)=>{
      calls.push({name:'fetch',url,init});
      if (overrides.throwFetch) throw new Error('private provider detail');
      return overrides.response ?? new Response(JSON.stringify({choices:[{message:{role:'assistant',content:'Prépare une pause courte.'}}],usage:{total_tokens:123}}),{status:200});
    },
  };
  const request=new Request('https://fixture.invalid/coach',{method:'POST',headers:{Authorization:'Bearer synthetic-token','X-Selkie-Coach-Provider':overrides.header ?? 'groq'},
    body:JSON.stringify({question:'Comment réduire TikTok ?'})});
  return {run:()=>createCoachHandler(deps)(request),calls,warnings};
}

test('success reserves distributed capacity before provider and completes without refund',async()=>{
  const f=fixture(); const response=await f.run();
  assert.equal(response.status,200);
  assert.deepEqual(await response.json(),{answer:'Prépare une pause courte.',questionsRemaining:99});
  assert.deepEqual(f.calls.map(c=>c.name),['consume_coach_credit','reserve_coach_capacity','fetch','finish_coach_capacity']);
  assert.equal(f.calls[1].args.capacity_scope,'groq');
  assert.ok(f.calls[1].args.token_reservation>220);
  assert.equal(f.calls.at(-1).args.reported_tokens,123);
});
test('global minute/day/concurrency/cooldown denial never calls provider and requests personal refund',async()=>{
  for(const error of ['GLOBAL_MINUTE_CAPACITY','GLOBAL_DAILY_CAPACITY','GLOBAL_CONCURRENCY_CAPACITY','PROVIDER_COOLDOWN']){
    const f=fixture({admission:{allowed:false,error,retryAfter:120}}); const response=await f.run();
    assert.equal(response.status,error==='GLOBAL_DAILY_CAPACITY'?503:429);
    assert.equal(response.headers.get('retry-after'),'120');
    assert.equal((await response.json()).error,error);
    assert.deepEqual(f.calls.map(c=>c.name),['consume_coach_credit','reserve_coach_capacity','refund_coach_credit']);
  }
});
test('unconfigured budget, broken database and malformed lease are fail closed',async()=>{
  for(const overrides of [{admission:{allowed:false,error:'CAPACITY_NOT_CONFIGURED',retryAfter:60}},
    {databaseError:true},{admission:{allowed:true,leaseId:'invalid'}}]){
    const f=fixture(overrides); assert.equal((await f.run()).status,503);
    assert.equal(f.calls.some(c=>c.name==='fetch'),false);
    assert.equal(f.calls.at(-1).name,'refund_coach_credit');
  }
});
test('an input exceeding the configured token budget is rejected before AI',async()=>{
  const f=fixture({admission:{allowed:false,error:'REQUEST_EXCEEDS_CAPACITY',retryAfter:0}});
  assert.equal((await f.run()).status,413);
  assert.equal(f.calls.some(c=>c.name==='fetch'),false);
});
test('provider 429 finishes lease with shared cooldown and personal credit refund',async()=>{
  const f=fixture({response:new Response('private provider detail',{status:429,headers:{'retry-after':'90'}})});
  const response=await f.run(); assert.equal(response.status,429);
  assert.equal(response.headers.get('retry-after'),'90');
  const completed=f.calls.find(c=>c.name==='finish_coach_capacity');
  assert.equal(completed.args.result_outcome,'rate_limited');
  assert.equal(completed.args.cooldown_seconds,90);
  assert.equal(f.calls.at(-1).name,'refund_coach_credit');
  assert.equal((await response.text()).includes('private provider detail'),false);
});
test('network error and empty response release slot and request personal refund',async()=>{
  for(const overrides of [{throwFetch:true},{response:new Response('{}',{status:200})}]){
    const f=fixture(overrides); assert.equal((await f.run()).status,502);
    assert.equal(f.calls.find(c=>c.name==='finish_coach_capacity').args.result_outcome,'failed');
    assert.equal(f.calls.at(-1).name,'refund_coach_credit');
  }
});
test('personal quota denial consumes no shared capacity and calls no provider',async()=>{
  const f=fixture({creditError:'FREE_QUOTA_EXCEEDED'}); assert.equal((await f.run()).status,402);
  assert.deepEqual(f.calls.map(c=>c.name),['consume_coach_credit']);
});
test('provider mismatch precedes personal and global quotas',async()=>{
  const f=fixture({header:'openai'}); assert.equal((await f.run()).status,503);
  assert.equal(f.calls.length,0);
});
test('accounting failure logs only an operational code, never prompts or vendor details',async()=>{
  const f=fixture({finishError:true}); assert.equal((await f.run()).status,200);
  assert.deepEqual(f.warnings,['COACH_CAPACITY_COMPLETION_FAILED']);
});
