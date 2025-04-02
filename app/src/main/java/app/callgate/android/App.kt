package app.callgate.android

import android.app.Application
import app.callgate.android.modules.calls.callsModule
import app.callgate.android.modules.notifications.notificationsModule
import app.callgate.android.modules.server.ServerService
import app.callgate.android.modules.server.serverService
import app.callgate.android.modules.settings.GeneralSettings
import app.callgate.android.modules.settings.settingsModule
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    private val settings: GeneralSettings by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                settingsModule,
                notificationsModule,
                serverService,
                callsModule,
            )
        }

        if (settings.autostart) {
            get<ServerService>().start(this)
        }
    }
}