package ptml.releasing.download_damages.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.download_damages.model.Damage
import ptml.releasing.databinding.ItemDamageBinding

class DamageAdapter(val listener: DamageListener) : RecyclerView.Adapter<DamageViewHolder>() {
    val damageList = mutableListOf<Damage?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DamageViewHolder(ItemDamageBinding.inflate(LayoutInflater.from(parent.context), parent, false), listener)

    override fun getItemCount() = damageList.size

    override fun onBindViewHolder(holder: DamageViewHolder, position: Int) {
        holder.performBind(damageList.get(position))
    }

}


class DamageViewHolder(val binding: ItemDamageBinding, val listener: DamageListener) : RecyclerView.ViewHolder(binding.root) {
    fun performBind(item: Damage?) {
        binding.tvItem.text = item?.description
        binding.tvItemLayout.setOnClickListener {
            listener.onItemClick(item)
        }
    }
}

interface DamageListener {
    fun onItemClick(item: Damage?)
}

