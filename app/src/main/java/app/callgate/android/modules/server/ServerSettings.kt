package app.callgate.android.modules.server

import app.callgate.android.modules.settings.KeyValueStorage
import app.callgate.android.modules.settings.get
import io.viascom.nanoid.NanoId

class ServerSettings(
    private val storage: KeyValueStorage,
) {
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
        private const val PORT = "port"
        private const val USERNAME = "username"
        private const val PASSWORD = "password"
    }
}