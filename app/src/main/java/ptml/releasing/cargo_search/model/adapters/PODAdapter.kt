package ptml.releasing.cargo_search.model.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import ptml.releasing.R
import ptml.releasing.cargo_search.model.PODOperationStep
import ptml.releasing.configuration.models.BaseConfig
import java.util.*


class PODAdapter(val context: Context, var dataSource: List<PODOperationStep>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_spinner, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.label.text = dataSource[position].DESCRIPTION
        val textView = view.findViewById<TextView>(R.id.tv_category)
        val drawable =   VectorDrawableCompat.create(
            context.resources, R.drawable.ic_arrow_drop_down, null)
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
            textView, null, null, drawable, null)

        return view
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ItemHolder(row: View?) {
        val label: TextView = row?.findViewById(R.id.tv_category) as TextView

    }

}