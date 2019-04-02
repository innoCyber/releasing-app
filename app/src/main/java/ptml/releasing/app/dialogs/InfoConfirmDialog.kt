package ptml.releasing.app.dialogs

import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

import com.yarolegovich.lovelydialog.LovelyStandardDialog
import ptml.releasing.R

class InfoConfirmDialog private constructor(
    context: Context,
    title: String?,
    message: String?,
    buttonText: String?,
    @DrawableRes topIcon: Int = 0,
    private val listener: InfoListener? = null
) : LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.HORIZONTAL), View.OnClickListener {

    init {
        setTopColorRes(R.color.colorAccent)
        setIcon(if (topIcon == 0) R.drawable.ic_info_white else topIcon)
        setTitle(title ?: context.getString(R.string.info))
        setMessage(message ?: context.getString(R.string.info_msg))
        setPositiveButton(buttonText, this)
    }

    override fun onClick(v: View) {
        dismiss()
        listener?.onConfirm()
    }

    interface InfoListener {
        fun onConfirm()
    }

    companion object {

        @JvmStatic
        fun showDialog(
            context: Context,
            title: String?,
            message: String?,
            buttonText: String? = context.getString(android.R.string.ok),
            @DrawableRes topIcon: Int = 0,
            listener: InfoListener? = null
        ) {
            val loadingDialog = InfoConfirmDialog(context, title, message, buttonText, topIcon, listener)
            loadingDialog.show()
        }

        @JvmStatic
        fun showDialog(
            context: Context, @StringRes title: Int, @StringRes message: Int, @DrawableRes topIcon: Int = 0,
            buttonText: String? = context.getString(android.R.string.ok),
            listener: InfoListener? = null
        ) {
            val loadingDialog =
                InfoConfirmDialog(
                    context = context,
                    title = context.getString(title),
                    message = context.getString(message),
                    buttonText = buttonText,
                    topIcon = topIcon,
                    listener = listener
                )
            loadingDialog.show()
        }
    }
}
