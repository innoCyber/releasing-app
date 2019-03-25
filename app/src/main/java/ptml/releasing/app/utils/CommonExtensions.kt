package ptml.releasing.app.utils

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import android.view.ViewGroup



/**
 * Extension method to update padding of view.
 *
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun View.updatePadding(paddingStart: Int = getPaddingStart(),
                       paddingTop: Int = getPaddingTop(),
                       paddingEnd: Int = getPaddingEnd(),
                       paddingBottom: Int = getPaddingBottom()) {
    setPaddingRelative(paddingStart, paddingTop, paddingEnd, paddingBottom)
}

fun View.updateMargin(start: Int = marginStart,
                      top: Int = marginTop,
                      end: Int = marginEnd,
                      bottom: Int = marginBottom) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(start, top, end, bottom)
        requestLayout()
    }
}
