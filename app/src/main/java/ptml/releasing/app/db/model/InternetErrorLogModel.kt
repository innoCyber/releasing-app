package ptml.releasing.app.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ptml.releasing.app.db.model.InternetErrorLogModel.Companion.TABLE_NAME
import java.util.*

@Entity(tableName = TABLE_NAME)
data class InternetErrorLogModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    val id: Int,
    @ColumnInfo(name = COLUMN_DESCRIPTION)
    val description: String,
    @ColumnInfo(name = COLUMN_URL)
    val url: String,
    @ColumnInfo(name = COLUMN_ERROR)
    val error:String,
    @ColumnInfo(name = COLUMN_DATE)
    val date: Date
) {

    companion object {
        const val TABLE_NAME = "internet_errors"
        const val COLUMN_ID = "id"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_URL = "url"
        const val COLUMN_ERROR = "error"
        const val COLUMN_DATE = "created"
        const val EMPTY_ID = -1

        const val GET_LOGS_QUERY = "SELECT $COLUMN_ID, $COLUMN_DESCRIPTION, $COLUMN_URL, $COLUMN_ERROR, $COLUMN_DATE " +
                "FROM   (SELECT $COLUMN_ID, $COLUMN_DESCRIPTION, $COLUMN_URL, $COLUMN_ERROR, $COLUMN_DATE, $COLUMN_DATE AS sort " +
                "        FROM   $TABLE_NAME " +
                "        UNION " +
                "        SELECT $EMPTY_ID as $COLUMN_ID, SUBSTR($COLUMN_DATE, 0, 9) as $COLUMN_DESCRIPTION, '' as $COLUMN_URL, '' as $COLUMN_ERROR, SUBSTR($COLUMN_DATE, 0, 9) || '000000' AS $COLUMN_DATE, SUBSTR($COLUMN_DATE, 0, 9) || '256060' AS sort " +
                "        FROM   $TABLE_NAME " +
                "        GROUP  BY SUBSTR($COLUMN_DATE, 0, 9)) " +
                "ORDER  BY sort DESC "

        const val GET_ALL_LOGS = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_DATE"
    }
}