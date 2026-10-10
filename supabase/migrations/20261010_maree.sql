-- À exécuter dans le projet Supabase Marée après sauvegarde de toute base existante.
-- Aucune clé de service ou d'IA ne doit être intégrée dans l'application Android.

create table if not exists public.profiles (
    user_id uuid primary key references auth.users(id) on delete cascade,
    premium boolean not null default false,
    created_at timestamptz not null default now()
);

create table if not exists public.user_state (
    user_id uuid primary key references auth.users(id) on delete cascade,
    payload jsonb not null default '{}'::jsonb,
    updated_at timestamptz not null default now()
);

create table if not exists public.coach_usage (
    user_id uuid primary key references auth.users(id) on delete cascade,
    questions_used integer not null default 0 check (questions_used >= 0)
);

alter table public.profiles enable row level security;
alter table public.user_state enable row level security;
alter table public.coach_usage enable row level security;

-- Le client peut lire son état, sans pouvoir s'accorder lui-même le premium.
drop policy if exists "own profile read" on public.profiles;
create policy "own profile read" on public.profiles
    for select to authenticated using ((select auth.uid()) = user_id);

drop policy if exists "own state read" on public.user_state;
create policy "own state read" on public.user_state
    for select to authenticated using ((select auth.uid()) = user_id);
drop policy if exists "own state create" on public.user_state;
create policy "own state create" on public.user_state
    for insert to authenticated with check ((select auth.uid()) = user_id);
drop policy if exists "own state update" on public.user_state;
create policy "own state update" on public.user_state
    for update to authenticated using ((select auth.uid()) = user_id)
    with check ((select auth.uid()) = user_id);

drop policy if exists "own usage read" on public.coach_usage;
create policy "own usage read" on public.coach_usage
    for select to authenticated using ((select auth.uid()) = user_id);

-- Crédit du coach réservé de façon atomique, même avec deux appels concurrents.
-- SECURITY DEFINER évite de donner aux utilisateurs le droit d'écrire leur quota.
create or replace function public.consume_coach_credit()
returns integer
language plpgsql
security definer
set search_path = ''
as $$
declare
    uid uuid := (select auth.uid());
    amount integer;
    subscriber boolean;
begin
    if uid is null then raise exception 'AUTH_REQUIRED'; end if;
    insert into public.coach_usage (user_id, questions_used)
    values (uid, 0) on conflict (user_id) do nothing;
    select questions_used into amount from public.coach_usage where user_id = uid for update;
    select coalesce(premium, false) into subscriber from public.profiles where user_id = uid;
    if not coalesce(subscriber, false) and amount >= 100 then
        raise exception 'FREE_QUOTA_EXCEEDED';
    end if;
    update public.coach_usage set questions_used = amount + 1 where user_id = uid;
    return case when coalesce(subscriber, false) then -1 else 99 - amount end;
end;
$$;

revoke all on function public.consume_coach_credit() from public;
revoke all on function public.consume_coach_credit() from anon;
grant execute on function public.consume_coach_credit() to authenticated;

-- Les profils seront créés au premier login par l'administrateur / une fonction dédiée.
-- Le statut premium doit être mis à jour uniquement après validation côté serveur du store.
