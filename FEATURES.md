# FEATURES - Launcherly

## Overview

Launcherly is a comprehensive Android TV launcher application with multiple features designed to enhance the TV experience. This document details the main features of the application.

## 1. Dynamic Background System

### Background Types
- **Image Backgrounds**: Support for local images and remote image URLs
- **Video Backgrounds**: Support for local videos and remote video URLs (including Google Drive links)
- **Custom Backgrounds**: Users can select from device storage or enter URLs for custom backgrounds

### Background Management
- Ability to set different background types (image or video)
- Google Drive URL conversion for direct access to shared resources
- Local file picker for selecting backgrounds from device storage

## 2. App Management

### App Display
- **Favorite Apps**: Shows only user-selected favorite applications
- **All Apps**: Displays all installed applications on the device
- **App Grid**: Organizes applications in a grid layout optimized for TV navigation

### App Operations
- Fetch list of all installed applications
- Mark/unmark applications as favorites
- Display app icons and names
- Launch applications through the launcher

## 3. Authentication System

### Login Process
- Check login status on app startup
- Login screen with authentication flow
- Automatic redirection based on login status

## 4. Date & Time Display

### Dynamic Time/Date
- Real-time clock display on the home screen
- Date information displayed alongside the time
- Automatic updates when system time changes

## 5. Settings System

### Guided Settings
- TV-optimized settings interface
- Navigation optimized for remote control
- Configuration options for various features

### Background Settings
- Options to configure background type and source
- Support for both local and remote resources
- Video and image background selection

### Favorite Apps Settings
- Interface to manage favorite applications
- Add/remove applications from favorites

## 6. Network & Internet Management

### Internet Status
- Real-time internet connectivity status
- Visual indicators for connection status
- Direct access to network settings

### Network Controls
- One-tap access to device network settings
- WiFi status indicators

## 7. Device Management

### System Integration
- Monitors package installation/removal events
- Automatic updates to app lists when packages change
- Time change detection for accurate clock display

## 8. TV-Specific Features

### Navigation
- Optimized for TV remote control navigation
- Focus management for TV UI components
- Directional keypad support

### Theme & Styling
- TV-optimized UI components using Material 3 for TV
- Dark theme optimized for TV viewing
- Responsive layout for different screen sizes

## 9. Media Playback

### Video Backgrounds
- Continuous video playback in the background
- Looping functionality for background videos
- Efficient resource management for continuous playback
- Support for various video formats through Media3

## 10. Security & Privacy

### Permissions
- Minimal permissions required for core functionality
- Scoped storage access for Android 11+ (API 30+)
- Package query permissions for app detection

## Feature Navigation

The app features are organized in the following navigation flow:
```
Check Login → Login (if needed) → Home Screen
    ↓
Settings → Guided Settings → Background Settings → Local File Picker
    ↓
Favorite Apps Settings
```

## Future Enhancements

<!-- TODO: Add future features planned for the application -->