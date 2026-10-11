-- Complément à 20261010_maree.sql. Ne modifie pas les données des autres tables.
alter table public.coach_usage
    add column if not exists last_requested_at timestamptz;

create or replace function public.consume_coach_credit()
returns integer
language plpgsql security definer set search_path = ''
as $$
declare
    uid uuid := (select auth.uid());
    amount integer;
    subscriber boolean;
    previous_request timestamptz;
begin
    if uid is null then raise exception 'AUTH_REQUIRED'; end if;
    insert into public.coach_usage(user_id, questions_used) values (uid, 0)
        on conflict (user_id) do nothing;
    select questions_used, last_requested_at into amount, previous_request
        from public.coach_usage where user_id = uid for update;
    if previous_request is not null and previous_request > clock_timestamp() - interval '5 seconds' then
        raise exception 'RATE_LIMITED';
    end if;
    select coalesce(premium, false) into subscriber from public.profiles where user_id = uid;
    if not coalesce(subscriber, false) and amount >= 100 then raise exception 'FREE_QUOTA_EXCEEDED'; end if;
    update public.coach_usage set questions_used = amount + 1, last_requested_at = clock_timestamp()
        where user_id = uid;
    return case when coalesce(subscriber, false) then -1 else 99 - amount end;
end;
$$;
revoke all on function public.consume_coach_credit() from public, anon;
grant execute on function public.consume_coach_credit() to authenticated;

create or replace function public.refund_coach_credit(target_user uuid)
returns void
language plpgsql security definer set search_path = ''
as $$
begin
    update public.coach_usage set questions_used = greatest(0, questions_used - 1)
        where user_id = target_user;
end;
$$;
revoke all on function public.refund_coach_credit(uuid) from public, anon, authenticated;
grant execute on function public.refund_coach_credit(uuid) to service_role;
