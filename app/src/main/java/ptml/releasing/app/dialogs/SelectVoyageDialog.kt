package ptml.releasing.app.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.DialogFragment
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ptml.releasing.R
import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import ptml.releasing.configuration.models.BaseConfig
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ReleasingTerminal
import ptml.releasing.configuration.view.adapter.ConfigSpinnerAdapter
import ptml.releasing.databinding.SelectTerminalLayoutBinding
import ptml.releasing.databinding.SelectVoyageLayoutBinding

class SelectVoyageDialog(
    private val voyages: List<ReleasingVoyage>,
    private val selected: ReleasingVoyage?
) : DialogFragment() {
    var listener: SelectVoyageListener? = null

    companion object {
        fun newInstance(
            voyages: List<ReleasingVoyage>,
             selected: ReleasingVoyage?,
            listener: SelectVoyageListener
        ): SelectVoyageDialog {
            val fragment = SelectVoyageDialog(voyages, selected)
            fragment.listener = listener
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = SelectVoyageLayoutBinding.inflate(LayoutInflater.from(context), null, false)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(binding.root)
            .setPositiveButton(getString(R.string.save)) { dialog, _ ->
                listener?.onSave(binding.selectVoyageSpinner.selectedItem as ReleasingVoyage)
                dialog.cancel()
            }
            .setNegativeButton(getString(R.string.cancel), null)
        builder.setCancelable(true)

        binding.selectVoyageSpinner.run {
            val withSelectOption = voyages.toMutableList()
            withSelectOption.add(0, ReleasingVoyage(-1, "None"))
            adapter = VoyageAdapter(requireContext(), R.id.tv_category, withSelectOption)
            val selectedItem = withSelectOption.indexOf(selected?: ReleasingVoyage(-1, "None"))
            setSelection(if (selectedItem == -1) 0 else selectedItem)
        }

        return builder.create()
    }

    interface SelectVoyageListener {
        fun onSave(voyage: ReleasingVoyage)

    }

    class VoyageAdapter(context: Context, id: Int, private val list: List<ReleasingVoyage>?)
        : ArrayAdapter<ReleasingVoyage>(context, id, list ?: mutableListOf()) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if (view == null) {
                val inflater = LayoutInflater.from(context)
                view = inflater.inflate(R.layout.item_spinner, parent, false)
            }
            val textView = view!!.findViewById<TextView>(R.id.tv_category)
            val drawable =   VectorDrawableCompat.create(
                context.resources, R.drawable.ic_arrow_drop_down, null)
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                textView, null, null, drawable, null)

            textView.text = list?.get(position)?.vesselName
            return view
        }


        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if (view == null) {
                val inflater = LayoutInflater.from(context)
                view = inflater.inflate(R.layout.item_spinner, parent, false)
            }


            val textView = view!!.findViewById<TextView>(R.id.tv_category)
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                textView, null, null, null, null)

            textView?.text = list?.get(position)?.vesselName
            return view
        }
    }


}