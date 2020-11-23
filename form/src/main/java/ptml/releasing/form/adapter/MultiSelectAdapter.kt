package ptml.releasing.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.form.databinding.FormItemCheckboxBinding
import timber.log.Timber

class MultiSelectAdapter<T> :
    BaseSelectAdapter<MultiSelectViewHolder<T>, T>() where T : SelectModel {

    val selectedItems = mutableMapOf<Int, T>()
    var listener: MultiSelectListener<T>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSelectViewHolder<T> {
        return MultiSelectViewHolder(
            this,
            FormItemCheckboxBinding.inflate(LayoutInflater.from(parent.context), null, false),
            listener
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MultiSelectViewHolder<T>, position: Int) {
        holder.performBind(items.values.toList()[position])
    }

    override fun initSelectedItems(selected: List<Int>?) {
        if (selected != null) {
            Timber.d("Selecting... %s", selected.size)
            for (i in selected) {
                val item = items[i]
                if (item != null) {
                    item.checked = true
                    selectedItems[item.id()] = item

                }


            }
        } else {
            Timber.d("selection is null: resetting to 0")
            for (item in items.values) {
                item.checked = false
            }
            selectedItems.clear()
        }

        listener?.onItemsSelected(selectedItems)
        notifyDataSetChanged()
    }
}

class MultiSelectViewHolder<T>(
    val adapter: MultiSelectAdapter<T>,
    val binding: FormItemCheckboxBinding,
    val listener: MultiSelectListener<T>? = null
) :
    RecyclerView.ViewHolder(binding.root) where T : SelectModel {

    fun performBind(item: T?) {
        Timber.w("TEXT: %s", item?.text())
        binding.checkBox.text = item?.text()
        binding.checkBox.isChecked = item?.checked ?: false
        binding.root.setOnClickListener {
            if (item != null) {
                item.checked = binding.checkBox.isChecked
                if (item.checked) {
                    adapter.selectedItems.put(item.id(), item)
                } else {
                    adapter.selectedItems.remove(item.id())
                }
                listener?.onItemsSelected(adapter.selectedItems)
            }
        }
    }
}

interface MultiSelectListener<T> where T : SelectModel {
    fun onItemsSelected(item: Map<Int, T>)
}

