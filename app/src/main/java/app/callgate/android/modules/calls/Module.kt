package app.callgate.android.modules.calls

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val callsModule = module {
    singleOf(::CallsService)
}