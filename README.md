# Tasbeeh & Adhkar 📿

A minimal, elegant, and distraction-free Android application designed to help you maintain consistency in your daily Dhikr and Adhkar. Developed with Jetpack Compose, this app provides a modern spiritual experience with powerful tracking and community features.

---

## ✨ Features

- **🎯 Personal Goals**: Set daily or all-time goals for specific Tasbeehs.
- **📚 Adhkar Library**: Access a curated library of common Adhkar with Arabic text, translations, and their specific virtues.
- **🛠️ Custom Tasbeehs**: Create and track your own custom Tasbeehs.
- **📊 Interactive Dashboard**: Visualize your progress with weekly activity charts and detailed statistics.
- **🏆 Achievements & Badges**: Stay motivated with a comprehensive achievement system and earn badges as you progress.
- **🌍 Global Leaderboard**: Join the community, see where you rank, and inspire others (optional and privacy-focused).
- **☁️ Cloud Sync**: Securely backup and restore your data using Firebase Authentication.
- **🔔 Smart Reminders**: Customizable periodic reminders to keep you engaged throughout the day.
- **🎨 Highly Customizable**: 
  - Dark/Light mode and "Pure Black" theme support.
  - Haptic feedback (vibration) and sound effects.
  - Multi-language support (English & Bengali).
  - Adjustable counter UI elements.

---

## 🚀 Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (100% Kotlin)
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database/Storage**: 
  - [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for preferences.
  - [Firebase Firestore](https://firebase.google.com/docs/firestore) for cloud sync and leaderboard.
- **Authentication**: [Firebase Auth](https://firebase.google.com/docs/auth) (Google Sign-In)
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)
- **Charts**: Custom Canvas-based drawing.
- **Dependency Management**: Version Catalogs (libs.versions.toml)

---

## 🛠️ Getting Started

### Prerequisites
- Android Studio Ladybug or newer.
- JDK 17+.
- A Firebase project (for cloud features).

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/ToukirMunna/Tasbeeh.git
   ```
2. Open the project in Android Studio.
3. Add your `google-services.json` file to the `app/` directory.
4. Build and run the application on your device or emulator.

---

## 📸 Screenshots

| Home | Library | Dashboard |
| :---: | :---: | :---: |
| ![Home](https://via.placeholder.com/300x600?text=Home+Screen) | ![Library](https://via.placeholder.com/300x600?text=Library+Screen) | ![Dashboard](https://via.placeholder.com/300x600?text=Dashboard+Screen) |

---

## 🤝 Contributing

Contributions are welcome! If you'd like to improve the app or add new features:
1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.

---

## 👨‍💻 Developer

**Toukir Ahmed**
- GitHub: [@ToukirMunna](https://github.com/ToukirMunna)

---
*May this app be a source of constant remembrance and peace for you.*
