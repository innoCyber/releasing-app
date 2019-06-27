package ptml.releasing.quick_remarks.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


import ptml.releasing.databinding.ItemQuickRemarkBinding
import ptml.releasing.quick_remarks.model.QuickRemark

class QuickRemarkAdapter(val listener: QuickRemarkListener) : RecyclerView.Adapter<QuickRemarkViewHolder>() {
    val remarkList = mutableListOf<QuickRemark?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = QuickRemarkViewHolder(ItemQuickRemarkBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener)

    override fun getItemCount() = remarkList.size

    override fun onBindViewHolder(holder: QuickRemarkViewHolder, position: Int) {
        holder.performBind(remarkList.get(position))
    }

}


class QuickRemarkViewHolder(val binding: ItemQuickRemarkBinding, val listener: QuickRemarkListener) : RecyclerView.ViewHolder(binding.root) {
    fun performBind(item: QuickRemark?) {
        binding.tvItem.text = item?.name
        binding.tvItem.isSelected = true
        binding.tvItemLayout.setOnClickListener {
            listener.onItemClick(item)
        }
    }
}

interface QuickRemarkListener {
    fun onItemClick(item: QuickRemark?)
}

