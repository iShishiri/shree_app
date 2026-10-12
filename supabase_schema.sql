-- Shree database schema (Supabase / Postgres)
-- Paste this into the Supabase SQL editor once the project exists.
-- Two users only (Shishir, Reetu). Row Level Security is enabled; policies are
-- added after auth is wired up, so for now tables are locked to the service role.

create extension if not exists "pgcrypto";

create table if not exists users (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  role text not null check (role in ('shishir','reetu')),
  created_at timestamptz not null default now()
);

create table if not exists daily_notes (
  id uuid primary key default gen_random_uuid(),
  body text not null,
  scheduled_date date not null,
  author text not null,
  created_at timestamptz not null default now()
);

create table if not exists letters (
  id uuid primary key default gen_random_uuid(),
  title text not null,
  body text not null,
  unlock_trigger text not null,          -- e.g. 'sad', 'cant_sleep', 'missing_you', 'before_exam'
  photo_url text,
  opened_at timestamptz,
  created_at timestamptz not null default now()
);

create table if not exists timeline (
  id uuid primary key default gen_random_uuid(),
  happened_on date not null,
  photo_url text,
  caption text,
  created_at timestamptz not null default now()
);

create table if not exists memories (
  id uuid primary key default gen_random_uuid(),
  body text,
  photo_url text,
  created_by text not null,
  created_at timestamptz not null default now()
);

create table if not exists reminders (
  id uuid primary key default gen_random_uuid(),
  body text not null,
  voice_url text,
  remind_at timestamptz not null,
  repeat text not null default 'none',   -- none/daily/weekly
  created_at timestamptz not null default now()
);

create table if not exists todos (
  id uuid primary key default gen_random_uuid(),
  body text not null,
  done boolean not null default false,
  due date,
  created_at timestamptz not null default now()
);

create table if not exists messages (
  id uuid primary key default gen_random_uuid(),
  sender text not null,
  body text,
  photo_url text,
  sent_at timestamptz not null default now()
);

create table if not exists lists (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  kind text not null check (kind in ('movies','places','activities','gifts')),
  created_at timestamptz not null default now()
);

create table if not exists list_items (
  id uuid primary key default gen_random_uuid(),
  list_id uuid not null references lists(id) on delete cascade,
  text text not null,
  done boolean not null default false,
  added_by text not null,
  created_at timestamptz not null default now()
);

-- Tier 3
create table if not exists location (
  id uuid primary key default gen_random_uuid(),
  lat double precision not null,
  lng double precision not null,
  updated_at timestamptz not null default now()
);

-- Seed the four shared lists
insert into lists (name, kind)
  select v.name, v.kind from (values
    ('Movies to watch','movies'),
    ('Places to go','places'),
    ('Things to do together','activities'),
    ('Gift ideas','gifts')
  ) as v(name, kind)
  where not exists (select 1 from lists);

-- Enable RLS on everything (policies added later, once auth is set up)
alter table users enable row level security;
alter table daily_notes enable row level security;
alter table letters enable row level security;
alter table timeline enable row level security;
alter table memories enable row level security;
alter table reminders enable row level security;
alter table todos enable row level security;
alter table messages enable row level security;
alter table lists enable row level security;
alter table list_items enable row level security;
alter table location enable row level security;
