package ptml.releasing.db.conveters;





import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.Map;

import androidx.room.TypeConverter;

public class DateConverter {

    /**
     * Convert a long value to a date
     * @param value the long value
     * @return the date
     */
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    /**
     * Convert a date to a long value
     * @param date the date
     * @return the long value
     */
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }



}
