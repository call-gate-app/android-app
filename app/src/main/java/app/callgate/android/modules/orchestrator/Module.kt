package app.callgate.android.modules.orchestrator

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val orchestratorModule = module {
    singleOf(::OrchestratorService)
}

internal const val MODULE_NAME = "orchestrator"

