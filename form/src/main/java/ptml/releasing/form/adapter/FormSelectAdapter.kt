package ptml.releasing.form.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import ptml.releasing.form.R

internal class FormSelectAdapter<T : SelectModel>(context: Context, private val list: List<T>?) :
    ArrayAdapter<T>(
        context, R.layout.form_spinner_single, list
            ?: mutableListOf()
    ) {

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

        textView.text = list?.get(position)?.text()
        return view
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.form_spinner_single, parent, false)
        }


        val textView = view!!.findViewById<TextView>(R.id.tv_category)
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
            textView, null, null, null, null
        )

        textView?.text = list?.get(position)?.text()
        return view
    }
}