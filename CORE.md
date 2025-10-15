# CORE - Launcherly

## Core Components

This document details the core components, utilities, and foundational elements of the Launcherly application.

## 1. Dependency Injection (Hilt)

### Application-Level Injection
- `@HiltAndroidApp` annotation on `LauncherlyApp` class
- Provides application context and base dependencies

### Activity/Fragment Injection
- `@AndroidEntryPoint` annotation for activities and fragments
- Enables injection of dependencies directly into UI components

### ViewModel Injection
- `@HiltViewModel` annotation for ViewModels
- Automatically provides ViewModel instances with injected dependencies

### Custom Modules
- Located in `core/di` directory
- Define bindings for services, repositories, and other dependencies

## 2. State Management

### Kotlin Flow & StateFlow
- Used for reactive programming and state management
- `StateFlow` for exposing immutable state streams from ViewModels
- `SharedFlow` for one-shot events and broadcasts

### UI State Patterns
- Sealed interfaces for representing different UI states
- Example: `HomeState` interface with implementations like `HomeInitialState`, `HomeLoadedFetchAppState`, `HomeErrorFetchAppsState`

### ViewModel Lifecycle
- `viewModelScope` for coroutine management
- Automatic cancellation when ViewModel is cleared
- Proper lifecycle-aware state updates

## 3. Core Services

### Home Service
- `HomeService`: Manages application data operations
- Fetches installed applications using package manager
- Filters between all apps and favorite apps
- Handles application launching

### App Info Model
- `AppInfoModel`: Data class representing application information
- Contains app name, package name, icon, and launch intent

### Background Service
- Handles dynamic background management
- Supports both image and video backgrounds
- Manages local and remote resource loading
- Google Drive URL conversion utilities

## 4. Core Utilities

### Image Loading (Coil)
- Custom `CoilImageLoader` implementation
- Optimized for TV usage with caching strategies
- Asynchronous image loading with placeholder support

### Google Drive Utilities
- `GoogleDriveUtils`: Converts sharing URLs to direct download URLs
- Handles Google Drive link transformations for media access

### File Utilities
- `LocalFileUtils`: Utilities for local file operations
- Checks for local file existence and Android resource paths
- Helper methods for file path validation

## 5. Shared Preferences Helper

### Preferences Management
- Located in `core/shared_prefs_helper` directory
- Manages user preferences and application settings
- Type-safe preference access with default values

## 6. Common Components

### Theme System
- Material 3 theme implementation for TV (androidx.tv.material3)
- Dark and light theme support
- Custom color scheme (Purple, PurpleGrey, Pink)

### Typography
- Consistent text styling across the app
- TV-optimized font sizes and weights

### Color System
- Standardized color palette for UI consistency
- TV-optimized contrast ratios for visibility

### Common Widgets
- Reusable UI components like `LoadingIndicator`
- TV-optimized component library

## 7. Broadcast Receivers

### App Change Receiver
- Monitors package installation/removal events
- Automatically updates app lists when changes occur
- Located in `features/app_change_receiver`

### Time Change Receiver
- Monitors system time/date changes
- Updates displayed time and date automatically
- Located in `features/time_change_receiver`

## 8. Navigation System

### Navigation Graph
- Single Activity architecture with Jetpack Navigation Compose
- Centralized navigation in `MainActivity`
- Type-safe navigation with composable destinations

### Destinations
- `check_login`: Initial screen to verify login status
- `login`: Authentication screen
- `home`: Main launcher interface
- `guided_settings`: TV-optimized settings
- `background_settings`: Background customization
- `local_file_picker`: File selection interface
- `favorite_apps_settings`: Favorite apps management

## 9. Data Models

### Sealed Classes for State
- `HomeState` and its implementations for UI state management
- Consistent pattern for handling loading, success, error, and empty states
- Type-safe state handling in Compose UI

### Data Transfer Objects
- Clean data models for API responses and local storage
- Proper separation between domain and UI models

## 10. Core Dependencies

### Jetpack Libraries
- **Compose Foundation/Ui**: Core UI toolkit components
- **Compose Material3**: Material Design 3 components for TV
- **Activity Compose**: Compose integration with Activity
- **Lifecycle**: ViewModel and reactive lifecycle components
- **Navigation Compose**: Type-safe navigation between screens

### External Libraries
- **Hilt**: Dependency injection framework
- **Coil**: Image loading library
- **Media3**: Media playback components
- **Retrofit**: HTTP client (via converter.gson)

## 11. TV-Specific Optimizations

### Input Handling
- Remote control navigation support
- Focus management for TV UI
- Key event handling for directional navigation

### Performance Considerations
- Efficient video playback for background videos
- Memory management for continuous background operations
- Optimized image loading with caching

## 12. Security Considerations

### Permissions
- Runtime permission handling where required
- Minimal permission requests based on functionality
- Scoped storage compliance for Android 11+

This core architecture provides a solid foundation for the Launcherly application, ensuring maintainability, scalability, and optimal performance for TV environments.