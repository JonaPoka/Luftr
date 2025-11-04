# Luftr 💪

<div align="center">

**AI-Powered Gym Workout Tracker for Android**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org)
[![Material You](https://img.shields.io/badge/Design-Material_You-blue.svg)](https://m3.material.io)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

## 📱 About

Luftr is a modern, feature-rich gym workout tracker built with **Jetpack Compose** and **Material Design 3**. It combines intelligent workout planning with comprehensive tracking capabilities to help you achieve your fitness goals.

## ✨ Features

### 🤖 AI Workout Planner
- **Interactive Quiz System**: Answer questions about your fitness goals, experience level, workout duration, and target muscles
- **Personalized Workouts**: Generate custom workout plans tailored to your specific needs
- **Smart Exercise Selection**: Automatically selects appropriate exercises based on your preferences
- **Adaptive Rep Schemes**: Sets and reps adjusted for your goals (strength, hypertrophy, endurance, or weight loss)

### 🏋️ Workout Tracking
- **Real-Time Tracking**: Track sets, reps, and weight for each exercise
- **Custom Workouts**: Create and save your own workout routines
- **Exercise Library**: Comprehensive database of exercises organized by muscle groups
- **Set Management**: Easy add/remove sets with intuitive UI
- **Workout History**: View all past workouts with detailed statistics

### 🎨 Design
- **Material Design 3**: Modern, clean interface with dynamic color theming
- **Material You**: Adaptive colors that match your device theme
- **Intuitive Navigation**: Easy-to-use bottom navigation and screen flows
- **Responsive UI**: Optimized for various screen sizes

### 💾 Data Management
- **Local Database**: Fast, offline-first with Room database
- **Automatic Saving**: All your data saved locally and securely
- **Workout Statistics**: Track your progress over time

## 🏗️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **Design System**: Material Design 3 (Material You)
- **Navigation**: Jetpack Navigation Compose
- **Async Operations**: Kotlin Coroutines & Flow
- **Dependency Injection**: Manual (lightweight approach)

## 📋 Requirements

- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Android Studio**: Hedgehog or later
- **JDK**: 17

## 🚀 Getting Started

See [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) for detailed setup and build instructions.

### Quick Start

1. Clone this repository
```bash
git clone https://github.com/JonaPoka/Luftr.git
```

2. Open the project in Android Studio

3. Wait for Gradle sync to complete

4. Run the app on your device or emulator

## 📸 Screenshots

*(Screenshots will be added here)*

## 🗂️ Project Structure

```
app/src/main/java/com/jonapoka/luftr/
├── data/
│   ├── entities/          # Room database entities
│   ├── dao/               # Data Access Objects
│   ├── repository/        # Repository pattern implementation
│   └── AIWorkoutGenerator # AI workout generation logic
├── ui/
│   ├── screens/           # Composable screens
│   ├── theme/             # Material You theme
│   └── navigation/        # Navigation setup
├── viewmodel/             # ViewModels for MVVM
└── MainActivity.kt        # Main entry point
```

## 🎯 Roadmap

Future enhancements planned:
- [ ] Workout analytics and progress charts
- [ ] Exercise images and video guides
- [ ] Workout templates library
- [ ] Rest timer between sets
- [ ] Cloud sync and backup
- [ ] Social features (share workouts)
- [ ] Apple Health / Google Fit integration
- [ ] Custom exercise creation

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

Created with ❤️ by JonaPoka

## 🙏 Acknowledgments

- Material Design 3 guidelines and components
- Android Jetpack libraries
- Kotlin community

---

**Made with 💪 and Jetpack Compose**
