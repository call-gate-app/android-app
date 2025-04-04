package app.callgate.android.modules.webhooks

import app.callgate.android.modules.settings.KeyValueStorage
import app.callgate.android.modules.settings.get
import io.viascom.nanoid.NanoId

class WebhooksSettings(
    private val storage: KeyValueStorage,
) {
    val internetRequired: Boolean
        get() = storage.get<Boolean>(INTERNET_REQUIRED) ?: true

    val retryCount: Int
        get() = storage.get<Int>(RETRY_COUNT) ?: 1

    val signingKey: String
        get() = storage.get<String>(SIGNING_KEY)
            ?: NanoId.generate(size = 8).also { storage.set(SIGNING_KEY, it) }

    companion object {
        const val INTERNET_REQUIRED = "internet_required"
        const val RETRY_COUNT = "retry_count"
        const val SIGNING_KEY = "signing_key"
    }
}