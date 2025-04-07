package app.callgate.android.modules.events

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val eventBusModule = module {
    singleOf(::EventBus)
}