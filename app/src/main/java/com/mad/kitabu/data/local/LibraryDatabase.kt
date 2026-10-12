
package com.mad.kitabu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        BookEntity::class,
        BookingEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class LibraryDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    abstract fun bookingDao(): BookingDao

    companion object {

        @Volatile
        private var INSTANCE: LibraryDatabase? = null

        fun getDatabase(
            context: Context
        ): LibraryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LibraryDatabase::class.java,
                    "kitabu_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
