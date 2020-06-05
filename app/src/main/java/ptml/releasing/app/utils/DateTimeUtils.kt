package ptml.releasing.app.utils

import android.content.Context
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class DateTimeUtils @Inject constructor(private val context: Context) {

    fun formatDate(date: Date?): String? {
        val sdf = SimpleDateFormat("d MMM yyyy h:mm a", Locale.getDefault())
        return sdf.format(date)
    }

    fun formatExportDate(date: Date): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault())
        return sdf.format(date)
    }

    fun toTimeStampRelative(date: Date): CharSequence? {
        return DateUtils.getRelativeTimeSpanString(date.time, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS)
    }
}