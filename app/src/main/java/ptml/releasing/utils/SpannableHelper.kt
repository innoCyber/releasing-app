package ptml.releasing.utils


import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.URLSpan
import androidx.core.content.ContextCompat
import ptml.releasing.R

class SpannableHelper {

    fun formatToLink(url: String?, context: Context?): SpannableString {
        val spannableUrl = SpannableString(url)
        spannableUrl.setSpan(URLSpan(url), 0, url?.length ?: 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableUrl.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.colorAccent)), 0, url?.length ?: 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannableUrl
    }


}
