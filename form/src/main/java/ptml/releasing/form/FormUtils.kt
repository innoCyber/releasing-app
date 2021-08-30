package ptml.releasing.form

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import ptml.releasing.form.models.Options
import ptml.releasing.form.models.QuickRemark
import ptml.releasing.form.utils.SizeUtils


internal object FormUtils {

    fun inflateView(context: Context, @LayoutRes id: Int): View {
        val inflater = LayoutInflater.from(context)
        return inflater.inflate(id, null, false)
    }


    fun getImageResourceByType(type: String?): Int {
        return when (FormType.fromType(type)) {
            FormType.IMAGES -> R.drawable.ic_images
            FormType.PRINTER -> R.drawable.ic_print_barcode
            FormType.PRINTER_DAMAGES->R.drawable.ic_print_damages
            else -> R.drawable.ic_damages
        }
    }


    fun getDataForMultiSpinner(list: List<Options>?): LinkedHashMap<Options, Boolean> {
        val linkedHashMap = LinkedHashMap<Options, Boolean>()
        for (index in 0 until (list?.size ?: 0)) {
            val s = list?.get(index)
            s?.position = index
            if (s != null) {
                linkedHashMap[s] = false
            }
        }
        return linkedHashMap
    }


    fun getQuickRemarksDataForMultiSpinner(list: List<QuickRemark>?): LinkedHashMap<QuickRemark, Boolean> {
        val linkedHashMap = LinkedHashMap<QuickRemark, Boolean>()

        for (index in 0 until (list?.size ?: 0)) {
            val s = list?.get(index)
            s?.position = index
            if (s != null) {
                linkedHashMap[s] = false
            }
        }
        return linkedHashMap
    }
    
    fun applyParams(view: View) {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            SizeUtils.dp2px(view.context, 16f),
            SizeUtils.dp2px(view.context, 16f),
            SizeUtils.dp2px(view.context, 16f),
            0
        )

        view.layoutParams = params
    }


    fun applyBottomParams(view: View) {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            SizeUtils.dp2px(view.context, 8f),
            SizeUtils.dp2px(view.context, 2f),
            SizeUtils.dp2px(view.context, 8f),
            0
        )

        view.layoutParams = params
    }

    fun applyLabelParams(view: View) {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            SizeUtils.dp2px(view.context, 16f),
            SizeUtils.dp2px(view.context, 4f),
            SizeUtils.dp2px(view.context, 16f),
            0
        )

        view.layoutParams = params
    }

    fun applySimpleTextParams(view: View) {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            SizeUtils.dp2px(view.context, 16f),
            0,
            SizeUtils.dp2px(view.context, 16f),
            0
        )

        view.layoutParams = params
    }


    fun applyTopParams(view: View) {
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(
            SizeUtils.dp2px(view.context, 16f),
            SizeUtils.dp2px(view.context, 16f),
            SizeUtils.dp2px(view.context, 16f),
            0
        )

        view.layoutParams = params
    }


    fun getButtonValidationErrorMessage(type: String?): Int {
        return when (FormType.fromType(type)) {
            FormType.IMAGES -> R.string.form_select_image_msg
            FormType.PRINTER -> R.string.form_printer_error_msg
            FormType.PRINTER_DAMAGES->R.string.form_printer_damages_error_msg
            else -> R.string.form_select_damage_msg
        }
    }

    fun changeBgColor(view: View, error: Boolean) {
        view.setBackgroundColor(
            if (error) ContextCompat.getColor(
                view.context,
                R.color.colorRed
            ) else ContextCompat.getColor(view.context, R.color.colorTransparent)
        )
    }

    fun applyTintToBackground(view: View, @ColorInt color: Int) {
        val tintedDrawable = applyTintToDrawable(view.background, color)
        view.background = tintedDrawable
    }

    private fun applyTintToDrawable(drawable: Drawable, @ColorInt color: Int): Drawable? {
        return drawable.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            DrawableCompat.setTint(wrappedDrawable.mutate(), color)
            wrappedDrawable
        }
    }


    fun changeBgDrawable(view: View, error: Boolean) {
        view.setBackgroundResource(if (error) R.drawable.spinner_bg_error else R.drawable.spinner_bg)
    }


}