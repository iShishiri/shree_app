-- Shree: access policies for the PROTOTYPE phase.
-- Your tables have Row Level Security on but no policies yet, so the app's public
-- (publishable) key currently can't read or write anything. This grants access so we
-- can prove the app works end to end.
--
-- SECURITY NOTE: these policies let anyone holding the publishable key read/write.
-- That key ships inside the app, so this is acceptable only while the data is
-- placeholder/test data. Before any real private notes, photos, or location go in,
-- we switch to Supabase Auth + "authenticated only" policies, with auto sign-in so
-- Reetu never sees a login screen.

do $$
declare t text;
begin
  foreach t in array array[
    'users','daily_notes','letters','timeline','memories',
    'reminders','todos','messages','lists','list_items','location'
  ]
  loop
    execute format('drop policy if exists shree_all on %I;', t);
    execute format(
      'create policy shree_all on %I for all to anon, authenticated using (true) with check (true);',
      t
    );
  end loop;
end $$;
