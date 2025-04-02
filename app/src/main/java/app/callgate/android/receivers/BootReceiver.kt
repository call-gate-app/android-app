package app.callgate.android.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.callgate.android.modules.server.ServerService
import app.callgate.android.modules.settings.GeneralSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {
    private val settings: GeneralSettings by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (!events.contains(intent.action)) return

        if (settings.autostart) {
            get<ServerService>().start(context)
        }
    }

    companion object {
        private val events = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.ACTION_BOOT_COMPLETED",
            Intent.ACTION_REBOOT,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON",
            Intent.ACTION_SHUTDOWN,
        )
    }
}