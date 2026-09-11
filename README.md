# Stapik Budgeting (Android)

A read-only mobile companion to [Stapik Budgeting](https://github.com/Stapik-Group/stapik-budgeting), the desktop household budget tracker. Written in Kotlin with Jetpack Compose, styled after the same retro old-school aesthetic as the desktop version and its sibling apps.

## Features

- **Period list** – browse budget periods (month/year), newest first, with an optional year filter
- **Period detail** – starting amount, planned vs. actual income/expense totals, and remaining balance for the period
- **Entries** – each expense/income entry shown with its category color, name, and planned/actual amounts; filterable by type (all/expense/income)
- **Cloud sync (read-only)** – fetches the budget from the Stapik Cloud compatible self-hosted API, same `documents/{slotKey}` endpoint as the desktop app
- **Offline cache** – automatically caches the last retrieved budget, with a banner showing when you're looking at cached data and from when
- **Pull-to-refresh** – drag down to fetch the latest data on demand
- **Themes** – Classic, Modern and Classic Pink, matching the desktop app, applied consistently across every screen
- **Encrypted connection config** – server URL and API key are encrypted with a key held in the Android Keystore before being stored on device; the API key is masked on screen with a show/hide toggle
- **Multilingual UI** – Polish, English and German, following the phone's system language
- **Retro aesthetic** – same raised-button, blue-navbar, grey-cell look as the desktop app

This app does not add, edit, or delete periods, categories, or entries – all of that happens in the desktop app. It only reads and displays whatever is currently in the cloud.

### What's intentionally not here

Like other companion apps, this app has **no notifications** and **no home screen widget** — a deliberate scope decision for the budgeting companion.

## Requirements

- Android 8.0 (API 26) or newer
- A running instance of the Stapik Cloud compatible sync API used by the desktop app (see [Stapik Budgeting](https://github.com/Stapik-Group/stapik-budgeting))

## Building

Requires [Android Studio](https://developer.android.com/studio) (Kotlin, Jetpack Compose).
