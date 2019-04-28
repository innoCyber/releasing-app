package ptml.releasing.app.form.adapter

import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

abstract class BaseSelectAdapter<V, T> : RecyclerView.Adapter<V>() where V : RecyclerView.ViewHolder, T : SelectModel {
    val items = mutableListOf<T>()


    open fun initSelectedItems(selected: List<Int>?) {
        if (selected != null) {
            Timber.d("Selecting... %s", selected.size)
            for (i in selected) {
                if(items.size > i){
                    items[i].checked = true
                }
            }
        }else{
            Timber.d("selection is null: resetting to 0")
            for (item in items) {
                item.checked = false
            }
        }
        notifyDataSetChanged()
    }

}