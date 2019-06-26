package ptml.releasing.app.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ptml.releasing.databinding.DialogChooseOperatorBinding

class ChooseOperatorInputDialog : DialogFragment() {
    var listener: ChooseOperatorListener? = null

    companion object {
        fun newInstance(listener: ChooseOperatorListener): ChooseOperatorInputDialog {
            val fragment = ChooseOperatorInputDialog()
            fragment.listener = listener
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogChooseOperatorBinding.inflate(LayoutInflater.from(context), null, false)
        val builder = MaterialAlertDialogBuilder(context!!)
        builder.setView(binding.root)
        builder.setTitle("Make a choice")

        binding.btnEnter.setOnClickListener {
            dismiss()
            listener?.onEnter()
        }

        binding.btnScan.setOnClickListener {
            dismiss()
            listener?.onScan()
        }

        return builder.create()
    }

    interface ChooseOperatorListener {
        fun onScan()
        fun onEnter()
    }

}