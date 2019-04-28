package ptml.releasing.app.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnCancelListener
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.R
import ptml.releasing.app.views.flex.FlexDirection
import ptml.releasing.app.views.flex.FlexWrap
import ptml.releasing.app.views.flex.FlexboxLayoutManager
import ptml.releasing.app.views.flex.JustifyContent
import ptml.releasing.databinding.ItemChipBinding
import timber.log.Timber
import java.util.*
import kotlin.collections.LinkedHashMap


class MultiSpinner : AppCompatSpinner, OnMultiChoiceClickListener, OnCancelListener {

    private var items: MutableList<String>? = null
    private var selected = mutableListOf<Boolean>()
    var defaultHintText: String? = "Select Items"
    private var spinnerTitle: String? = ""
    private var listener: MultiSpinnerListener? = null
    var selectedItems = LinkedHashMap<String, Int>()
    private val chipsListener = object : ChipListener {
        override fun onItemClose(position: Int, item: String, itemsRemaining: Int) {
            Timber.d("Removed %s", item)
            selected[position] = false
            selectedItems.remove(item)

            if (itemsRemaining <= 0) {
                val adapter = MultiSpinnerAdapter(context, defaultHintText)
                setAdapter(adapter)
            } else {
                chipAdapter()
            }
            if (selected.size > 0) {
                listener?.onItemsSelected(selected)
            }
        }
    }

    private fun chipAdapter() {
        val adapter = ChipsMultiSpinnerAdapter(context, selectedItems, chipsListener)
        setAdapter(adapter)
    }

    constructor(context: Context) : super(context) {}

    constructor(arg0: Context, arg1: AttributeSet) : super(arg0, arg1) {
        val a = arg0.obtainStyledAttributes(arg1, R.styleable.MultiSpinner)
        val N = a.indexCount
        for (i in 0 until N) {
            val attr = a.getIndex(i)
            if (attr == R.styleable.MultiSpinner_hintText) {
                spinnerTitle = a.getString(attr)
            }
        }
        a.recycle()
    }

    constructor(arg0: Context, arg1: AttributeSet, arg2: Int) : super(arg0, arg1, arg2) {}

    override fun onClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
        selected[which] = isChecked
    }

    override fun onCancel(dialog: DialogInterface?) {
        // refresh text on spinner
        selectedItems.clear()
        for (i in items?.indices ?: 0 until 0) {
            if (selected[i]) {
                selectedItems[items!![i]] = i
            }
        }



        if (selectedItems.size > 0) {
            val adapter = ChipsMultiSpinnerAdapter(context, selectedItems, chipsListener)
            setAdapter(adapter)
        } else {
            val adapter = MultiSpinnerAdapter(context, defaultHintText)
            setAdapter(adapter)
        }
        if (selected.size > 0) {
            listener?.onItemsSelected(selected)
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun performClick(): Boolean {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(spinnerTitle)
        builder.setMultiChoiceItems(
                items?.toTypedArray<CharSequence>(), selected.toBooleanArray(), this
        )
        builder.setPositiveButton(
                android.R.string.ok
        ) { dialog, which -> dialog.cancel() }
        builder.setOnCancelListener(this)
        builder.show()
        return true
    }

    /**
     * Sets items to this spinner.
     *
     * @param items    A TreeMap where the keys are the values to display in the spinner
     * and the value the initial selected state of the key.
     * @param listener A MultiSpinnerListener.
     */
    fun setItems(items: LinkedHashMap<String, Boolean>, listener: MultiSpinnerListener?) {
        this.items = ArrayList(items.keys)
        this.listener = listener

        val values = ArrayList(items.values)
        selected.addAll(values)
        for (i in 0 until items.size) {
            selected[i] = values[i]
        }

        // all text on the spinner
        val adapter = MultiSpinnerAdapter(context, defaultHintText)
        setAdapter(adapter)

        // Set Spinner Text
        // onCancel(null)
    }


    fun setSelection(selection: List<Int>?) {
        if (selection != null) {
            Timber.d("Selection is non null")
            for (i in selection) {
                if (selection.size > i) {
                    selected[i] = true
                }
            }
        } else {
            Timber.d("Selection is null")
            for (i in 0 until selected.size) {
                selected[i] = false
            }
        }
        onCancel(null)
    }

}

internal class MultiSpinnerAdapter(context: Context, text: String?) :
        ArrayAdapter<String>(context, R.layout.spinner_single, arrayListOf(text)) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.spinner_single, parent, false)
        }
        val textView = view!!.findViewById<TextView>(R.id.tv_category)
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_down)
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                textView, null, null, drawable, null
        )

        textView.text = getItem(position)
        return view
    }


    /*override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.spinner_single, parent, false)
        }


        val textView = view!!.findViewById<TextView>(R.id.tv_category)
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                textView, null, null, null, null
        )

        textView?.text = getItem(position)
        return view
    }*/
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return convertView ?: View(context)
    }
}


internal class ChipsMultiSpinnerAdapter(
        context: Context,
        var items: LinkedHashMap<String, Int>,
        val listener: ChipListener?
) :
        ArrayAdapter<String>(context, R.layout.spinner_single, items.keys.toList()) {

    companion object {
        const val SPAN_TWO_SIZE = 3
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            val rvAdapter = ChipAdapter()
            rvAdapter.setItems(items)
            rvAdapter.listener = listener

            val rootView = inflater.inflate(R.layout.spinner_multi, parent, false)
            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.flexDirection = FlexDirection.ROW
            layoutManager.justifyContent = JustifyContent.FLEX_START
            layoutManager.flexWrap = FlexWrap.WRAP

            val recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_view)
            recyclerView.adapter = rvAdapter
            recyclerView.layoutManager = layoutManager
            recyclerView.addItemDecoration(SpacesItemDecoration(4))

            view = rootView

        }

        return view ?: View(context)
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return convertView ?: View(context)
    }
}


interface MultiSpinnerListener {
    fun onItemsSelected(selected: List<Boolean>)
}

class ChipAdapter : RecyclerView.Adapter<ChipViewHolder>() {
    internal var items = LinkedHashMap<String, Int>()
    internal var itemsList = mutableListOf<String>()
    var listener: ChipListener? = null

    fun setItems(items: LinkedHashMap<String, Int>) {
        this.items.clear()
        this.itemsList.addAll(items.keys)
        this.items.putAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder {
        return ChipViewHolder(
                this,
                ItemChipBinding.inflate(LayoutInflater.from(parent.context), null, false),
                listener
        )
    }

    override fun getItemCount() = itemsList.size

    override fun onBindViewHolder(holder: ChipViewHolder, position: Int) {
        holder.performBind(itemsList[position])
    }

}

class ChipViewHolder(
        val adapter: ChipAdapter,
        val binding: ItemChipBinding,
        val listener: ChipListener? = null
) :
        RecyclerView.ViewHolder(binding.root) {

    fun performBind(item: String) {
        Timber.w("TEXT: %s", item)
        binding.chip.text = item
        binding.chip.isCheckable = false
        binding.chip.isClickable = true
        binding.chip.isChipIconVisible = false
        binding.chip.isCloseIconVisible = true
        binding.chip.setOnCloseIconClickListener {
            adapter.itemsList.removeAt(adapterPosition)
            adapter.notifyDataSetChanged()
            adapter.items[item]?.let { it1 -> listener?.onItemClose(it1, item, adapter.itemsList.size) }
        }
    }
}

interface ChipListener {
    fun onItemClose(position: Int, item: String, itemsRemaining: Int)
}

