package ptml.releasing.voyage.view


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import ptml.releasing.databinding.ItemVoyageBinding

class VoyageAdapter(val listener: VoyageClickListener) : RecyclerView.Adapter<VoyageViewHolder>() {
    private val voyageList = mutableListOf<ReleasingVoyage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VoyageViewHolder(
        ItemVoyageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ), listener
    )

    override fun getItemCount() = voyageList.size

    override fun onBindViewHolder(holder: VoyageViewHolder, position: Int) {
        holder.performBind(voyageList[position])
    }

    fun setItems(items: List<ReleasingVoyage>) {
        voyageList.clear()
        voyageList.addAll(items)
        notifyDataSetChanged()
    }

}


class VoyageViewHolder(val binding: ItemVoyageBinding, val listener: VoyageClickListener) :
    RecyclerView.ViewHolder(binding.root) {
    fun performBind(item: ReleasingVoyage) {
        binding.tvItem.text = item.vesselName
        binding.tvItem.isSelected = true
        binding.tvItemLayout.setOnClickListener {
            listener.onItemClick(item)
        }
    }
}

interface VoyageClickListener {
    fun onItemClick(item: ReleasingVoyage?)
}

