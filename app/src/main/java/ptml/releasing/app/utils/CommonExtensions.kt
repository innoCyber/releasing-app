package ptml.releasing.app.utils

import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


/**
 * Extension method to update padding of view.
 * @param paddingStart The start padding of the view, defaults to the original start padding or 0
 * @param paddingTop The top padding of the view, defaults to the original top padding or 0
 * @param paddingEnd The end padding of the view, defaults to the original end padding or 0
 * @param paddingBottom The bottom padding of the view, defaults to the original bottom padding or 0
 *
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun View.updatePadding(
    paddingStart: Int = getPaddingStart(),
    paddingTop: Int = getPaddingTop(),
    paddingEnd: Int = getPaddingEnd(),
    paddingBottom: Int = getPaddingBottom()
) {
    setPaddingRelative(paddingStart, paddingTop, paddingEnd, paddingBottom)
}

/**
 * Extension method to update margin of view.
 * @param start The start margin of the view, defaults to the original start margin or 0
 * @param top The top margin of the view, defaults to the original top margin or 0
 * @param end The end margin of the view, defaults to the original end margin or 0
 * @param bottom The bottom margin of the view, defaults to the original bottom margin or 0
 *
 */
fun View.updateMargin(
    start: Int = marginStart,
    top: Int = marginTop,
    end: Int = marginEnd,
    bottom: Int = marginBottom
) {
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        val p = layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(start, top, end, bottom)
        requestLayout()
    }
}

/**
 * Extension method to dismiss the SoftInput from the current window
 * @link InputMethodManager#hideSoftInputFromWindow()
 *
 */
fun View.hideSoftInputFromWindow() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.setValidation(validation: String?) {
    when (validation) {
        Constants.ALPHANUMERIC -> {
            inputType = InputType.TYPE_CLASS_TEXT
        }
        Constants.NUMERIC -> {
            inputType = InputType.TYPE_CLASS_NUMBER
        }
    }
}

fun EditText.setAllCapInputFilter() {
    val existingFilters = filters
    val newFilters = existingFilters.copyOf(existingFilters.size + 1)
    newFilters.set(existingFilters.size, InputFilter.AllCaps())
    filters = newFilters
}

fun EditText.setImeDoneListener(listener: DonePressedListener) {
    setOnEditorActionListener(object : TextView.OnEditorActionListener {
        override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                if (event.action == KeyEvent.ACTION_UP) {
                    listener.onDonePressed()
                }

                // We need to return true even if we didn't handle the event to continue
                // receiving future callbacks.
                return true
            } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                listener.onDonePressed()
                return true
            }
            return false
        }
    })
}

fun View.beInvisibleIf(beInvisible: Boolean) = if (beInvisible) beInvisible() else beVisible()

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) beVisible() else beGone()

fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

fun View.beInvisible() {
    visibility = View.INVISIBLE
}

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}

interface DonePressedListener {
    fun onDonePressed()
}
