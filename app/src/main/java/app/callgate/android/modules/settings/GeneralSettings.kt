package app.callgate.android.modules.settings

class GeneralSettings(
    private val storage: KeyValueStorage,
) {
    val autostart: Boolean
        get() = storage.get(AUTOSTART) ?: false

    companion object {
        private const val AUTOSTART = "autostart"
    }
}