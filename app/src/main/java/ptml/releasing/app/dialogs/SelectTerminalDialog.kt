package ptml.releasing.app.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ptml.releasing.R
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ReleasingTerminal
import ptml.releasing.configuration.view.adapter.ConfigSpinnerAdapter
import ptml.releasing.databinding.SelectTerminalLayoutBinding

class SelectTerminalDialog(
    private val terminals: List<ReleasingTerminal>,
    private val selected: Configuration
) : DialogFragment() {
    var listener: SelectTerminalListener? = null

    companion object {
        fun newInstance(
            terminals: List<ReleasingTerminal>, selected: Configuration,
            listener: SelectTerminalListener
        ): SelectTerminalDialog {
            val fragment = SelectTerminalDialog(terminals, selected)
            fragment.listener = listener
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = SelectTerminalLayoutBinding.inflate(LayoutInflater.from(context), null, false)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(binding.root)
            .setPositiveButton(getString(R.string.save)) { dialog, _ ->
                listener?.onSave(binding.selectTerminalSpinner.selectedItem as ReleasingTerminal)
                dialog.cancel()
            }
            .setNegativeButton(getString(R.string.cancel), null)
        builder.setCancelable(true)

        binding.selectTerminalSpinner.run {
            adapter = ConfigSpinnerAdapter(requireContext(), R.id.tv_category, terminals)
            val selectedItem = terminals.indexOf(selected.terminal)
            setSelection(if (selectedItem == -1) 0 else selectedItem)
        }

        return builder.create()
    }

    interface SelectTerminalListener {
        fun onSave(terminal: ReleasingTerminal)

    }

}