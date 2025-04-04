package app.callgate.android.modules.orchestrator

import android.app.ForegroundServiceStartNotAllowedException
import android.content.Context
import android.os.Build
import android.util.Log
import app.callgate.android.modules.server.ServerService
import app.callgate.android.modules.settings.GeneralSettings

class OrchestratorService(
    private val settings: GeneralSettings,
    private val serverSvc: ServerService,
) {
    fun start(context: Context, autostart: Boolean) {
        if (autostart && !settings.autostart) {
            return
        }

        try {
            serverSvc.start(context)
        } catch (e: Throwable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && e is ForegroundServiceStartNotAllowedException
            ) {
                Log.e(
                    "OrchestratorService",
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
        serverSvc.stop(context)
    }
}