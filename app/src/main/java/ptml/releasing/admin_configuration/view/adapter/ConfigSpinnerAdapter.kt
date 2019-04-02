package ptml.releasing.admin_configuration.view.adapter

/**
 * Created by Cyberman on 3/12/2019.
 */

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import ptml.releasing.R
import ptml.releasing.admin_configuration.models.BaseConfig

class ConfigSpinnerAdapter<T>(context: Context, id: Int, private val list: List<T>) : ArrayAdapter<T>(context, id, list) where T : BaseConfig {

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

        textView.text = list[position].value
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

        textView?.text = list[position].value
        return view
    }
}