# Kitabu — Library Book Rental & Reservation System

MADB372 SF1 — Android library application built using Kotlin, Jetpack Compose and Room.

## Features

- Browse a reactive catalog with book availability badges.
- Search books by title or author using Room SQL `LIKE`.
- Filter available titles.
- Reserve a book for 7, 14 or 21 days.
- Activate or cancel pending reservations.
- Renew active rentals by 7 days and return borrowed books.
- Track dates, remaining days and booking status.
- Save data locally across restarts.

## Technology and architecture

Kotlin · Jetpack Compose (Material 3) · Navigation Compose · Room (SQLite) · KSP · Kotlin Coroutines · Flow / StateFlow · MVVM.

`Compose screens → LibraryViewModel → LibraryRepository → Room DAOs → SQLite`

## Build and run

1. Clone this repository.
2. Open its root directory in Android Studio.
3. Allow Gradle to synchronize and install the requested Android SDK.
4. Select the **app** configuration and an Android device/emulator (minimum API 26).
5. Press **Run**. Sample books are seeded into Room at first setup.

Windows command-line commands:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:compileDebugAndroidTestKotlin
.\gradlew.bat :app:connectedDebugAndroidTest
```

The final command requires a connected Android device or emulator.

## CRUD coverage

| Operation | Code path |
| --- | --- |
| Create | Book insert and new reservation |
| Read | Catalog Flow, SQL search and bookings |
| Update | Activate, renew, return, restore availability |
| Delete | Cancel pending reservation |

The repository coordinates related updates through Room transactions. Instrumented database tests are under `app/src/androidTest/`.

## Screenshot evidence

Emulator screenshots of the Catalog, search, available-only filtering, reservation dialog, pending and active booking states, renewal and cancellation will be stored in `screenshots/`.

## Assessment evidence

The final Canvas submission will also include a Word report, AI prompt/response evidence and retrospective Figma design plans, as applicable. AI assistance was used to draft and refine code, troubleshoot Gradle and Room, and prepare documentation. App behavior was tested in Android Studio.

## License

Academic coursework. No open-source license granted.
