# Launcherly

Short description: A modern Android TV launcher application with dynamic background support, app management, and custom settings.

## Badges
- Build: <!-- Add CI badge URL -->
- Coverage: <!-- Coverage badge -->

## Summary
Launcherly is an Android TV launcher application built with Jetpack Compose for TV. It provides a customizable home screen with dynamic background support (both images and videos), app management features, and various settings to customize the TV experience. The app supports both local and remote background resources including Google Drive links.

## Tech stack
- Languages: Kotlin
- Frameworks: Jetpack Compose for TV, AndroidX
- Architecture: MVVM (Model-View-ViewModel)
- Key services: Hilt (Dependency Injection), Navigation Compose, Media3 (for video playback), Coil (for image loading)
- Data: Shared Preferences for local storage

## Getting started
1. Clone repo
```bash
git clone <repo-url>
cd Launcherly
```
2. Open in Android Studio
3. Build the project
```bash
./gradlew build
```
4. Run on Android TV device or emulator (API 28+)

## Quick API example
The app doesn't expose external APIs but uses system APIs to:
- Fetch installed applications
- Manage favorite apps
- Control background media playback

## Troubleshooting
- Make sure to run on API 28+ as required by the app
- Ensure the device supports Android TV (Leanback launcher required)
- For video backgrounds, ensure proper permissions for external storage access

## Style & tone guidelines
- Audience: developer-first. Use active voice, imperative instructions for commands.
- Length: keep top-level sections short (1–6 paragraphs). Use links to other section files for detail.
- Code: include minimal working examples. For long examples, add a `<!-- TRUNCATED -->` note and link to a gist or file.
- Headings: use H2 for major sections, H3 for subsections. Use bullet lists for steps.
- Dates: include ISO format `YYYY-MM-DD` where applicable (e.g., release dates).