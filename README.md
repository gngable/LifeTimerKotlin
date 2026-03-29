# LifeTimer

An Android app that shows how much time has elapsed since — or remains until — any date and time you choose. All time units update live every ~10 milliseconds.

## Features

- Pick any past or future date and time
- Displays total elapsed/remaining time in 8 units simultaneously: milliseconds, seconds, minutes, hours, days, weeks, months, and years
- Live updates every ~10ms
- SINCE / UNTIL indicator that flips automatically
- Supports light and dark mode (dynamic color on Android 12+)

## Requirements

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android device or emulator running API 26 (Android 8.0) or higher

## Build & Run

### Via Android Studio (recommended)

1. Clone the repo:

   ```sh
   git clone https://github.com/your-username/LifeTimerKotlin.git
   ```

2. Open Android Studio → **File → Open** → select the `LifeTimerKotlin` folder
3. Accept the Gradle wrapper download prompt and wait for sync to complete
4. Select a device or emulator (API 26+) and click **Run**

### Via command line

```bash
# Debug build
./gradlew :app:assembleDebug

# Install directly to a connected device
./gradlew :app:installDebug

# Release build (requires signing config)
./gradlew :app:assembleRelease
```

The debug APK is output to `app/build/outputs/apk/debug/app-debug.apk`.

## Project Structure

```text
app/src/main/kotlin/com/example/lifetimer/
├── MainActivity.kt        # Compose UI (picker, direction badge, time unit cards)
├── MainViewModel.kt       # 10ms ticker coroutine + TimeState computation
└── ui/theme/
    ├── Color.kt           # Material 3 color tokens
    ├── Type.kt            # Typography scale
    └── Theme.kt           # Light/dark color schemes, dynamic color, edge-to-edge
```

## Tech Stack

| Concern   | Solution                                                    |
| --------- | ----------------------------------------------------------- |
| UI        | Jetpack Compose + Material Design 3                         |
| State     | `ViewModel` + `StateFlow`                                   |
| Timer     | Coroutine `while(true) { delay(10L) }` in `viewModelScope`  |
| Time math | `java.time.temporal.ChronoUnit` (native at API 26+)         |
| Build     | Kotlin DSL + Gradle version catalog                         |
| Min SDK   | 26 (Android 8.0)                                            |
| Target SDK| 34 (Android 14)                                             |
