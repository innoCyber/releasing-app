package ptml.releasing.app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ptml.releasing.app.db.converter.DateConverter
import ptml.releasing.app.db.dao.InternetErrorLogDao
import ptml.releasing.app.db.model.InternetErrorLogModel

@Database(
    entities = [InternetErrorLogModel::class],
    version = 1, exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class ReleasingDb : RoomDatabase() {

    abstract fun internetErrorDao(): InternetErrorLogDao

    companion object {
        private const val DB_NAME = "releasing.db"
        private var INSTANCE: ReleasingDb? = null
        private val lock = Any()

        fun getInstance(context: Context): ReleasingDb {
            if (INSTANCE == null) {
                synchronized(lock) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            ReleasingDb::class.java, DB_NAME
                        )
                            .build()
                    }
                    return INSTANCE as ReleasingDb
                }
            }
            return INSTANCE as ReleasingDb
        }
    }

}