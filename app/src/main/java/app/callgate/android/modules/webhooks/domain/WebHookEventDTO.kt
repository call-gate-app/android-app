package app.callgate.android.modules.webhooks.domain

data class WebHookEventDTO(
    val id: String,
    val webhookId: String,
    val event: WebHookEvent,
    val deviceId: String,
    val payload: Any,
)
