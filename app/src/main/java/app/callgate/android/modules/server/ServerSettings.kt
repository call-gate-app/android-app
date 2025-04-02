package app.callgate.android.modules.server

import app.callgate.android.modules.settings.KeyValueStorage
import app.callgate.android.modules.settings.get
import kotlin.random.Random

class ServerSettings(
    private val storage: KeyValueStorage,
) {
    val port: Int
        get() = storage.get<Int>(PORT) ?: 8084

    val username: String
        get() = storage.get<String?>(USERNAME)
            ?: "call"

    @OptIn(ExperimentalStdlibApi::class)
    val password: String
        get() = storage.get<String?>(PASSWORD)
            ?: Random.nextBytes(4).toHexString()
                .also { storage.set(PASSWORD, it) }

    companion object {
        private const val PORT = "port"
        private const val USERNAME = "username"
        private const val PASSWORD = "password"
    }
}