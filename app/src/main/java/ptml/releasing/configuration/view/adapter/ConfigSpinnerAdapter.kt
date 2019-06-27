package ptml.releasing.configuration.view.adapter

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
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import ptml.releasing.R
import ptml.releasing.configuration.models.BaseConfig

class ConfigSpinnerAdapter<T>(context: Context, id: Int, private val list: List<T>?) : ArrayAdapter<T>(context, id, list ?: mutableListOf()) where T : BaseConfig {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.spinner_single, parent, false)
        }
        val textView = view!!.findViewById<TextView>(R.id.tv_category)
        val drawable =   VectorDrawableCompat.create(
            context.resources, R.drawable.ic_arrow_drop_down, null)
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                textView, null, null, drawable, null)

        textView.text = list?.get(position)?.value
        return view
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.spinner_single, parent, false)
        }


        val textView = view!!.findViewById<TextView>(R.id.tv_category)
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                textView, null, null, null, null)

        textView?.text = list?.get(position)?.value
        return view
    }
}
