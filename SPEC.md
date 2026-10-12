# Shree — spec v1

A private app for two people: Shishir Dahal and Reetu Shrestha. "Shree" (श्री) is their names joined, and a letter in Nepali/Sanskrit. It is a surprise gift, then a long-term app Shishir keeps adding to.

- **Target:** one month from 2026-10-11 (around mid-November 2026).
- **Her:** Android phone, student preparing for Loksewa, forgets things a lot, loves sentimental things (letters, old photos, songs), soft but bold-and-bright aesthetic.
- **Him:** iPhone, never built an app before (Claude writes the code).

## What each person uses

| | Reetu (Android) | Shishir (iPhone) |
|---|---|---|
| Surface | The Shree Android app | A private Shree web page (add to home screen) |
| Can | See notes, memories, widgets, reminders, chat; take snaps; share location | Write notes and letters, record voice reminders, add coupons and memories, edit lists, chat, see her location |

## Stack (all free, no subscription)

- **App:** native Android, Kotlin + Jetpack Compose (needed for home-screen widgets).
- **Admin page:** a small web app so Shishir can run it from his iPhone.
- **Backend:** Supabase free tier — Postgres database, auth, storage, and Realtime.
  - Limits: 1 GB file storage, 5 GB downloads/month, 500 MB database. Fine for two people and photos.
  - No credit card. Sign up with any email (not rankmetopseo).
- **Photos:** stored in Supabase storage, compressed before upload. **No video for now.**
- **Instant notifications:** Firebase Cloud Messaging (free). This is the only piece that uses Shishir's personal Google account.
- **Distribution:** sideloaded APK (no Play Store, no $25 fee). Shishir installs/updates by sending a file. Google Play testing track is an optional future convenience.

## Design

- **Style:** "Pop" — white sticker cards with a deep-blue outline and a hard sky-blue offset shadow. Playful, not flat.
- **Colours (their colours, blue + sky blue):** sky blue `#3FB4F3` (accents, shadows), deep blue `#15528A` (outlines, text), soft `#E2F4FD`, canvas `#F2FAFE`, white cards.
- **Font:** Baloo 2 (rounded) for the app and the श्री icon.
- **Icon:** श्री in Baloo 2, deep blue on a white sticker tile with a sky-blue shadow. Drawn as real artwork so it is crisp at every size.

## Features

### Tier 1 — ready on gift day
- **Daily note from Shishir** — a new message each day, pre-written and scheduled ahead. Fires locally on her phone so it works with no signal.
- **"Open when…" letters** — letters that unlock for a moment (sad, can't sleep, missing you, before an exam).
- **Our timeline** — the relationship as a scroll of photos + captions, with a day counter and countdowns to anniversaries/birthdays.
- **Memory jar** — small moments either of you drop in; the app surfaces one.
- **Reminders** — text plus Shishir's recorded voice (tap-to-play on the notification; voice plays when she opens it).
- **To-do with a big home-screen widget** — the thing that fixes her forgetting; visible without opening the app.
- **"On this day" widget** — picks one item at midnight and keeps it all day. Shows a memory from this date if there is one, else a random photo of them. One per day so it never goes stale.
- **Live chat** — real-time 1:1 chat (Supabase Realtime) with online/typing indicators; offline messages arrive via FCM push.
- **Shared lists** — movies to watch, places to go, activities together, gift ideas; either person adds items.

### Tier 2 — first updates
- Mood check-in (one tap; nudges Shishir on a rough streak).
- Snap of the day (a shared photo prompt).
- Photo wall widget.

### Tier 3 — later
- One-way location (he sees her); opt-in toggle, with Android's "all the time" permission.
- Loksewa study tools (exam countdown, syllabus checklist, daily study timer).

## Photo caching (offline-first)
When a photo is added, her app downloads it in the background into the app's private storage — she never taps "download." Photos then open instantly and work with no signal. New photos are pre-fetched so the timeline and widget are always ready. Images are compressed on upload to stay within the 1 GB free storage (room for 1000+ photos).

## Data model (first pass, Supabase tables)
- `users` — the two of them.
- `daily_notes` — text, scheduled_date, author.
- `letters` — title, body, unlock_trigger, optional photo.
- `timeline` — date, photo_url, caption.
- `memories` — text or photo_url, created_by, created_at.
- `reminders` — text, voice_url, remind_at, repeat.
- `todos` — text, done, due.
- `messages` — chat: sender, body, photo_url, sent_at (Realtime).
- `lists` — name, kind (movies/places/activities/gifts); `list_items` — list_id, text, done, added_by.
- `location` (tier 3) — lat, lng, updated_at.

## Accounts / setup checklist
- [ ] Supabase project (Shishir's own email).
- [ ] Firebase project for FCM (Shishir's personal Google account).
- [ ] Android Studio + SDK on the Windows PC (installing).
- [ ] App icon artwork (श्री, Baloo 2, Pop sky-blue).
- [ ] ~20 min on her phone on gift day to install the APK and grant permissions.

## Open questions
- App display name on her phone: "Shree", or something sweeter?
- Default daily-note time (morning?).
- Anniversary / birthday dates for the countdowns.
- A few starter letters, notes and list items to seed before gift day.
