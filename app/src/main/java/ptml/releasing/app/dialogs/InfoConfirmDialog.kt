package ptml.releasing.app.dialogs

import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes


import ptml.releasing.R
import ptml.releasing.app.views.dialogs.LovelyStandardDialog

class InfoConfirmDialog private constructor(
    context: Context,
    title: String?,
    message: String?,
    buttonText: String?,
    private val listener: InfoListener? = null
) : LovelyStandardDialog(context, ButtonLayout.VERTICAL),
    View.OnClickListener {

    init {
        setTitle(title ?: context.getString(R.string.info))
        setMessage(message ?: context.getString(R.string.info_msg))
        setPositiveButton(buttonText, this)
        setNegativeButton(android.R.string.cancel, null)
        setButtonsColorRes(R.color.colorAccent)
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
            listener: InfoListener? = null
        ) {
            val loadingDialog =
                InfoConfirmDialog(context, title, message, buttonText, listener)
            loadingDialog.show()
        }

        @JvmStatic
        fun showDialog(
            context: Context, @StringRes title: Int, @StringRes message: Int,
            buttonText: String? = context.getString(android.R.string.ok),
            listener: InfoListener? = null
        ) {
            val loadingDialog =
                InfoConfirmDialog(
                    context = context,
                    title = context.getString(title),
                    message = context.getString(message),
                    buttonText = buttonText,
                    listener = listener
                )
            loadingDialog.show()
        }
    }
}
