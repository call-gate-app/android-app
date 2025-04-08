package app.callgate.android.modules.db

import org.koin.dsl.module

val dbModule = module {
    single { AppDatabase.getDatabase(get()) }

    single { get<AppDatabase>().webhooksDao() }
}