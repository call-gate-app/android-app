package app.callgate.android.modules.webhooks

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val webhooksModule = module {
    singleOf(::WebHooksService)
}

private const val MODULE_NAME = "webhooks"
