<h1 align="center">🎬 Film Hunter</h1>

<p align="center">
  <a href="https://www.android.com/"><img alt="Platform" src="https://img.shields.io/badge/platform-android-brightgreen.svg"/></a>
  <a href="https://developer.android.com/about/versions/oreo"><img alt="API" src="https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/JetBrains/kotlin/releases/tag/v2.2.21"><img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.2.21-blueviolet"/></a>
  <a href="https://github.com/davidaxen/films-finder-app/blob/main/LICENSE"><img alt="License" src="https://img.shields.io/github/license/davidaxen/films-finder-app"/></a>
</p>

**FilmHunter** is an Android application that helps you discover movies and TV series, and find perfect matches with your friends through an interactive swiping experience.

> **Note:** This app is currently fully implemented in Spanish. Support for other languages will be added in the future.
> 
> **Note:** This app requires API keys for TMDB and Supabase. Make sure to set up your `secret.properties` file before building.

## 📱 Features

### 🏠 Home
- Discover movies and series through various categories

**Screenshots:**
<p align="left">
  <img width="30%" alt="home_movies" src="https://github.com/user-attachments/assets/caed7347-391d-48b2-b026-f52b54cc65a3" />
  &nbsp;
  <img width="30%" alt="home_series" src="https://github.com/user-attachments/assets/657cbf89-9e00-44c6-a655-d534f9bb0590" />
</p>

### 🔍 Search
- Search movies and series by title
- Explore films by genre and platform
- Find film details and related content

**Demo:**
<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td align="center">
      <video src="https://github.com/user-attachments/assets/79453443-65bb-4071-8a6f-610e042c644a"
             width="30%"
             controls>
      </video>
    </td>
    <td align="center">
      <video src="https://github.com/user-attachments/assets/ce94cd24-d770-4fcd-b4d0-6c024d2c6258"
             width="30%"
             controls>
      </video>
    </td>
  </tr>
</table>


### 💫 Match Sessions
- Create matching sessions with friends
- Swipe through films together in real-time
- Get instant notifications when you find a match
- View session history and matched films

**Screenshots & Videos:**
<!-- Add your Match Sessions feature screenshots/videos here -->
<!-- Example:
![Match Session](screenshots/match.png)
![Match Found](screenshots/match_found.png)
[![Match Demo](https://img.youtube.com/vi/VIDEO_ID/0.jpg)](https://www.youtube.com/watch?v=VIDEO_ID)
-->

### 💾 Saved Films
- Save your favorite movies and series
- Access your saved content anytime

**Demo:**
<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td align="center">
      <video src="https://github.com/user-attachments/assets/bfe4c1b5-6f7e-4a1d-9c37-d196f18d65af"
             width="30%"
             controls>
      </video>
    </td>
  </tr>
</table>

### 👤 Profile
- Manage your account settings
- Edit profile information
- Configure notifications and privacy settings

**Screenshot:**
<p align="left">
  <img width="30%" alt="Profile" src="https://github.com/user-attachments/assets/c6f220ec-1414-42ac-a29f-99d2c153f6ac" />
</p>

## 🛠️ Tech Stack

- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture with MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Navigation**: Navigation Compose
- **Backend**: Supabase (Authentication, Database, Real-time)
- **API**: The Movie Database (TMDB) API
- **Image Loading**: Coil
- **Networking**: Retrofit, Ktor Client
- **Serialization**: Kotlinx Serialization

## 📁 Project Structure

```
app/src/main/java/com/darvi/filmhunter/
├── data/              # Data layer (repositories, data sources, DTOs)
├── domain/            # Domain layer (entities, use cases)
├── presentation/      # UI layer (screens, view models, components)
│   ├── auth/          # Authentication screens
│   ├── core/          # Core components and navigation
│   ├── home/          # Home screen
│   ├── matcher/       # Match session screens
│   ├── profile/       # Profile screen
│   ├── saved/         # Saved films screen
│   └── search/        # Search screens
├── FilmHunterApplication.kt
└── MainActivity.kt
```

## 🚀 Getting Started

### Prerequisites

1. Clone the repository:
```bash
git clone https://github.com/davidaxen/films-finder-app.git
cd films-finder-app
```

2. Create a `secret.properties` file in the root directory with the following keys:
```properties
SUPABASE_KEY=your_supabase_anon_key
SUPABASE_URL=your_supabase_project_url
TMDB_API_KEY=your_tmdb_api_key
```

### Building the App

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run the app on an emulator or physical device

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the GPL-3.0 License - see the (<a href="https://github.com/davidaxen/films-finder-app/blob/main/LICENSE">LICENSE</a>) file for details.

## 👨‍💻 Author

David Bracamonte Martins
