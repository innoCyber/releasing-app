package ptml.releasing.app.form

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import ptml.releasing.R
import ptml.releasing.configuration.models.Options

internal class FormSelectAdapter(context: Context, private val list: List<Options>?)
        : ArrayAdapter<Options>(context, R.layout.spinner_configuration_layout, list
            ?: mutableListOf()) {

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

            textView.text = list?.get(position)?.name
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

            textView?.text = list?.get(position)?.name
            return view
        }
    }