# Luftr - Build Instructions

## Prerequisites

1. **Android Studio** (Hedgehog or later recommended)
   - Download from: https://developer.android.com/studio

2. **Android SDK**
   - Minimum SDK: 26 (Android 8.0)
   - Target SDK: 34 (Android 14)
   - Compile SDK: 34

3. **Java Development Kit (JDK) 17**
   - Usually bundled with Android Studio

## Opening the Project in Android Studio

1. Launch Android Studio
2. Select **"Open an Existing Project"** or go to **File > Open**
3. Navigate to the `Luftr` directory and select it
4. Click **"OK"**
5. Wait for Gradle sync to complete (this may take a few minutes the first time)

## Building the Project

### Using Android Studio UI
1. After Gradle sync completes, click **Build > Make Project** (Ctrl+F9 / Cmd+F9)
2. Wait for the build to complete
3. Check the Build output window for any errors

### Using Command Line
```bash
# On Linux/Mac:
./gradlew assembleDebug

# On Windows:
gradlew.bat assembleDebug
```

## Running the App

### Using Android Studio
1. Connect an Android device via USB (with USB debugging enabled) OR
2. Create an Android Virtual Device (AVD) from Tools > Device Manager
3. Click the **Run** button (▶) or press Shift+F10
4. Select your device/emulator
5. The app will install and launch automatically

### Using Command Line
```bash
# Install on connected device
./gradlew installDebug

# Start the app
adb shell am start -n com.jonapoka.luftr/.MainActivity
```

## Project Structure

```
Luftr/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/jonapoka/luftr/
│   │       │   ├── data/              # Database, DAOs, Repository
│   │       │   │   ├── entities/      # Room database entities
│   │       │   │   ├── dao/           # Data Access Objects
│   │       │   │   ├── repository/    # Repository layer
│   │       │   │   └── AIWorkoutGenerator.kt
│   │       │   ├── ui/                # UI layer
│   │       │   │   ├── screens/       # Compose screens
│   │       │   │   ├── theme/         # Material You theme
│   │       │   │   └── navigation/    # Navigation setup
│   │       │   ├── viewmodel/         # ViewModels
│   │       │   ├── MainActivity.kt
│   │       │   └── LuftrApplication.kt
│   │       └── res/                   # Resources (strings, icons, etc.)
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Features Implemented

✅ **Material Design 3 (Material You)**
- Dynamic color theming
- Modern Material 3 components
- Adaptive UI

✅ **Room Database**
- Workouts, Exercises, and Sets tables
- Relational data structure
- Flow-based reactive queries

✅ **AI Workout Planner**
- Interactive quiz for workout customization
- Personalized workout generation based on:
  - Fitness goals (Build Muscle, Lose Weight, Get Stronger, Improve Endurance)
  - Experience level (Beginner, Intermediate, Advanced)
  - Workout duration (30, 45, 60, 90+ minutes)
  - Target muscle groups (Chest, Back, Legs, Shoulders, Arms, Core)

✅ **Workout Tracking**
- Create and name custom workouts
- Add exercises with muscle group categorization
- Track sets, reps, and weight for each exercise
- Save and retrieve workout history

✅ **UI Screens**
- **Home Screen**: Quick access to features and recent workouts
- **Active Workout Screen**: Real-time workout tracking with set management
- **AI Planner Screen**: Step-by-step workout generation wizard
- **History Screen**: View all past workouts with statistics
- **Workout List Screen**: Browse all saved workouts

✅ **Additional Features**
- Workout duration tracking
- Exercise organization by muscle groups
- AI-generated workout indicators
- Clean and intuitive Material You interface

## Troubleshooting

### Gradle Sync Failed
- Make sure you have an active internet connection
- Try **File > Invalidate Caches > Invalidate and Restart**
- Update Gradle if prompted

### Build Errors
- Check that all required SDK components are installed
- Update Android Gradle Plugin if needed
- Clean and rebuild: **Build > Clean Project** then **Build > Rebuild Project**

### App Crashes
- Check Logcat in Android Studio for error messages
- Ensure the minimum SDK version (26) is met
- Verify database migrations are correct

## Next Steps

You can enhance the app by:
- Adding workout templates
- Implementing workout analytics and progress charts
- Adding exercise images/videos
- Implementing workout reminders/notifications
- Adding user authentication and cloud sync
- Adding exercise form guides
- Implementing workout sharing features

## Support

For issues or questions:
- Check existing issues on GitHub
- Create a new issue with detailed description
- Include error logs from Logcat when reporting crashes

---

**Happy Coding! 💪**
