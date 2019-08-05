package ptml.releasing.images

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.app.utils.image.ImageLoader
import ptml.releasing.databinding.ItemImageBinding
import java.io.File

/**
Created by kryptkode on 8/5/2019
 */

class UploadImageAdapter (
    private val imageLoader: ImageLoader,
    private val listener: UploadImageListener
)  : RecyclerView.Adapter<UploadImagesViewHolder>() {

    private var imagesList = mutableListOf<File>()

    fun setImageList(list: List<File>) {
        imagesList.clear()
        imagesList.addAll(list)
        notifyDataSetChanged()
    }

    fun add(file: File) {
        imagesList.add(file)
        notifyDataSetChanged()
    }

    fun remove(file: File) {
        imagesList.remove(file)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadImagesViewHolder {
        return  UploadImagesViewHolder(ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            imageLoader, listener)
    }

    override fun getItemCount(): Int {
        return  imagesList.size
    }

    override fun onBindViewHolder(holder: UploadImagesViewHolder, position: Int) {
        holder.performBind(imagesList[position])
    }

    interface UploadImageListener{
        fun onItemClick(file: File)
    }
}