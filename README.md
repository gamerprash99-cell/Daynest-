# Daynest

A calm, premium personal life companion: plan your day, journal how you're
feeling, track habits, and split expenses with people — all in one place,
stored entirely on your device.

## What's in this project

- **Jetpack Compose + Material 3** UI throughout, MVVM architecture, Room for
  local persistence, DataStore for preferences, Navigation Compose.
- **Planner** — tasks with notes, due dates, and priority.
- **Journal / Diary** — a quick mood-tagged entry list, plus a calendar-based
  timeline view (tap the calendar icon on the Journal tab) with a richer
  entry editor (title, mood, body).
- **Habits** — daily habits with streak tracking, a calendar heatmap view,
  and a list view.
- **Expenses** — personal transactions, categories, and group expense
  splitting (equal split, with real balance calculation and settlement
  suggestions between members).
- A **Home dashboard** pulling today's tasks, habits, and journal activity
  into one view, plus **onboarding** and **Settings**.
- Everything is local-first (Room + DataStore) — no account or internet
  connection required to use the app.

## Known remaining gaps

Being upfront about what's not finished yet, so nothing is assumed working
that isn't:

- Percentage/exact-amount/shares-based expense splits aren't implemented yet
  (only equal split).
- Gallery photo picker, voice recording, sticker attachment, and the drawing
  canvas inside the journal editor are UI-only placeholders right now.
- App Lock / biometric settings don't yet persist or actually gate app access.
- Receipt OCR scanning and home-screen widgets don't exist yet.

## Building the app

### Option A — GitHub Actions (no computer setup required)

This repo includes a GitHub Actions workflow (`.github/workflows/android.yml`)
that automatically builds a debug APK every time you push code.

**One-time setup:**
1. Create a new repository on GitHub (it can be private or public).
2. Upload this entire `Daynest` folder to it. The easiest way if you're not
   familiar with git: on the repository's GitHub page, use **Add file → Upload
   files**, drag the whole folder's contents in, and commit. (If the upload
   tool balks at folder uploads, zip is not needed — modern GitHub supports
   dragging a whole folder tree in one go.)
3. That's it — no secrets or extra setup needed. The workflow only uses your
   code; nothing is signed with a private key, so there's nothing sensitive
   to configure.

**Every time after that:**
1. Push or upload a change to the `main` (or `master`) branch.
2. Go to the **Actions** tab at the top of your repository.
3. Click the most recent workflow run (it'll show a yellow dot while running,
   then a green check when done — usually a few minutes).
4. Scroll down to **Artifacts** and click **Daynest-Debug-APK** to download a
   `.zip` containing `app-debug.apk`.
5. Unzip it, copy `app-debug.apk` to your Android phone (USB, email, a cloud
   drive — whatever's easiest), open it there, and allow "install from this
   source" when Android asks.

You can also trigger a build manually anytime without pushing new code: go to
**Actions → Android CI → Run workflow**.

If the run shows a red ✕ instead of a green check, click into it, open the
failed step, and paste the error back here (or to Claude Code) — the logs
will show exactly which file and line caused it.

### Option B — Android Studio (lets you also run on an emulator/device directly)

1. Install **Android Studio** (free, from developer.android.com/studio).
2. **Open** this `Daynest` folder as a project and let Gradle sync.
3. Click the green **Run ▶** button with an emulator or your phone (USB
   debugging enabled) selected.

### Option C — Claude Code

If you have Android Studio (and its SDK) installed locally, Claude Code can
open this same project, run the real build, read actual compiler errors, and
fix them directly — useful if a build ever breaks in a way that needs more
than the CI log to diagnose.

## Project structure

```
.github/workflows/android.yml   CI: builds a debug APK on every push
app/src/main/kotlin/com/daynest/
  MainActivity.kt, DaynestApplication.kt
  data/
    local/          Room database, entities, DAOs (task, journal, habit, expense)
    preferences/    DataStore-backed settings (onboarding, theme, name)
    repository/     One repository per module, used by the ViewModels
  di/               A small hand-written dependency container (no Hilt)
  ui/
    theme/          Colors, typography, shapes/spacing — the design system
    components/     Reusable buttons, cards, empty states, FABs
    navigation/     Bottom navigation + the nav graph
    onboarding/, home/, tasks/, journal/, habits/, expense/, settings/
app/src/main/res/
    drawable, mipmap-anydpi-v26   Original Daynest branding (adaptive icon,
                                   monochrome icon, in-app logo)
```

## Build configuration at a glance

| | |
|---|---|
| Android Gradle Plugin | 8.13.0 |
| Kotlin | 2.1.0 |
| Gradle wrapper | 9.0.0 |
| compileSdk / targetSdk | 35 |
| minSdk | 26 |
| JDK required | 17 |

These versions are pinned in `gradle/libs.versions.toml` and matched exactly
by the JDK 17 setup in the GitHub Actions workflow.
