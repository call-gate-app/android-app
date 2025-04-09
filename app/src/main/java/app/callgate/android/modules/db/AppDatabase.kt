package app.callgate.android.modules.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import app.callgate.android.modules.webhooks.db.WebHook
import app.callgate.android.modules.webhooks.db.WebHooksDao

@Database(
    entities = [
        WebHook::class,
    ],
    version = 1,
    autoMigrations = [

    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun webhooksDao(): WebHooksDao

    companion object {
        fun getDatabase(context: android.content.Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "db.sqlite"
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}