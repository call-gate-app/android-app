# 📞 CallGate

[![Apache License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
**Call Management API for Android™**

CallGate provides programmatic control of phone calls through a REST API, specifically designed for automation scenarios requiring basic call management without voice interaction.

> ⚠️ **No Audio Handling** - This app only manages call initiation/termination, **does NOT handle voice playback or recording**

- [📞 CallGate](#-callgate)
  - [🌟 Features](#-features)
  - [📦 Getting Started](#-getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
  - [🚀 Core API Usage](#-core-api-usage)
    - [📞 Call Management](#-call-management)
      - [Start Call](#start-call)
      - [End Active Call](#end-active-call)
  - [📡 Webhook System](#-webhook-system)
    - [Events](#events)
    - [Configuration](#configuration)
    - [Example Payload](#example-payload)
    - [SMSGate Compatibility](#smsgate-compatibility)
  - [🔒 Security Best Practices](#-security-best-practices)
  - [🌐 Related Projects](#-related-projects)
  - [📌 Project Status](#-project-status)
  - [🤝 Contributing](#-contributing)
  - [📜 License](#-license)

## 🌟 Features

- 📲 Call control via HTTP API
- 📞 USSD support
- 📡 Real-time webhook notifications
- 🔒 Basic authentication protection
- 📶 Local server operation (no internet required)
- 🛠️ Simple JSON API structure

## 📦 Getting Started

### Prerequisites

- Android device with SIM card
- Network access to the device

### Installation

1. [Download the latest APK](https://github.com/call-gate-app/android-app/releases/latest)
2. Install and launch the app
3. Tap "Offline" to start server → Status bar icon appears
4. **First Steps:**
    - Settings → Server to view credentials
    - Change the random password to something secure, if necessary

## 🚀 Core API Usage

**Base URL:** `http://<device-ip>:8084/api/v1`

### 📞 Call Management

#### Start Call

```http
POST /calls
```

```bash
curl -X POST \
  -u "username:password" \
  -H "Content-Type: application/json" \
  -d '{"call": {"phoneNumber": "+1234567890"}}' \
  http://device-ip:8084/api/v1/calls
```

**Responses:**

- `200 OK`: Call initiated
- `400 Bad Request`: Invalid number format
- `401 Unauthorized`: Invalid credentials
- `500 Internal Server Error`: Call failed

#### End Active Call

```http
DELETE /calls
```

```bash
curl -X DELETE \
  -u "username:password" \
  http://device-ip:8084/api/v1/calls
```

**Responses**

- `204 No Content`: Call ended
- `404 Not Found`: No ringing or active call
- `500 Internal Server Error`: Termination failed

## 📡 Webhook System

### Events

| Event          | Description       |
| -------------- | ----------------- |
| `call:ringing` | Device is ringing |
| `call:started` | Call connected    |
| `call:ended`   | Call terminated   |

### Configuration

**Base URL:** `http://<device-ip>:8084/api/v1/webhooks`

| Method | Endpoint         | Description               |
| ------ | ---------------- | ------------------------- |
| POST   | `/`              | Create or replace webhook |
| GET    | `/webhooks`      | Retrieve all webhooks     |
| DELETE | `/webhooks/{id}` | Delete a specific webhook |

```bash
# Register webhook
curl -X POST \
  -u "username:password" \
  -H "Content-Type: application/json" \
  -d '{"event":"call:started", "url":"https://your-server.com/webhook"}' \
  http://device-ip:8084/api/v1/webhooks

# Retrieve all webhooks
curl -X GET \
  -u "username:password" \
  http://device-ip:8084/api/v1/webhooks

# Delete a specific webhook
curl -X DELETE \
  -u "username:password" \
  http://device-ip:8084/api/v1/webhooks/123
```

**Requirements:**

- Valid SSL certificate on receiver
- Receiver accessible from device

### Example Payload

```json
{
  "deviceId": "0000000019c2d7bf00000195fe00ac0c",
  "event": "call:ringing",
  "id": "cKTqpr_Rrdgqzsv5ZyqWT",
  "payload": {
    "phoneNumber": "6505551212"
  },
  "webhookId": "hNMNzp4EYlwWsGxWiWcpD"
}
```

### SMSGate Compatibility

✅ Uses the same webhook request structure and signing mechanism as [SMSGate webhooks](https://docs.sms-gate.app/features/webhooks/)

⚠️ Key differences:

- Linear retry policy (vs exponential backoff)
- Max 1 retry attempt by default (call events expire quickly)

## 🔒 Security Best Practices

- Rotate credentials regularly
- Restrict to trusted networks
- Consider encryption for remote access

## 🌐 Related Projects

✅ **SMSGate** - Complete SMS Management Solution  
[https://sms-gate.app/](https://sms-gate.app/)

## 📌 Project Status

The app is currently in **active development** and **not ready for production use**.

**Version Warning**  
⚠️ API may change in minor versions during 0.x phase

## 🤝 Contributing

We welcome contributions! Please:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📜 License

Distributed under the Apache 2.0 License. See [LICENSE](LICENSE) for details.
