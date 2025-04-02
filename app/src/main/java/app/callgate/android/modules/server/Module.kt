package app.callgate.android.modules.server

import org.koin.dsl.module

val serverService = module {
    single { ServerService() }
}