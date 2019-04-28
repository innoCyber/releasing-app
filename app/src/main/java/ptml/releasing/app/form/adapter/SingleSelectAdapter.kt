package ptml.releasing.app.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.databinding.ItemCheckboxBinding
import timber.log.Timber

class SingleSelectAdapter<T> : BaseSelectAdapter<SingleSelectViewHolder<T>, T>() where T : SelectModel {
    var listener: SingleSelectListener<T>? = null
    var selectedItem: T? = null

    fun setItems(items: List<T>?) {
        this.items.clear()
        this.items.addAll(items ?: mutableListOf())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleSelectViewHolder<T> {
        return SingleSelectViewHolder(
                this,
                ItemCheckboxBinding.inflate(LayoutInflater.from(parent.context), null, false),
                listener
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SingleSelectViewHolder<T>, position: Int) {
        holder.performBind(items[position])
    }

    override fun initSelectedItems(selected: List<Int>?) { //overridden to ensure only one item is selected
        if (selected != null) {
            Timber.d("Selecting... %s", selected.size)
            if (selected.isNotEmpty()) {
                items[selected[0]].checked = true
            }
        } else {
            Timber.d("selection is null: resetting to 0")
            for (item in items) {
                item.checked = false
            }
        }

        listener?.onItemSelected(selectedItem)
        notifyDataSetChanged()
    }


}

class SingleSelectViewHolder<T>(
        val adapter: SingleSelectAdapter<T>,
        val binding: ItemCheckboxBinding,
        val listener: SingleSelectListener<T>? = null
) :
        RecyclerView.ViewHolder(binding.root) where T : SelectModel {

    fun performBind(item: T?) {
        binding.checkBox.text = item?.getText()
        binding.checkBox.isChecked = item?.checked ?: false
        binding.root.setOnClickListener {
            adapter.selectedItem = item
            deselectOthers(adapterPosition)
            listener?.onItemSelected(item)
        }
    }

    private fun deselectOthers(position: Int) {
        for (index in 0 until adapter.itemCount) {
            adapter.items[index].checked = index == position
            adapter.notifyItemChanged(index)
        }
    }
}

interface SingleSelectListener<T> where T : SelectModel {
    fun onItemSelected(item: T?)
}

interface SelectModel {
    fun getText(): String
    var checked: Boolean
}