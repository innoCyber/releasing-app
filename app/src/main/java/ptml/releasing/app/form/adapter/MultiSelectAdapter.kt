package ptml.releasing.app.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.databinding.ItemCheckboxBinding
import timber.log.Timber

class MultiSelectAdapter<T> : BaseSelectAdapter<MultiSelectViewHolder<T>, T>() where T : SelectModel {

    val selectedItems = mutableListOf<T>()
    var listener: MultiSelectListener<T>? = null

    fun setItems(items:List<T>?){
        this.items.clear()
        this.items.addAll(items ?: mutableListOf())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSelectViewHolder<T> {
        return MultiSelectViewHolder(
            this,
            ItemCheckboxBinding.inflate(LayoutInflater.from(parent.context), null, false),
            listener
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MultiSelectViewHolder<T>, position: Int) {
        holder.performBind(items[position])
    }

}

class MultiSelectViewHolder<T>(
    val adapter: MultiSelectAdapter<T>,
    val binding: ItemCheckboxBinding,
    val listener: MultiSelectListener<T>? = null
) :
    RecyclerView.ViewHolder(binding.root) where T : SelectModel {

    fun performBind(item: T) {
        Timber.w("TEXT: %s", item.getText())
        binding.checkBox.text = item.getText()
        binding.checkBox.isChecked = item.checked
        binding.root.setOnClickListener {
            adapter.selectedItems.add(item)
            item.checked = binding.checkBox.isChecked
            listener?.onItemsSelected(adapter.selectedItems)
        }
    }
}

interface MultiSelectListener<T> where T : SelectModel {
    fun onItemsSelected(item: List<T>)
}

