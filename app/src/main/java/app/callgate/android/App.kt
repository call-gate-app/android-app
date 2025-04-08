package app.callgate.android

import android.app.Application
import app.callgate.android.modules.calls.callsModule
import app.callgate.android.modules.db.dbModule
import app.callgate.android.modules.notifications.notificationsModule
import app.callgate.android.modules.orchestrator.OrchestratorService
import app.callgate.android.modules.orchestrator.orchestratorModule
import app.callgate.android.modules.server.serverService
import app.callgate.android.modules.settings.settingsModule
import app.callgate.android.modules.webhooks.webhooksModule
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                settingsModule,
                dbModule,
                notificationsModule,
                serverService,
                callsModule,
                webhooksModule,
                orchestratorModule,
            )
        }

        get<OrchestratorService>().start(this, true)
    }
}