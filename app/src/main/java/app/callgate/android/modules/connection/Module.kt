package app.callgate.android.modules.connection

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val connectionModule = module {
    singleOf(::ConnectionService)
    viewModelOf(::ConnectionViewModel)
}

val MODULE_NAME = "connection"