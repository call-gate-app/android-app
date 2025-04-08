package app.callgate.android.modules.webhooks

import android.content.Context
import android.webkit.URLUtil
import app.callgate.android.domain.EntitySource
import app.callgate.android.modules.server.ServerSettings
import app.callgate.android.modules.webhooks.db.WebHook
import app.callgate.android.modules.webhooks.db.WebHooksDao
import app.callgate.android.modules.webhooks.domain.WebHookDTO
import app.callgate.android.modules.webhooks.domain.WebHookEvent
import app.callgate.android.modules.webhooks.domain.WebHookEventDTO
import app.callgate.android.modules.webhooks.workers.SendWebhookWorker
import io.viascom.nanoid.NanoId
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.net.URL

class WebHooksService(
    private val webHooksDao: WebHooksDao,
    private val localServerSettings: ServerSettings,
//    private val gatewaySettings: GatewaySettings,
    private val webhooksSettings: WebhooksSettings,
) : KoinComponent {
//    private val eventsReceiver by lazy { EventsReceiver() }

    fun start(context: Context) {
//        eventsReceiver.start()
    }

    fun stop(context: Context) {
//        eventsReceiver.stop()
    }

    fun select(source: EntitySource): List<WebHookDTO> {
        return webHooksDao.selectBySource(source).map {
            WebHookDTO(
                id = it.id,
                url = it.url,
                event = it.event,
            )
        }
    }

    fun sync(source: EntitySource, webHooks: List<WebHookDTO>) {
        webHooksDao.replaceAll(source, webHooks.map {
            WebHook(
                id = requireNotNull(it.id) { "ID is required for sync" },
                url = it.url,
                event = it.event,
                source = source,
            )
        })
    }

    fun replace(source: EntitySource, webHook: WebHookDTO): WebHookDTO {
        if (!URLUtil.isHttpsUrl(webHook.url)
            && !(URLUtil.isHttpUrl(webHook.url) && URL(webHook.url).host == "127.0.0.1")
        ) {
            throw IllegalArgumentException("url must start with https:// or http://127.0.0.1")
        }
        if (webHook.event !in WebHookEvent.entries) {
            throw IllegalArgumentException(
                "Unsupported event"
            )
        }

        val webHookEntity = WebHook(
            id = webHook.id ?: NanoId.generate(),
            url = webHook.url,
            event = webHook.event,
            source = source,
        )
        webHooksDao.upsert(webHookEntity)

        return WebHookDTO(
            id = webHookEntity.id,
            url = webHookEntity.url,
            event = webHookEntity.event,
        )
    }

    fun delete(source: EntitySource, id: String) {
        webHooksDao.delete(source, id)
    }

    fun emit(event: WebHookEvent, payload: Any) {
        webHooksDao.selectByEvent(event).forEach {
            // skip emitting if source is disabled
//            when {
//                it.source == EntitySource.Local && !localServerSettings.enabled -> return@forEach
//                it.source == EntitySource.Cloud && !gatewaySettings.enabled -> return@forEach
//            }

            val deviceId = when (it.source) {
                EntitySource.Local -> localServerSettings.deviceId
//                EntitySource.Cloud -> gatewaySettings.deviceId
                else -> null
            } ?: return@forEach

            SendWebhookWorker.start(
                get(),
                url = it.url,
                WebHookEventDTO(
                    id = NanoId.generate(),
                    webhookId = it.id,
                    event = event,
                    deviceId = deviceId,
                    payload = payload,
                ),
                webhooksSettings.internetRequired
            )
        }
    }

}