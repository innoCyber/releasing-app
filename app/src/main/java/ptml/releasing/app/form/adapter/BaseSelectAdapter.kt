package ptml.releasing.app.form.adapter

import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

abstract class BaseSelectAdapter<V, T> : RecyclerView.Adapter<V>() where V : RecyclerView.ViewHolder, T : SelectModel {
    val items = mutableListOf<T>()


    open fun initSelectedItems(selected: List<Int>?) {

    }

}