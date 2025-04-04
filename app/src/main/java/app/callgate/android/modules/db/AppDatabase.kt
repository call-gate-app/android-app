package app.callgate.android.modules.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [

    ],
    version = 1,
    autoMigrations = [

    ]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        fun getDatabase(context: android.content.Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "db.sqlite"
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}