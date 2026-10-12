# Kitabu Library App

Kitabu is an Android library app that I developed for my MADB372 Mobile Application Development B assignment. The idea is to let a user browse books, check which ones are available and make reservations without needing an internet connection.

I built the app in Android Studio using Kotlin and Jetpack Compose. It uses a Room database to store the books and reservations on the device.

## What the app can do

- Display books with their titles, authors, categories and availability.
- Search for a book by its title or author.
- Show only books that are currently available.
- Reserve a book for 7, 14 or 21 days.
- View pending and active reservations under **My Library**.
- Activate or cancel a pending reservation.
- Renew an active reservation for another 7 days.
- Return a book so that it becomes available again.
- Keep saved records after the app is closed and reopened.

## Tools and technologies

- **Kotlin** for the application code.
- **Jetpack Compose and Material 3** for the interface.
- **Navigation Compose** for switching between the main screens.
- **Room Database** for storing books and bookings locally.
- **ViewModel and Repository** to separate the interface from the database logic.
- **Coroutines and Flow** so the interface updates when the saved data changes.
- **Gradle and KSP** for the project setup and Room code generation.

The app follows the MVVM approach. The Compose screens communicate with the ViewModel, which uses the repository to access the Room DAOs and database.

## How to run the project

1. Download or clone this repository.
2. Open the project folder in **Android Studio**.
3. Wait for **Gradle Sync** to finish. Android Studio might ask you to install an SDK or other components.
4. Start an Android emulator or connect an Android phone with USB debugging enabled.
5. Select the **app** run configuration and click **Run**.

When the app runs for the first time, it adds sample books to the local database.

### Build and test commands (Windows)

Run these commands in the project folder:

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :app:compileDebugAndroidTestKotlin
.\gradlew.bat :app:connectedDebugAndroidTest
```

The last command needs an emulator or Android device to be connected.

## Database operations

The project uses Room for the main CRUD operations:

| Operation | How I used it in Kitabu |
| --- | --- |
| Create | Add sample books and save new reservations |
| Read | Load the book list, search results and reservations |
| Update | Activate and renew reservations, return books and update availability |
| Delete | Remove a pending reservation when it is cancelled |

The database classes are in `app/src/main/java/com/mad/kitabu/data/local/`, and the repository is in `data/repository/`.

## Testing

I tested the app in Android Studio using an emulator. I checked that searching and filtering work, reservations can be made and cancelled, and active bookings can be renewed or returned. I also checked that the records stay saved when the app is restarted.

The project includes Room database tests in `app/src/androidTest/`.


## Project information

**Module:** Mobile Application Development B (MADB372)  
**Assessment:** SF1  
**Platform:** Android  
**Language:** Kotlin
