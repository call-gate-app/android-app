package app.callgate.android.modules.server

import app.callgate.android.modules.settings.KeyValueStorage
import app.callgate.android.modules.settings.get
import io.viascom.nanoid.NanoId

class ServerSettings(
    private val storage: KeyValueStorage,
) {
    var deviceId: String?
        get() = storage.get<String?>(DEVICE_ID)
        set(value) = storage.set(DEVICE_ID, value)

    val port: Int
        get() = storage.get<Int>(PORT) ?: 8084

    val username: String
        get() = storage.get<String?>(USERNAME)
            ?: "call"

    val password: String
        get() = storage.get<String?>(PASSWORD)
            ?: NanoId.generate(size = 8)
                .also { storage.set(PASSWORD, it) }

    companion object {
        private const val DEVICE_ID = "DEVICE_ID"
        private const val PORT = "port"
        private const val USERNAME = "username"
        private const val PASSWORD = "password"
    }
}