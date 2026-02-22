# NetDet — 5G to 4G Network Monitor

An Android app that runs silently in the background and alerts you the moment your connection drops from 5G to 4G.

## What it does

- Monitors your mobile network type in real time
- Sends an instant notification when a 5G → 4G downgrade is detected
- Runs as a foreground service so it stays alive in the background

## Screenshots

![Screenshot_2026-02-22-22-54-03-50_26d05ef31836fd867434b94c4e1b6202](https://github.com/user-attachments/assets/158f6949-1526-462a-91f1-76a5d4b13038)

## Requirements

- Android 12 (API 31) or higher
- A device with 5G support
- `READ_PHONE_STATE` permission

## Setup

1. Clone the repo
```bash
   git clone https://github.com/yourusername/netdet.git
```
2. Open in Android Studio
3. Build and run on a physical device (emulators don't report real network types)

## Permissions Used

| Permission | Reason |
|---|---|
| `READ_PHONE_STATE` | Detect network type changes |
| `POST_NOTIFICATIONS` | Send downgrade alerts |
| `FOREGROUND_SERVICE` | Keep monitor running in background |
| `CHANGE_NETWORK_STATE` | Required for foreground service type |

## Tech

- Java
- `TelephonyCallback` API (Android 12+)
- Foreground Service with `connectedDevice` type
