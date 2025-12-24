# 📸 Facial Photo Management

An intelligent Android app that organizes your photos by automatically detecting and grouping faces using on-device machine learning.

![Android](https://img.shields.io/badge/Android-26%2B-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-purple?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-blue?logo=jetpackcompose)
![License](https://img.shields.io/badge/License-MIT-yellow)

## ✨ Features

- **🔍 Face Detection**: Uses Google ML Kit for accurate face detection in photos
- **🧠 Face Recognition**: TensorFlow Lite model for identifying and grouping similar faces
- **📁 Smart Organization**: Automatically groups photos by person
- **🔒 Privacy-First**: All processing happens on-device - your photos never leave your phone
- **🎨 Modern UI**: Beautiful Material 3 design with Jetpack Compose
- **⚡ Background Processing**: WorkManager handles scanning in the background

## 📱 Screenshots

*Coming soon*

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Kotlin** | Primary language |
| **Jetpack Compose** | Modern declarative UI |
| **ML Kit** | Face detection |
| **TensorFlow Lite** | Face recognition/embedding |
| **Room** | Local database |
| **WorkManager** | Background processing |
| **Coil** | Image loading |
| **Navigation Compose** | Screen navigation |

## 📋 Requirements

- Android 8.0 (API 26) or higher
- Camera permission (for future features)
- Storage/Media access permission

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API 34

### Build from Source

1. **Clone the repository**
   ```bash
   git clone https://github.com/saurabhkumar1432/facialPhotoManagement.git
   cd facialPhotoManagement
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or use Android Studio's Run button

### Install from Releases

1. Go to [Releases](https://github.com/saurabhkumar1432/facialPhotoManagement/releases)
2. Download the latest APK
3. Enable "Install from unknown sources" on your device
4. Install the APK

## 🏗️ Project Structure

```
app/src/main/
├── java/com/example/facialrecognition/
│   ├── data/           # Room database, repositories
│   ├── ml/             # ML Kit & TensorFlow integration
│   ├── ui/
│   │   ├── detail/     # Photo detail screen
│   │   ├── main/       # Main navigation
│   │   ├── photos/     # Photo grid views
│   │   ├── profile/    # User profile & settings
│   │   ├── search/     # Search functionality
│   │   ├── theme/      # Material theming
│   │   └── welcome/    # Onboarding screens
│   └── work/           # WorkManager jobs
└── res/                # Resources (layouts, drawables, etc.)
```

## 🤝 Contributing

Contributions are welcome! Please read our [Contributing Guide](CONTRIBUTING.md) for details on:
- Code of Conduct
- Development workflow
- How to submit pull requests

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Google ML Kit](https://developers.google.com/ml-kit) for face detection
- [TensorFlow Lite](https://www.tensorflow.org/lite) for on-device inference
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI

## 📬 Contact

**Saurabh Kumar**
- GitHub: [@saurabhkumar1432](https://github.com/saurabhkumar1432)

---

<p align="center">
  Made with ❤️ for organizing memories
</p>
