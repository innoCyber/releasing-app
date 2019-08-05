package ptml.releasing.images

import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.app.utils.image.ImageLoader
import ptml.releasing.databinding.ItemImageBinding
import java.io.File

/**
Created by kryptkode on 8/5/2019
 */
class UploadImagesViewHolder(
    private val binding: ItemImageBinding,
    private val imageLoader: ImageLoader,
    private val listener: UploadImageAdapter.UploadImageListener
) : RecyclerView.ViewHolder(binding.root) {

    fun performBind(file: File) {
        imageLoader.loadImageFromFile(file, binding.image)
        binding.root.setOnClickListener {
            listener.onItemClick(file)
        }
    }
}