package ptml.releasing.app.views

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import ptml.releasing.R


class PlaceHolderDrawable
/**
 * @param context application context
 */
    (context: Context) : CircularProgressDrawable(context) {
    init {
        strokeWidth = 5f
        centerRadius = 30f
        setColorFilter(
            ContextCompat.getColor(context, R.color.colorAccent),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        start()
    }
}
