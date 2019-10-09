package ptml.releasing.app.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class InfoDialog : DialogFragment() {
    private var title: String? = null
    private var message: String? = null
    private var buttonText: String? = null
    private var negativeButtonText: String? = null
    private var hasNegativeButton: Boolean? = false
    private var listener: InfoListener? = null
    private var negativeListener: NegativeListener? = null

    companion object {
        private const val TITLE = "title"
        private const val MESSAGE = "message"
        private const val BUTTON_TEXT = "button_text"
        private const val NEUTRAL_BUTTON = "neutral_button"
        private const val NEUTRAL_BUTTON_TEXT = "neutral_button_text"

        @JvmStatic
        fun newInstance(
            title: String?,
            message: String?,
            buttonText: String? = null,
            listener: InfoListener? = null,
            hasNegativeButton: Boolean = false,
            negativeButtonText: String? = null,
            negativeListener: NegativeListener? = null
        ): InfoDialog {
            val bundle = Bundle()
            bundle.putString(TITLE, title)
            bundle.putString(MESSAGE, message)
            bundle.putString(BUTTON_TEXT, buttonText)
            bundle.putString(NEUTRAL_BUTTON_TEXT, negativeButtonText)
            bundle.putBoolean(NEUTRAL_BUTTON, hasNegativeButton)
            val fragment = InfoDialog()
            fragment.arguments = bundle
            fragment.listener = listener
            fragment.negativeListener = negativeListener
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString(TITLE)
        message = arguments?.getString(MESSAGE)
        buttonText = arguments?.getString(BUTTON_TEXT)
        negativeButtonText = arguments?.getString(NEUTRAL_BUTTON_TEXT)
        hasNegativeButton = arguments?.getBoolean(NEUTRAL_BUTTON)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(context!!)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(buttonText) { _, _ ->
                listener?.onConfirm()
            }
        if (hasNegativeButton == true) {
            builder.setNegativeButton(negativeButtonText) { _, _ ->
                negativeListener?.onNeutralClick()
            }
        }
        return builder.create()
    }


    interface InfoListener {
        fun onConfirm()
    }

    interface NegativeListener {
        fun onNeutralClick()
    }

}