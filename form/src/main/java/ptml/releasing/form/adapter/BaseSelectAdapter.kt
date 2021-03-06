package ptml.releasing.form.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class BaseSelectAdapter<V, T> :
    RecyclerView.Adapter<V>() where V : RecyclerView.ViewHolder, T : SelectModel {
    val items = mutableMapOf<Int, T>()
    fun setItems(items: List<T>?) {
        this.items.clear()
        for (i in items ?: mutableListOf()) {
            this.items.put(i.id(), i)
        }
        notifyDataSetChanged()
    }

    open fun initSelectedItems(selected: List<Int>?) {

    }

}