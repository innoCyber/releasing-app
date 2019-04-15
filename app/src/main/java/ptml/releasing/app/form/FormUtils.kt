package ptml.releasing.app.form

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import ptml.releasing.R

internal object FormUtils {

     fun inflateView(context:Context, @LayoutRes id: Int): View {
        val inflater = LayoutInflater.from(context)
        return inflater.inflate(id, null, false)
    }


    fun getImageResourceByType(type: String): Int {
        return when (FormType.valueOf(type)) {
            FormType.IMAGES -> R.drawable.ic_images
            FormType.PRINTER -> R.drawable.ic_print
            else -> R.drawable.ic_damages
        }
    }

}