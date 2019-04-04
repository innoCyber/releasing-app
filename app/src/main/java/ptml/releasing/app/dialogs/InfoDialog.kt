package ptml.releasing.app.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class InfoDialog : DialogFragment() {
    private var title: String? = null
    private var message: String? = null
    private var buttonText: String? = null
    private var listener:InfoListener? = null

    companion object {
        private const val TITLE = "title"
        private const val MESSAGE = "message"
        private const val BUTTON_TEXT = "button_text"

        @JvmStatic
        fun newInstance(
            title: String?,
            message: String?,
            buttonText: String? = null,
            listener: InfoListener? = null
        ): InfoDialog {
            val bundle = Bundle()
            bundle.putString(TITLE, title)
            bundle.putString(MESSAGE, message)
            bundle.putString(BUTTON_TEXT, buttonText)
            val fragment = InfoDialog()
            fragment.arguments = bundle
            fragment.listener = listener
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString(TITLE)
        message = arguments?.getString(MESSAGE)
        buttonText = arguments?.getString(BUTTON_TEXT)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText) { _, _ ->
                listener?.onConfirm()
            }

        return builder.create()
    }


    interface InfoListener {
        fun onConfirm()
    }

}