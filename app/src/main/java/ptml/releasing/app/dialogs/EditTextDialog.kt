package ptml.releasing.app.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ptml.releasing.R
import ptml.releasing.app.utils.Validation
import ptml.releasing.databinding.DialogEdittextBinding

class EditTextDialog : DialogFragment() {
    private lateinit var binding: DialogEdittextBinding
    private var dialog: AlertDialog? = null
    var listener: EditTextDialogListener? = null

    companion object {
        const val EXTRA_URL = "extra.url"
        const val EXTRA_TITLE = "extra.title"
        const val EXTRA_HINT = "extra.hint"
        const val EXTRA_IS_URL = "extra.is.url"

        fun newInstance(
            url: String? = null,
            listener: EditTextDialogListener? = null,
            title: String? = null,
            hint: String? = null,
            isUrl:Boolean = true
        ): EditTextDialog {
            val fragment = EditTextDialog()
            val bundle = Bundle()
            bundle.putString(EXTRA_URL, url)
            bundle.putString(EXTRA_TITLE, title)
            bundle.putString(EXTRA_HINT, hint)
            bundle.putBoolean(EXTRA_IS_URL, isUrl)
            fragment.arguments = bundle
            fragment.listener = listener
            return fragment
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEdittextBinding.inflate(LayoutInflater.from(context), null, false)
        val builder = AlertDialog.Builder(context!!)
        val title = arguments?.getString(EXTRA_TITLE)
        builder.setTitle(if (TextUtils.isEmpty(title)) getString(R.string.change_server_url) else title)
        builder.setView(binding.root)
            .setPositiveButton(getString(R.string.save), null)
            .setNegativeButton(getString(R.string.cancel), null)
        val serverUrl = arguments?.getString(EXTRA_URL)
        binding.editServerUrl.setText(serverUrl)
        val hint = arguments?.getString(EXTRA_HINT)
        if (!TextUtils.isEmpty(hint)) {
            binding.tilServerUrl.hint = hint
        }

        dialog = builder.create()

        dialog?.setOnShowListener {
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.isEnabled = false
            button.setOnClickListener {
                validate()
            }
        }

        return dialog!!
    }

    private fun validate() {
        val text = binding.editServerUrl.text.toString()
        val isUrl = arguments?.getBoolean(EXTRA_IS_URL, true)
        if (isUrl == true && !Validation.isURL(text)) {
            binding.tilServerUrl.error = getString(R.string.enter_valid_url)
            return
        }else if (text.isEmpty()){
            binding.tilServerUrl.error = getString(R.string.enter_operator_name)
            return
        }
        listener?.onSave(text)
        dialog?.dismiss()
    }

    override fun onStart() {
        super.onStart()
        binding.editServerUrl.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilServerUrl.error = ""
                dialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.isEnabled = !TextUtils.isEmpty(s)
            }
        })
    }

    interface EditTextDialogListener {
        fun onSave(value: String)
    }
}