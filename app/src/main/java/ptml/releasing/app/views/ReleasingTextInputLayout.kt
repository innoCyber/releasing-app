package ptml.releasing.app.views

import android.content.Context
import android.graphics.ColorFilter
import android.util.AttributeSet
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.textfield.TextInputLayout

class ReleasingTextInputLayout : TextInputLayout {

    private val backgroundDefaultColorFilter: ColorFilter?
        get() {
            return DrawableCompat.getColorFilter(editText?.background ?: return null)
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setError(error: CharSequence?) {
        val defaultColorFilter = backgroundDefaultColorFilter
        super.setError(error)
        //Reset EditText's background color to default.
        updateBackgroundColorFilter(defaultColorFilter)
    }

    override fun drawableStateChanged() {
        val defaultColorFilter = backgroundDefaultColorFilter
        super.drawableStateChanged()
        //Reset EditText's background color to default.
        updateBackgroundColorFilter(defaultColorFilter)
    }

    private fun updateBackgroundColorFilter(colorFilter: ColorFilter?) {
        editText?.background?.colorFilter = colorFilter
    }
}