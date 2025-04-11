package app.callgate.android.modules.orchestrator

import android.app.ForegroundServiceStartNotAllowedException
import android.content.Context
import android.os.Build
import android.util.Log
import app.callgate.android.modules.server.ServerService
import app.callgate.android.modules.settings.GeneralSettings
import app.callgate.android.modules.webhooks.WebHooksService

class OrchestratorService(
    private val settings: GeneralSettings,
    private val serverSvc: ServerService,
    private val webHooksSvc: WebHooksService,
) {
    fun start(context: Context, autostart: Boolean) {
        if (autostart && !settings.autostart) {
            return
        }

        webHooksSvc.start(context)

        try {
            serverSvc.start(context)
        } catch (e: Throwable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && e is ForegroundServiceStartNotAllowedException
            ) {
                Log.e(
                    MODULE_NAME,
                    "Can't start foreground services while the app is running in the background"
                )
//                logsSvc.insert(
//                    LogEntry.Priority.WARN,
//                    MODULE_NAME,
//                    "Can't start foreground services while the app is running in the background"
//                )
                return
            }

            throw e
        }
    }

    fun stop(context: Context) {
        try {
            webHooksSvc.stop(context)
        } catch (e: Throwable) {
            Log.e(MODULE_NAME, "Failed to stop webhooks service", e)
        }

        try {
            serverSvc.stop(context)
        } catch (e: Throwable) {
            Log.e(MODULE_NAME, "Failed to stop server service", e)
        }
    }
}