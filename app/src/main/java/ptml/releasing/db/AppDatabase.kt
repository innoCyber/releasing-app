package ptml.releasing.db


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ptml.releasing.db.conveters.DateConverter
import ptml.releasing.db.daos.config.CargoTypeDao
import ptml.releasing.db.daos.config.OperationStepDao
import ptml.releasing.db.daos.config.TerminalDao
import ptml.releasing.db.models.config.CargoType
import ptml.releasing.db.models.config.OperationStep
import ptml.releasing.db.models.config.Terminal

/**
 * Created by Cyberman on 4/2/2018.
 */
@TypeConverters(DateConverter::class)
@Database(entities = [CargoType::class, OperationStep::class, Terminal::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cargoTypeDao(): CargoTypeDao

    abstract fun operationStepDao(): OperationStepDao

    abstract fun terminalDao(): TerminalDao


}
