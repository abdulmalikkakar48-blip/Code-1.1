"""Real PostgreSQL admission tests in an isolated disposable container; no Supabase credentials."""
import concurrent.futures
import json
import os
from pathlib import Path
import subprocess
import time
import uuid

IMAGE = 'postgres@sha256:ca0bd484cb98bf4b24eb1010e73fb3fcbd6714d240fbc1a10eea5b7dbecb641d'
ROOT = Path(__file__).resolve().parents[1]
NAME = 'selkie-capacity-test-' + uuid.uuid4().hex[:12]
ENV = {k: v for k, v in os.environ.items() if k not in
       ('DOCKER_HOST', 'DOCKER_CONTEXT', 'DOCKER_TLS', 'DOCKER_TLS_VERIFY', 'DOCKER_CERT_PATH')}

def docker(*args, input=None, check=True):
    return subprocess.run(['docker', '--host=unix:///var/run/docker.sock', *args], env=ENV,
                          input=input, text=True, capture_output=True, check=check)

def sql(statement, check=True):
    return docker('exec', '-i', NAME, 'psql', '-U', 'postgres', '-v', 'ON_ERROR_STOP=1', '-qAt',
                  input=statement, check=check)

def reset(rpm=100, rpd=1000, tpm=1000000, tpd=10000000, concurrent=100):
    sql(f"""truncate public.coach_capacity_calls;
        delete from public.coach_capacity_limits;
        insert into public.coach_capacity_limits values
        ('groq',{rpm},{rpd},{tpm},{tpd},{concurrent},true,null);""")

def reserve(tokens=100):
    return json.loads(sql(f"set role service_role; select public.reserve_coach_capacity('groq',{tokens});").stdout)

def finish(lease, outcome='success', actual='null', cooldown=0):
    sql(f"set role service_role; select public.finish_coach_capacity('{lease}','{outcome}',{actual},{cooldown});")

def status():
    return json.loads(sql("set role service_role; select public.coach_capacity_status('groq');").stdout)

def parallel_requests(count=80):
    with concurrent.futures.ThreadPoolExecutor(max_workers=16) as pool:
        return list(pool.map(lambda _: reserve(), range(count)))

def run():
    docker('image', 'inspect', IMAGE) if docker('image', 'inspect', IMAGE, check=False).returncode == 0 else docker('pull', IMAGE)
    docker('run', '-d', '--name', NAME, '--network', 'none', '-e', 'POSTGRES_HOST_AUTH_METHOD=trust', IMAGE)
    try:
        for _ in range(60):
            if docker('exec', NAME, 'pg_isready', '-U', 'postgres', check=False).returncode == 0:
                break
            time.sleep(0.5)
        else:
            raise RuntimeError('PostgreSQL did not become ready')
        sql('create role anon nologin; create role authenticated nologin; create role service_role nologin bypassrls;')
        sql((ROOT / 'supabase/migrations/20261013_coach_capacity.sql').read_text())
        assert reserve()['error'] == 'CAPACITY_NOT_CONFIGURED'
        assert json.loads(sql("set role service_role; select public.coach_capacity_health('groq');").stdout)=={'configured':False}
        reset(); sql("update public.coach_capacity_limits set enabled=false;")
        assert reserve()['error'] == 'CAPACITY_NOT_CONFIGURED'
        print('PASS absent/disabled configuration fails closed', flush=True)

        reset(rpm=3)
        for _ in range(3):
            result=reserve(); assert result['allowed']; finish(result['leaseId'])
        assert reserve()['error']=='GLOBAL_MINUTE_CAPACITY'
        assert status()['requestsLastMinute']==3
        print('PASS completed calls still count against minute budget', flush=True)

        reset(rpm=100,rpd=2)
        for _ in range(2): assert reserve()['allowed']
        assert reserve()['error']=='GLOBAL_DAILY_CAPACITY'
        sql("update public.coach_capacity_calls set started_at=clock_timestamp()-interval '24 hours 1 second';")
        assert reserve()['allowed']
        print('PASS daily rolling window expires', flush=True)

        reset(tpm=250)
        assert reserve()['allowed'] and reserve()['allowed']
        assert reserve()['error']=='GLOBAL_MINUTE_CAPACITY'
        assert reserve(251)['error']=='REQUEST_EXCEEDS_CAPACITY'
        reset(tpd=250)
        assert reserve()['allowed'] and reserve()['allowed']
        assert reserve()['error']=='GLOBAL_DAILY_CAPACITY'
        print('PASS token budgets and oversized single request', flush=True)

        reset(concurrent=2)
        first,second=reserve(),reserve()
        assert reserve()['error']=='GLOBAL_CONCURRENCY_CAPACITY'
        finish(first['leaseId'])
        assert reserve()['allowed']
        sql("update public.coach_capacity_calls set lease_until=clock_timestamp()-interval '1 second';")
        assert reserve()['allowed']
        print('PASS concurrency release and expired worker lease', flush=True)

        reset(); result=reserve(); finish(result['leaseId'],'rate_limited','null',120)
        finish(result['leaseId'],'success',100,0)
        assert reserve()['error']=='PROVIDER_COOLDOWN'
        assert status()['requestsLast24Hours']==1 and status()['activeCalls']==0
        sql("update public.coach_capacity_limits set blocked_until=clock_timestamp()-interval '1 second';")
        assert reserve()['allowed']
        print('PASS shared provider cooldown and idempotent completion', flush=True)

        reset(); result=reserve(); finish(result['leaseId'],'success',300)
        assert status()['reservedOrReportedTokensLast24Hours']==300 and status()['estimatesExceeded']==1
        sql("update public.coach_capacity_calls set started_at=clock_timestamp()-interval '49 hours'; select public.prune_coach_capacity();")
        assert status()['requestsLast24Hours']==0
        print('PASS reported usage above estimate and scheduled purge', flush=True)

        reset(rpm=10)
        for _ in range(8):
            result=reserve(); finish(result['leaseId'])
        health=json.loads(sql("set role service_role; select public.coach_capacity_health('groq');").stdout)
        assert health['warningLevel']=='warning' and health['highestUtilizationPercent']==80
        for _ in range(2):
            result=reserve(); finish(result['leaseId'])
        assert json.loads(sql("set role service_role; select public.coach_capacity_health('groq');").stdout)['warningLevel']=='critical'
        print('PASS operator warning at 80% and critical threshold at 95%', flush=True)

        for role in ('anon','authenticated'):
            assert sql(f"set role {role}; select public.reserve_coach_capacity('groq',100);",check=False).returncode != 0
            assert sql(f"set role {role}; select * from public.coach_capacity_limits;",check=False).returncode != 0
            assert sql(f"set role {role}; select public.coach_capacity_status('groq');",check=False).returncode != 0
            assert sql(f"set role {role}; select public.coach_capacity_health('groq');",check=False).returncode != 0
        print('PASS clients cannot set, inspect or reserve global budget', flush=True)

        reset(rpm=7)
        results=parallel_requests()
        assert sum(r['allowed'] for r in results)==7 and status()['requestsLast24Hours']==7
        reset(concurrent=3)
        results=parallel_requests()
        assert sum(r['allowed'] for r in results)==3 and status()['activeCalls']==3
        print('PASS 80 concurrent attempts: exact minute cap 7; exact concurrency cap 3', flush=True)
    finally:
        docker('rm','-f','-v',NAME,check=False)

if __name__ == '__main__':
    run()
