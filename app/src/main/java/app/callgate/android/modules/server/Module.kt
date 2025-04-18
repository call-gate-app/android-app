package app.callgate.android.modules.server

import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val serverService = module {
    singleOf(::ServerService)
    viewModelOf(::ServerViewModel)
}