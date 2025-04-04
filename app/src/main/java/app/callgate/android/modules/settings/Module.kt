package app.callgate.android.modules.settings

import androidx.preference.PreferenceManager
import app.callgate.android.modules.server.ServerSettings
import app.callgate.android.modules.webhooks.TemporaryStorage
import app.callgate.android.modules.webhooks.WebhooksSettings
import org.koin.dsl.module

val settingsModule = module {
    factory { PreferenceManager.getDefaultSharedPreferences(get()) }
    factory {
        ServerSettings(
            PreferencesStorage(get(), "server")
        )
    }
    factory {
        GeneralSettings(
            PreferencesStorage(get(), "general")
        )
    }
    factory {
        WebhooksSettings(
            PreferencesStorage(get(), "webhooks")
        )
    }
    factory {
        TemporaryStorage(
            PreferencesStorage(get(), "webhooks")
        )
    }
}