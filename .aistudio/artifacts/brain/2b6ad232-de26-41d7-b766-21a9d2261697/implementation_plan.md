# Audio Recording, CameraX Media Notes & GitHub Release CI/CD

Implement device microphone voice notes with real-time speech transcription, CameraX circular video messages and quick photo snapping in chat, fix the GitHub Actions workflow to publish installable universal release APKs without KSP unit test failures, and expand frontend/backend features.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions were selected based on your preferences:

- **Camera Mode**: Circular video notes (Telegram/AyuGram style round video messages) alongside quick camera photo capture.
- **Voice Transcription**: On-device Android `SpeechRecognizer` with real-time text transcription bubble and offline fallback.
- **CI/CD Release Artifact**: Universal ready-to-install Release APK generated in GitHub Actions alongside debug builds, resolving the KSP/IntelliJ unit test daemon issue on Gradle 9.
- **Backend & Local Storage**: Structured message persistence with metadata for voice recordings (duration, audio file URI, speech-to-text transcript) and circular video notes (video URI, thumbnail, duration).

---

## 1. Overview & Core Concept

This update transforms **Mail Chat** into a rich multimedia messaging experience:
- **Audio Voice Recording & Transcription**: Hold or tap the microphone button with slide-to-cancel or lock-to-record gestures, capturing audio using Android's device microphone with real-time waveform visualization and on-device transcription directly into text before or during sending.
- **CameraX Circular Video Notes & Photos**: Seamless transition between audio and video note modes (tapping the mic/camera toggle icon like Telegram). Users can record circular video clips or take instant snaps directly within the chat dialog.
- **Flawless GitHub CI/CD Pipeline**: Fix the GitHub Actions workflow where KSP 2 / Gradle 9 crashed during `testDebugUnitTest` (`Cannot invoke ksp.com.intellij...`), generate a signed universal release APK artifact (`mail-chat-release.apk`) that can be installed on any Android phone directly from GitHub releases/artifacts.
- **Frontend & Backend Richness**: Expand chat with audio player playback controls, scrubber, speed toggle (1x / 1.5x / 2x), transcript toggle accordion, and saved ghost-mode preferences.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Voice Note Recording & Live Transcription**:
   - The user presses and holds the microphone button in `ConversationScreen`.
   - The input bar animates into a sleek recording bar with a pulsing red record dot, running timer (`00:03`), animated live audio amplitude waves, and a slide-to-cancel cue (`< Slide to cancel`).
   - Dragging upward locks recording into hands-free mode with a **Send**, **Pause**, and **Delete** button.
   - Concurrently, Android `SpeechRecognizer` listens and streams partial transcribed words into a live speech bubble above the bar.
   - On release or send, the voice note is added to the chat bubble with playback controls and an expandable transcript view.

2. **Circular Video Note Recording**:
   - Quick tap on the mic icon toggles to Camera mode (icon morphs with rotation animation).
   - Long-press launches a circular CameraX preview overlay with a spinning radial progress timer around the perimeter (up to 60s).
   - Releasing sends the circular video message bubble into the conversation with rounded masking and looping thumbnail preview.

3. **Universal Release APK Download**:
   - Pushing commits or triggering GitHub Actions runs the updated pipeline.
   - Unit tests are isolated from KSP daemon incompatibilities, and a release APK is generated and packaged as `mail-chat-release.apk` under GitHub Actions Artifacts, ready for direct side-loading on Android devices.

```
┌────────────────────────────────────────────────────────┐
│                   Chat Header                          │
│  [←]  (Avatar) Mail Support   [Ghost: ON] [⋮]         │
├────────────────────────────────────────────────────────┤
│                                                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │ 👤 Support: Welcome! How can we assist you?       │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│       ┌───────────────────────────────────────────┐    │
│       │ 🎙 0:14  ▶ ılı.lıllıl.ıllı    1.5x         │    │
│       │ 📝 Transcript: "Hello, checking my code"  │    │
│       │                              12:45 ✓✓     │    │
│       └───────────────────────────────────────────┘    │
│                                                        │
│       ┌──────────────┐                                 │
│       │    ( 🎥 )    │  Circular Video Note            │
│       │  [Play ▶]    │  12:46 ✓✓                       │
│       └──────────────┘                                 │
│                                                        │
├────────────────────────────────────────────────────────┤
│  [ 😊 ] [ 📎 ] [ Type a message...          ] [ 🎙/📷 ]│
└────────────────────────────────────────────────────────┘
```

---

## 3. Key Technical Decisions & Trade-Offs

- **CameraX API Integration**:
  - *Decision*: Uncomment `camera-camera2`, `camera-lifecycle`, `camera-view`, and `camera-core` in `app/build.gradle.kts`. Use `ProcessCameraProvider` and `PreviewView` inside Compose via `AndroidView`.
  - *Why*: CameraX provides lifecycle-aware camera capture with robust hardware compatibility across Android 7 to 15+.
- **Voice Recording & Live Transcription**:
  - *Decision*: Use `AudioRecord`/`MediaRecorder` for capturing clean AAC/M4A audio files alongside Android's built-in `SpeechRecognizer` (`RecognitionListener`) for real-time speech-to-text.
  - *Fallback*: If `SpeechRecognizer` speech language packs or Google services are offline on a specific device, it gracefully saves the audio file and marks transcription as "Audio message (offline)".
- **GitHub Actions Fix**:
  - *Decision*:
    1. Resolve KSP IntelliJ application service crash by configuring Gradle JVM arguments and properly isolating compilation steps.
    2. Configure keystore handling so `assembleRelease` creates a valid installable universal APK using release signing with fallback to debug certificate.
    3. Upload both `mail-chat-release.apk` and `mail-chat-debug.apk` with direct download URLs in GitHub Actions summary.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────┐
│                 Compose Chat UI Layer                  │
│  ConversationScreen  │  VoiceRecorderBar  │  CameraOverlay
└──────────────┬──────────────────┬──────────────────────┘
               │                  │
┌──────────────▼──────────────────▼──────────────────────┐
│                  ViewModel & Handlers                  │
│  ChatViewModel: SendVoiceMessage, SendVideoNote        │
└──────────────┬──────────────────┬──────────────────────┘
               │                  │
┌──────────────▼──────────────────▼──────────────────────┐
│                 Native Hardware Services               │
│  AudioRecordingManager          CameraXController      │
│  - MediaRecorder / AudioRecord  - ProcessCameraProvider│
│  - SpeechRecognizer (STT)       - ImageCapture / Video │
└────────────────────────────────────────────────────────┘
```

### Permission Strategy
- Declare `RECORD_AUDIO` and `CAMERA` in `AndroidManifest.xml`.
- Runtime permission request via Compose `rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions())` with explanatory bottom sheet if permission is denied.

### CI/CD Workflow Strategy
- Updated `.github/workflows/android-build.yml` with:
  - Java 17 setup
  - Gradle caching
  - Release APK building with signing fallback
  - KSP daemon stability flags
  - Direct artifact publishing under GitHub Actions
