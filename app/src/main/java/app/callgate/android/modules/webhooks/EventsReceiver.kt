package app.callgate.android.modules.webhooks

import app.callgate.android.modules.events.EventBus
import app.callgate.android.modules.events.EventsReceiver
import kotlinx.coroutines.coroutineScope

class EventsReceiver : EventsReceiver() {
    override suspend fun collect(eventBus: EventBus) {
        coroutineScope {
//            launch {
//                eventBus.collect<PingEvent> {
//                    Log.d("EventsReceiver", "Event: $it")
//
//                    get<WebHooksService>().emit(
//                        WebHookEvent.SystemPing,
//                        mapOf("health" to it.health)
//                    )
//                }
//            }

//            launch {
//                eventBus.collect<MessageStateChangedEvent> { event ->
//                    Log.d("EventsReceiver", "Event: $event")
//
//                    val webhookEventType = when (event.state) {
//                        ProcessingState.Sent -> WebHookEvent.SmsSent
//                        ProcessingState.Delivered -> WebHookEvent.SmsDelivered
//                        ProcessingState.Failed -> WebHookEvent.SmsFailed
//                        else -> return@collect
//                    }
//
//                    event.phoneNumbers.forEach { phoneNumber ->
//                        val payload = when (webhookEventType) {
//                            WebHookEvent.SmsSent -> SmsEventPayload.SmsSent(
//                                messageId = event.id,
//                                phoneNumber = phoneNumber,
//                                event.simNumber,
//                                sentAt = Date(),
//                            )
//
//                            WebHookEvent.SmsDelivered -> SmsEventPayload.SmsDelivered(
//                                messageId = event.id,
//                                phoneNumber = phoneNumber,
//                                event.simNumber,
//                                deliveredAt = Date(),
//                            )
//
//                            WebHookEvent.SmsFailed -> SmsEventPayload.SmsFailed(
//                                messageId = event.id,
//                                phoneNumber = phoneNumber,
//                                event.simNumber,
//                                failedAt = Date(),
//                                reason = event.error ?: "Unknown",
//                            )
//
//                            else -> return@forEach
//                        }
//
//                        get<WebHooksService>().emit(
//                            webhookEventType, payload
//                        )
//                    }
//                }
//            }
        }
    }
}