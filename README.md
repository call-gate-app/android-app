# 📞 CallGate 

[![Apache License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

**Call Management API for Android**  

CallGate provides programmatic control of phone calls through a REST API, specifically designed for automation scenarios requiring basic call management without voice interaction.

> ⚠️ **No Audio Handling** - This app only manages call initiation/termination, **does NOT handle voice playback or recording**

## 🌟 Features

- 📲 Start/stop calls via HTTP requests
- 📡 Webhooks for call state events
- 🔐 Basic authentication protection
- 📶 Local server operation (no internet required)
- 🛠️ Simple JSON API structure

## 📌 Related Projects

✅ **SMSGate** - Companion project for SMS management: [https://sms-gate.app/](https://sms-gate.app/)  
_Manage text messages through a similar API-driven approach._

## 🚀 Getting Started

### Prerequisites
- Android device with SIM card
- Network access to the device

### Setup
1. Install the [APK](https://github.com/call-gate-app/android-app/releases/latest) on your Android device
2. Start the server by tapping the "Offline" button
3. Ensure the icon appears in the status bar
4. Open Settings → Server to view default credentials

## 🛠️ Usage

### API Endpoints
Base URL: `http://<device-ip>:8084/api/v1`

#### Start a Call
```http
POST /calls
```
**Request:**
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -u username:password \
  -d '{"call": {"phoneNumber": "123456789"}}' \
  http://device-ip:8084/api/v1/calls
```
**Response:**
- `200 OK`: Call initiated successfully
- `400 Bad Request`: Invalid request
- `401 Unauthorized`: Invalid credentials
- `500 Internal Server Error`: Call failed

#### End Active Call
```http
DELETE /calls
```
**Request:**
```bash
curl -X DELETE \
  -u username:password \
  http://device-ip:8084/api/v1/calls
```
**Response:**
- `204 No Content`: Call terminated
<!-- - `404 Not Found`: No active call -->
- `500 Internal Server Error`: Termination error

## 📡 Webhooks

Webhooks allow you to receive events related to call state changes. For now next events are supported:

- `call:ringing`: Call is ringing
- `call:started`: Call has started
- `call:ended`: Call has ended

### Prerequisites

- The receiver of the webhook must use valid SSL certificate
- The receiver of the webhook must be reachable from the device

### API Endpoints

Base URL: `http://<device-ip>:8084/api/v1/webhooks`

| Method | Endpoint         | Description               | Request Body                                                        | Response      |
| ------ | ---------------- | ------------------------- | ------------------------------------------------------------------- | ------------- |
| POST   | `/`              | Create or replace webhook | `{ "event": "call:ringing", "url": "https://example.com/webhook" }` | `201 Created` |
| GET    | `/webhooks`      | Retrieve all webhooks     |                                                                     | `200 OK`      |
| DELETE | `/webhooks/{id}` | Delete a specific webhook |                                                                     | `200 OK`      |

Webhook structure:

```json
{
    "id": "1",
    "event": "call:ringing",
    "url": "https://example.com/webhook"
}
```

The `id` field is optional and will be auto-generated if not provided.

The webhooks are compatible with the [SMSGate webhooks](https://docs.sms-gate.app/features/webhooks/) including signing. The main difference is in the retry policy, by default the CallGate makes only two attempts to send a webhook because call events more dynamic than SMS ones. Also the linear retry policy is used instead of exponential backoff.

## 🔒 Important Notes

- **Call Limitations**: Only manages call initiation/termination - no voice capabilities
- Security Recommendations:
  - Rotate credentials regularly
  - Restrict to trusted networks
  - Consider encryption for remote access

## 📌 Project Status

**Active Development**

⚠️ **Experimental Version** - API may change without notice  
Current focus areas:
- Improved call state management
- Enhanced error handling
- Enhanced Android versions compatibility

## 🤝 Contributing

We welcome contributions! Please:
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📜 License

Distributed under the Apache 2.0 License. See [LICENSE](LICENSE) for details.
