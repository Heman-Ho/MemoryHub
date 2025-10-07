# MemoryHub

MemoryHub is an Android application designed to support people with dementia (PWD) through reminiscence therapy.  
It features interactive memory-based games, such as a **matching game**, that use **personal images** to stimulate engagement and recall.

## Watch the Demo here:
[![Watch the demo](https://img.youtube.com/vi/gTWHod5TDxE/0.jpg)](https://youtu.be/gTWHod5TDxE)

---

## 📱 Features

-  **Matching Game** and **Puzzle Game** with adjustable difficulty (Easy / Medium / Hard)
-  **Personal Image Integration** (Firebase storage integration)  
-  **User Authentication** (Firebase Authentication)  
-  **Cloud Save** for user settings and progress (Firebase Realtime Database / Firestore)  

---


## Getting Started

Follow these steps to set up and run **MemoryHub** locally.

### 1. Clone the Repository
```bash
git clone https://github.com/<your-username>/MemoryHub.git
cd memory-hub
```
### 2. Open in Android Studio
- Select File -> Open
- choose the cloned memory-hub folder

### 3. Firebase setup
- Go to Firebase Console
- Click Add Project → name it MemoryHub.
- In your Firebase dashboard, click Add app → choose Android.
- Enter your app’s package name (You can find it under app/src/main/AndroidManifest.xml).
- Click Register app.

### 4. Generate and add google-services.json
- Download the google-services.json file from the Firebase setup page.
- Place it inside the project at app/google-services.json
- Make sure to sync gradle files (there is a button to sync on Android Studio).

### 5. Enable Firebase Services
- Go to the Firebase Console → Build section, and enable:
  - Authentication 
  - Realtime Database 
  - Storage 
- If using Realtime Database, you can start in Test Mode for local development.

## Running the app!
- Connect your Android device (or start an emulator).
- Click Run ▶ in Android Studio.
- Log in or register a user.
- Explore our features!


### Team Members
MemoryHub was developed as a collaborative SFU Computing Science project.
- Heman Ho
- Mohnish Devarapalli
- Dhwanan Mirani
- Elliot Ye
- Ryan Zhou
---

