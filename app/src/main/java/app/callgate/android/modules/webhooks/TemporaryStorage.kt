package app.callgate.android.modules.webhooks

import app.callgate.android.modules.settings.KeyValueStorage
import app.callgate.android.modules.settings.get

class TemporaryStorage(
    private val storage: KeyValueStorage,
) {
    fun put(key: String, value: String) {
        storage.set(PREFIX + key, value)
    }

    fun get(key: String): String? {
        return storage.get(PREFIX + key)
    }

    fun remove(key: String) {
        storage.remove(PREFIX + key)
    }

    companion object {
        private const val PREFIX = "storage."
    }
}