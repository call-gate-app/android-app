package app.callgate.android.modules.orchestrator

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val orchestratorModule = module {
    singleOf(::OrchestratorService)
}

private const val MODULE_NAME = "orchestrator"

