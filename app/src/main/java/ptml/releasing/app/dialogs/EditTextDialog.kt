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
import ptml.releasing.BuildConfig
import ptml.releasing.R
import ptml.releasing.app.utils.Validation
import ptml.releasing.databinding.DialogEdittextBinding

class EditTextDialog : DialogFragment() {
    private lateinit var binding: DialogEdittextBinding
    private var dialog: AlertDialog? = null
    var listener: EditTextDialogListener? = null

    companion object {
        const val EXTRA_URL = "extra.url"

        fun newInstance(url: String? = null, listener: EditTextDialogListener? = null): EditTextDialog {
            val fragment = EditTextDialog()
            val bundle = Bundle()
            bundle.putString(EXTRA_URL, url)
            fragment.arguments = bundle
            fragment.listener = listener
            return fragment
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogEdittextBinding.inflate(LayoutInflater.from(context), null, false)
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.change_server_url)
        builder.setView(binding.root)
                .setPositiveButton(getString(R.string.save), null)
                .setNegativeButton(getString(R.string.cancel), null)
        var serverUrl = arguments?.getString(EXTRA_URL)
        if(TextUtils.isEmpty(serverUrl)){
            serverUrl = BuildConfig.BASE_URL
        }
        binding.editServerUrl.setText(serverUrl)

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
        val url = binding.editServerUrl.text.toString()
        if (!Validation.isURL(url)) {
            binding.tilServerUrl.error = getString(R.string.enter_valid_url)
            return
        }
        listener?.onSave(url)
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
                binding.tilServerUrl.error =""
                dialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.isEnabled = !TextUtils.isEmpty(s)
            }
        })
    }

    interface EditTextDialogListener {
        fun onSave(url: String)
    }
}