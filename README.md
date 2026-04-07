# BreakFree

An Android app that helps you reclaim your focus by blocking distracting apps and Instagram Reels during focus sessions.

## Features

- **Focus Sessions** — start timed sessions (25, 45, 60, or 90 minutes) that block selected apps
- **App Blocking** — uses Android's Accessibility Service to detect and block chosen apps while a session is active
- **Reels Blocking** — automatically redirects away from Instagram Reels during sessions
- **Google Sign-In** — authentication via Firebase + Google

## Tech Stack

- Kotlin + Jetpack Compose
- Firebase Authentication (Google Sign-In)
- Android Accessibility Service
- Material3 + custom glass-morphism UI
- ExoPlayer (Media3) for login screen video background

## Setup

### Prerequisites
- Android Studio
- Android device or emulator (API 24+)
- Firebase project

### Firebase Setup

1. Create a project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add an Android app with package name `com.example.breakfree`
3. Enable **Google Sign-In**: Authentication → Sign-in method → Google → Enable
4. Add your debug SHA-1 fingerprint: Project Settings → Your apps → SHA certificate fingerprints
   ```
   keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android -keypass android
   ```
5. Download `google-services.json` and place it in the `app/` folder

### Run

1. Clone the repo
2. Complete Firebase setup above
3. Open in Android Studio
4. Sync Gradle and run

### Accessibility Permission

The app requires the Accessibility Service permission to block apps. After installing, go to:

**Settings → Accessibility → BreakFree → Enable**

## Project Structure

```
app/src/main/java/com/example/breakfree/
├── MainActivity.kt                        # Entry point, auth state
├── session/
│   └── SessionManager.kt                  # Active session state + timer
├── service/
│   └── BreakFreeAccessibilityService.kt   # App blocking logic
└── ui/
    ├── auth/
    │   └── LoginScreen.kt                 # Google Sign-In screen
    ├── home/
    │   ├── HomeScreen.kt                  # Main focus screen
    │   └── StartSessionSheet.kt           # Session config bottom sheet
    ├── components/
    │   └── GlassCard.kt                   # Reusable glass card component
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## Auth

After sign-in, `Firebase.auth.currentUser` returns a `FirebaseUser` with:
- `uid` — unique user ID
- `email` — Google email
- `displayName` — full name
- `photoUrl` — profile picture URL
