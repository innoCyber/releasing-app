package ptml.releasing.app.form

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import ptml.releasing.R
import ptml.releasing.app.utils.SizeUtils
import ptml.releasing.configuration.models.ConfigureDeviceData
import ptml.releasing.configuration.models.Options


internal object FormUtils {

    fun inflateView(context: Context, @LayoutRes id: Int): View {
        val inflater = LayoutInflater.from(context)
        return inflater.inflate(id, null, false)
    }


    fun getImageResourceByType(type: String?): Int {
        return when (FormType.fromType(type)) {
            FormType.IMAGES -> R.drawable.ic_images
            FormType.PRINTER -> R.drawable.ic_print
            else -> R.drawable.ic_damages
        }
    }


    fun getDataForMultiSpinner(list: List<Options>?): LinkedHashMap<String, Boolean> {
        val linkedHashMap = LinkedHashMap<String, Boolean>()
        for (s in list ?: mutableListOf()) {
            linkedHashMap[s.name] = false
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
            SizeUtils.dp2px(view.context, 4f),
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


    fun changeBgDrawable(view: View, error: Boolean) {
        view.setBackgroundResource(if (error) R.drawable.spinner_bg_error else R.drawable.spinner_bg)
    }


}