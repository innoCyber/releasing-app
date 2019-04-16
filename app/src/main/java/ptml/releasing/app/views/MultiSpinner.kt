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
import ptml.releasing.R
import ptml.releasing.configuration.models.Options


import java.util.ArrayList
import java.util.LinkedHashMap

class MultiSpinner : AppCompatSpinner, OnMultiChoiceClickListener, OnCancelListener {

    private var items: List<String>? = null
    private var selected = mutableListOf<Boolean>()
    var defaultHintText = "Select Items"
    private var spinnerTitle: String? = ""
    private var listener: MultiSpinnerListener? = null

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
        val spinnerBuffer = StringBuilder()
        for (i in items?.indices ?: 0 until 0) {
            if (selected[i]) {
                spinnerBuffer.append(items!![i])
                spinnerBuffer.append(", ")
            }
        }

        var spinnerText = spinnerBuffer.toString()
        if (spinnerText.length > 2) {
            spinnerText = spinnerText.substring(0, spinnerText.length - 2)
        } else {
            spinnerText = defaultHintText
        }

        val adapter = MultiSpinnerAdapter(context, spinnerText)
        setAdapter(adapter)
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
        onCancel(null)
    }


}

internal class MultiSpinnerAdapter(context: Context, text: String)
    : ArrayAdapter<String>(context, R.layout.spinner_configuration_layout, arrayListOf(text)) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.spinner_configuration_layout, parent, false)
        }
        val textView = view!!.findViewById<TextView>(R.id.tv_category)
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_down)
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                textView, null, null, drawable, null)

        textView.text = getItem(position)
        return view
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.spinner_configuration_layout, parent, false)
        }


        val textView = view!!.findViewById<TextView>(R.id.tv_category)
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                textView, null, null, null, null)

        textView?.text = getItem(position)
        return view
    }
}

interface MultiSpinnerListener {
    fun onItemsSelected(selected: List<Boolean>)
}
