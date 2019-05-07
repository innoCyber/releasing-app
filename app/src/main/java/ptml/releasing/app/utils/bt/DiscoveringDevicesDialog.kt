package ptml.releasing.app.utils.bt

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ptml.releasing.R
import ptml.releasing.databinding.ProgressBarLayoutBinding

class DiscoveringDevicesDialog : DialogFragment() {

    var listener: DiscoveringListener? = null

    companion object{
        @JvmStatic
        fun newInstance(listener: DiscoveringListener): DiscoveringDevicesDialog {
            val fragment = DiscoveringDevicesDialog()
            fragment.listener = listener
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = ProgressBarLayoutBinding.inflate(LayoutInflater.from(context))
        binding.tvMessage.text = getString(R.string.general_msg_please_wait)
        val builder = AlertDialog.Builder(context!!)
        builder.setView(binding.root)
        builder.setTitle(R.string.discovering_devices)
        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            listener?.onCancel()
            dialog.dismiss()
        }
        return builder.create()
    }


    interface DiscoveringListener {
        fun onCancel()
    }
}