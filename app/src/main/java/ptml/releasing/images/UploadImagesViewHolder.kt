package ptml.releasing.images

import androidx.recyclerview.widget.RecyclerView
import ptml.releasing.app.utils.image.ImageLoader
import ptml.releasing.databinding.ItemImageBinding
import ptml.releasing.images.model.Image

/**
Created by kryptkode on 8/5/2019
 */
class UploadImagesViewHolder(
    private val binding: ItemImageBinding,
    private val imageLoader: ImageLoader,
    private val listener: UploadImageAdapter.UploadImageListener
) : RecyclerView.ViewHolder(binding.root) {

    fun performBind(image: Image) {
        imageLoader.loadImage(image.file, binding.image)
        binding.image.setOnClickListener {
            listener.onItemClick(image, adapterPosition)
        }
    }
}