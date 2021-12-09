package ptml.releasing.cargo_search.data.data_source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.cargo_search.domain.model.ChassisNumber
import ptml.releasing.cargo_search.domain.model.ShipSideChassisNumbers

@Database(entities = [ChassisNumber::class, ShipSideChassisNumbers::class], version = 3)
abstract class ChassisDatabase : RoomDatabase(){
    abstract fun chassisNumberDao(): ChassisNumberDao

    companion object {
        const val DATABASE_NAME = "chassisnumber_db"

            // Singleton prevents multiple instances of database opening at the
            // same time.
            @Volatile
            private var INSTANCE: ChassisDatabase? = null

            fun getDatabase(context: Context): ChassisDatabase {
                // if the INSTANCE is not null, then return it,
                // if it is, then create the database
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        ChassisDatabase::class.java,
                        DATABASE_NAME
                    ).build()
                    INSTANCE = instance
                    // return instance
                    instance
                }
            }
    }
}