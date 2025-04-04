package app.callgate.android.modules.calls.webhooks

/**
 * Data class representing the payload for call-related webhook events.
 * Contains information about the phone number associated with the call.
 */
data class CallEventPayload(
    /** The phone number associated with the call event, may be null if unavailable */
    val phoneNumber: String?,
)
