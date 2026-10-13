-- Configure limits from the provider dashboard before activation. No invented free-tier defaults.
create table public.coach_capacity_limits (
    scope text primary key check (scope in ('groq', 'openai')),
    requests_per_minute integer not null check (requests_per_minute between 1 and 1000000),
    requests_per_day integer not null check (requests_per_day between 1 and 100000000),
    tokens_per_minute bigint not null check (tokens_per_minute between 1 and 1000000000000),
    tokens_per_day bigint not null check (tokens_per_day between 1 and 1000000000000),
    max_concurrent integer not null check (max_concurrent between 1 and 1000),
    enabled boolean not null default false,
    blocked_until timestamptz
);
create table public.coach_capacity_calls (
    id uuid primary key default gen_random_uuid(),
    scope text not null references public.coach_capacity_limits(scope),
    started_at timestamptz not null,
    lease_until timestamptz not null,
    finished_at timestamptz,
    reserved_tokens integer not null check (reserved_tokens between 1 and 1000000),
    actual_tokens integer check (actual_tokens between 0 and 1000000000),
    outcome text not null default 'reserved' check (outcome in ('reserved', 'success', 'failed', 'rate_limited'))
);
create index coach_capacity_window on public.coach_capacity_calls(scope, started_at);
alter table public.coach_capacity_limits enable row level security;
alter table public.coach_capacity_calls enable row level security;
revoke all on public.coach_capacity_limits, public.coach_capacity_calls from public, anon, authenticated;
grant select, insert, update, delete on public.coach_capacity_limits, public.coach_capacity_calls to service_role;

-- Serialize admission across ALL Edge Function instances using a database row lock.
-- Rolling 60-second / 24-hour windows avoid twice the quota at a bucket boundary.
create function public.reserve_coach_capacity(capacity_scope text, token_reservation integer)
returns jsonb language plpgsql security definer set search_path = '' as $$
declare
    limits public.coach_capacity_limits%rowtype;
    moment timestamptz;
    minute_requests bigint;
    day_requests bigint;
    minute_tokens bigint;
    day_tokens bigint;
    active_count bigint;
    identifier uuid;
    earliest timestamptz;
    reason text;
    delay_seconds integer;
begin
    if token_reservation is null or token_reservation not between 1 and 1000000 then
        raise exception 'INVALID_TOKEN_RESERVATION';
    end if;
    select * into limits from public.coach_capacity_limits where scope = capacity_scope for update;
    if not found or not limits.enabled then
        return jsonb_build_object('allowed', false, 'error', 'CAPACITY_NOT_CONFIGURED', 'retryAfter', 60);
    end if;
    moment := clock_timestamp();
    if limits.blocked_until > moment then
        return jsonb_build_object('allowed', false, 'error', 'PROVIDER_COOLDOWN',
            'retryAfter', greatest(1, ceil(extract(epoch from limits.blocked_until - moment))::integer));
    end if;
    if token_reservation > least(limits.tokens_per_minute, limits.tokens_per_day) then
        return jsonb_build_object('allowed', false, 'error', 'REQUEST_EXCEEDS_CAPACITY', 'retryAfter', 0);
    end if;
    -- Idle data needs an operator-scheduled purge as documented; no messages or user IDs stored here.
    delete from public.coach_capacity_calls where scope = capacity_scope and started_at < moment - interval '48 hours';
    select count(*) filter (where started_at > moment - interval '60 seconds'), count(*),
        coalesce(sum(greatest(reserved_tokens, coalesce(actual_tokens, reserved_tokens)))
            filter (where started_at > moment - interval '60 seconds'), 0),
        coalesce(sum(greatest(reserved_tokens, coalesce(actual_tokens, reserved_tokens))), 0)
        into minute_requests, day_requests, minute_tokens, day_tokens
        from public.coach_capacity_calls where scope = capacity_scope and started_at > moment - interval '24 hours';
    if day_requests >= limits.requests_per_day or day_tokens + token_reservation > limits.tokens_per_day then
        reason := 'GLOBAL_DAILY_CAPACITY';
        select min(started_at) + interval '24 hours' into earliest from public.coach_capacity_calls
            where scope = capacity_scope and started_at > moment - interval '24 hours';
    elsif minute_requests >= limits.requests_per_minute or minute_tokens + token_reservation > limits.tokens_per_minute then
        reason := 'GLOBAL_MINUTE_CAPACITY';
        select min(started_at) + interval '60 seconds' into earliest from public.coach_capacity_calls
            where scope = capacity_scope and started_at > moment - interval '60 seconds';
    else
        select count(*), min(lease_until) into active_count, earliest from public.coach_capacity_calls
            where scope = capacity_scope and finished_at is null and lease_until > moment;
        if active_count >= limits.max_concurrent then reason := 'GLOBAL_CONCURRENCY_CAPACITY'; end if;
    end if;
    if reason is not null then
        delay_seconds := greatest(1, ceil(extract(epoch from earliest - moment))::integer);
        return jsonb_build_object('allowed', false, 'error', reason, 'retryAfter', delay_seconds);
    end if;
    insert into public.coach_capacity_calls(scope, started_at, lease_until, reserved_tokens)
        values (capacity_scope, moment, moment + interval '45 seconds', token_reservation) returning id into identifier;
    return jsonb_build_object('allowed', true, 'leaseId', identifier);
end;
$$;

-- Failures still consume provider capacity. Finishing a lease releases only its concurrency slot.
create function public.finish_coach_capacity(lease_id uuid, result_outcome text,
    reported_tokens integer default null, cooldown_seconds integer default 0)
returns void language plpgsql security definer set search_path = '' as $$
declare capacity_scope text;
begin
    if result_outcome not in ('success', 'failed', 'rate_limited') or result_outcome is null
        or reported_tokens < 0 or reported_tokens > 1000000000
        or cooldown_seconds is null or cooldown_seconds not between 0 and 86400 then
        raise exception 'INVALID_CAPACITY_RESULT';
    end if;
    select scope into capacity_scope from public.coach_capacity_calls where id = lease_id;
    if not found then return; end if;
    perform 1 from public.coach_capacity_limits where scope = capacity_scope for update;
    update public.coach_capacity_calls set finished_at = clock_timestamp(), outcome = result_outcome,
        actual_tokens = reported_tokens where id = lease_id and finished_at is null;
    if found and cooldown_seconds > 0 then
        update public.coach_capacity_limits set blocked_until = greatest(blocked_until,
            clock_timestamp() + make_interval(secs => cooldown_seconds)) where scope = capacity_scope;
    end if;
end;
$$;

create function public.coach_capacity_status(capacity_scope text)
returns jsonb language sql security definer set search_path = '' as $$
    select jsonb_build_object('scope', limits.scope, 'enabled', limits.enabled,
        'limits', jsonb_build_object('requestsPerMinute', limits.requests_per_minute,
            'requestsPerDay', limits.requests_per_day, 'tokensPerMinute', limits.tokens_per_minute,
            'tokensPerDay', limits.tokens_per_day, 'maxConcurrent', limits.max_concurrent),
        'blockedUntil', limits.blocked_until,
        'requestsLastMinute', count(calls.id) filter (where calls.started_at > clock_timestamp() - interval '60 seconds'),
        'requestsLast24Hours', count(calls.id),
        'reservedOrReportedTokensLastMinute', coalesce(sum(greatest(calls.reserved_tokens,
            coalesce(calls.actual_tokens, calls.reserved_tokens))) filter (where calls.started_at > clock_timestamp() - interval '60 seconds'), 0),
        'reservedOrReportedTokensLast24Hours', coalesce(sum(greatest(calls.reserved_tokens,
            coalesce(calls.actual_tokens, calls.reserved_tokens))), 0),
        'activeCalls', count(calls.id) filter (where calls.finished_at is null and calls.lease_until > clock_timestamp()),
        'reportedTokensLast24Hours', coalesce(sum(calls.actual_tokens), 0),
        'estimatesExceeded', count(calls.id) filter (where calls.actual_tokens > calls.reserved_tokens))
    from public.coach_capacity_limits limits left join public.coach_capacity_calls calls
        on calls.scope = limits.scope and calls.started_at > clock_timestamp() - interval '24 hours'
    where limits.scope = capacity_scope group by limits.scope;
$$;

-- Private operator indicators; these are SQL-readable signals, not external notifications.
create function public.coach_capacity_health(capacity_scope text)
returns jsonb language plpgsql security definer set search_path = '' as $$
declare report jsonb; pressure numeric;
begin
    report := public.coach_capacity_status(capacity_scope);
    if report is null then return jsonb_build_object('configured', false); end if;
    pressure := greatest(
        (report->>'requestsLastMinute')::numeric / (report->'limits'->>'requestsPerMinute')::numeric,
        (report->>'requestsLast24Hours')::numeric / (report->'limits'->>'requestsPerDay')::numeric,
        (report->>'reservedOrReportedTokensLastMinute')::numeric / (report->'limits'->>'tokensPerMinute')::numeric,
        (report->>'reservedOrReportedTokensLast24Hours')::numeric / (report->'limits'->>'tokensPerDay')::numeric,
        (report->>'activeCalls')::numeric / (report->'limits'->>'maxConcurrent')::numeric);
    return report || jsonb_build_object('configured', true, 'highestUtilizationPercent', round(pressure * 100, 2),
        'warningLevel', case when not (report->>'enabled')::boolean then 'disabled'
            when (report->>'blockedUntil')::timestamptz > clock_timestamp() then 'provider_cooldown'
            when pressure >= 0.95 then 'critical' when pressure >= 0.80 then 'warning' else 'normal' end);
end;
$$;

create function public.prune_coach_capacity()
returns void language sql security definer set search_path = '' as $$
    delete from public.coach_capacity_calls where started_at < clock_timestamp() - interval '48 hours';
$$;
revoke all on function public.reserve_coach_capacity(text, integer),
    public.finish_coach_capacity(uuid, text, integer, integer), public.coach_capacity_status(text),
    public.prune_coach_capacity(), public.coach_capacity_health(text) from public, anon, authenticated;
grant execute on function public.reserve_coach_capacity(text, integer),
    public.finish_coach_capacity(uuid, text, integer, integer), public.coach_capacity_status(text),
    public.prune_coach_capacity(), public.coach_capacity_health(text) to service_role;
