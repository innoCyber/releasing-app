package ptml.releasing.form.views

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
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import ptml.releasing.form.R
import ptml.releasing.form.adapter.SelectModel
import ptml.releasing.form.databinding.FormItemChipBinding
import ptml.releasing.form.views.flex.FlexDirection
import ptml.releasing.form.views.flex.FlexWrap
import ptml.releasing.form.views.flex.FlexboxLayoutManager
import ptml.releasing.form.views.flex.JustifyContent
import timber.log.Timber
import java.util.*


class MultiSpinner<T> : AppCompatSpinner, OnMultiChoiceClickListener,
    OnCancelListener where T : SelectModel {


    private var items: MutableList<T>? = null
    private var selected = mutableListOf<Boolean>()
    var defaultHintText: String? = "Select Items"
    private var spinnerTitle: String? = ""
    private var listener: MultiSpinnerListener? = null

    val selectedItems = mutableMapOf<Int, T>()
    private val chipsListener = object : ChipListener<T> {
        override fun onItemClose(item: T, itemsRemaining: Int) {
            Timber.d("Removed %s", item)
            selected[item.position()] = false
            selectedItems.remove(item.id())

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
        Timber.d("Showing views")
        selectedItems.clear()
        for (i in items?.indices ?: 0 until 0) {
            if (selected[i]) {
                val item = items!![i]
                selectedItems[item.id()] = item
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

        Timber.d("Hello there items: %s", selectedItems.size)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun performClick(): Boolean {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(spinnerTitle)
        builder.setMultiChoiceItems(
            items?.map { it.text() }?.toTypedArray<CharSequence>(),
            selected.toBooleanArray(),
            this
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
    fun setItems(items: LinkedHashMap<T, Boolean>, listener: MultiSpinnerListener?) {
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


    fun setSelection(idList: List<Int>?) {
        val selection = convertIdToPosition(idList)
        Timber.d("Initializing multi-select with %s", selection.size)
        for (i in selection) {
            if (selected.size > i) {
                selected[i] = true
            }
        }

        listener?.onItemsSelected(selected)
        onCancel(null)
    }

    private fun convertIdToPosition(idList: List<Int>?): List<Int> {
        val positionList = mutableListOf<Int>()
        val itemIdsList = items?.map { it.id() }
        Timber.d("List of IDs: %s", itemIdsList)
        for (id in idList ?: mutableListOf()) {
            val optionPosition = if (itemIdsList?.contains(id) == true) {
                itemIdsList.indexOf(id)
            } else {
                null
            }

            Timber.d("Position of item: %s", optionPosition)

            if (optionPosition != null) {
                positionList.add(optionPosition)
            }

        }


        return positionList
    }

}

internal class MultiSpinnerAdapter(context: Context, text: String?) :
    ArrayAdapter<String>(context, R.layout.form_spinner_single, arrayListOf(text)) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.form_spinner_single, parent, false)
        }
        val textView = view!!.findViewById<TextView>(R.id.tv_category)
        val drawable = VectorDrawableCompat.create(
            context.resources, R.drawable.ic_arrow_drop_down, null
        )
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
            view = inflater.inflate(R.layout.form_spinner_single, parent, false)
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


internal class ChipsMultiSpinnerAdapter<T>(
    context: Context,
    var items: MutableMap<Int, T>,
    val listener: ChipListener<T>?
) :
    ArrayAdapter<T>(context, R.layout.form_spinner_single, items.values.toList()) where T : SelectModel {

    companion object {
        const val SPAN_TWO_SIZE = 3
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            val rvAdapter = ChipAdapter<T>()
            rvAdapter.setItems(items)
            rvAdapter.listener = listener

            val rootView = inflater.inflate(R.layout.form_spinner_multi, parent, false)
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

class ChipAdapter<T> : RecyclerView.Adapter<ChipViewHolder<T>>() where T : SelectModel {
    internal var items = mutableMapOf<Int, T>()
    internal var itemsList = mutableListOf<T>()
    var listener: ChipListener<T>? = null

    fun setItems(items: Map<Int, T>) {
        this.items.clear()
        this.itemsList.addAll(items.values)
        this.items.putAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChipViewHolder<T> {
        return ChipViewHolder(
            this,
            FormItemChipBinding.inflate(LayoutInflater.from(parent.context), null, false),
            listener
        )
    }

    override fun getItemCount() = itemsList.size

    override fun onBindViewHolder(holder: ChipViewHolder<T>, position: Int) {
        holder.performBind(itemsList[position])
    }

}

class ChipViewHolder<T>(
    val adapter: ChipAdapter<T>,
    val binding: FormItemChipBinding,
    val listener: ChipListener<T>? = null
) :
    RecyclerView.ViewHolder(binding.root) where T : SelectModel {

    fun performBind(item: T) {
        Timber.w("TEXT: %s", item)
        binding.chip.text = item.text()
        binding.chip.isCheckable = false
        binding.chip.isClickable = true
        binding.chip.isChipIconVisible = false
        binding.chip.isCloseIconVisible = true
        binding.chip.setOnCloseIconClickListener {
            adapter.itemsList.removeAt(adapterPosition)
            adapter.notifyDataSetChanged()
            adapter.items[item.id()]?.let {
                listener?.onItemClose(
                    item,
                    adapter.itemsList.size
                )
            }
        }
    }
}

interface ChipListener<T> where T : SelectModel {
    fun onItemClose(item: T, itemsRemaining: Int)
}

