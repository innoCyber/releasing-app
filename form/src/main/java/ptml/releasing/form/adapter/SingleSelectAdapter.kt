package ptml.releasing.form.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.form.databinding.FormItemCheckboxBinding
import timber.log.Timber

class SingleSelectAdapter<T> :
    BaseSelectAdapter<SingleSelectViewHolder<T>, T>() where T : SelectModel {
    var listener: SingleSelectListener<T>? = null
    var selectedItem: T? = null
    var selectedItemPosition: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleSelectViewHolder<T> {
        return SingleSelectViewHolder(
            this,
            FormItemCheckboxBinding.inflate(LayoutInflater.from(parent.context), null, false),
            listener
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: SingleSelectViewHolder<T>, position: Int) {
        holder.performBind(items.values.toList()[position])
    }

    override fun initSelectedItems(selected: List<Int>?) { //overridden to ensure only one item is selected
        if (selected != null) {
            Timber.d("Selecting... %s", selected.size)
            if (selected.isNotEmpty()) {
                val item = items[selected[0]]
                Timber.d("Selected: $item")
                item?.checked = true
                selectedItem = item
                listener?.onItemSelected(selectedItem)
            }
        } else {
            Timber.d("selection is null: resetting to 0")
            selectedItem = null
            for (item in items.values) {
                item.checked = false
            }
        }


        notifyDataSetChanged()
    }


}

class SingleSelectViewHolder<T>(
    val adapter: SingleSelectAdapter<T>,
    val binding: FormItemCheckboxBinding,
    val listener: SingleSelectListener<T>? = null
) :
    RecyclerView.ViewHolder(binding.root) where T : SelectModel {

    fun performBind(item: T?) {
        binding.checkBox.text = item?.text()
        binding.checkBox.isChecked = item?.checked ?: false
        binding.root.setOnClickListener {
            adapter.selectedItem = item
            adapter.selectedItemPosition = adapterPosition
            deselectOthers(adapterPosition)
            listener?.onItemSelected(item)
        }
    }

    private fun deselectOthers(position: Int) {
        for (index in 0 until adapter.itemCount) {
            (adapter.items.values.toList()[index] as SelectModel).checked = index == position
            adapter.notifyItemChanged(index)
        }
    }
}


interface SingleSelectListener<T> where T : SelectModel {
    fun onItemSelected(item: T?)
}

interface SelectModel {
    fun text(): String
    fun id(): Int
    fun position(): Int
    var checked: Boolean
}