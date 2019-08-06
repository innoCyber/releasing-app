package ptml.releasing.app.utils.image.viewer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil


import androidx.fragment.app.Fragment

import java.io.File

import ptml.releasing.R
import ptml.releasing.app.utils.image.ImageLoader
import ptml.releasing.databinding.FragmentImageViewBinding
import ptml.releasing.images.model.Image
import timber.log.Timber


class ImageViewerFragment : Fragment() {

    private lateinit var binding: FragmentImageViewBinding
    private var listener: ImageViewListener? = null
    var imageLoader: ImageLoader? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as ImageViewListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_image_view, container, false)
        Timber.e("IMAGE TAG : ${binding.image.tag}")

        val imageFile = getImageFromArgs(arguments)?.file

        Timber.e("ImageFile: ${imageFile?.path}")
        imageLoader?.loadImage(imageFile ?: File(""), binding.image)


        binding.image.setOnClickListener {
            listener?.onPhotoClick()
        }
        return binding.root
    }


    interface ImageViewListener {
        fun onPhotoClick()
    }

    companion object {

        private const val IMAGE_ARG = "path_arg"

        fun newInstance(image: Image, imageLoader: ImageLoader): ImageViewerFragment {
            val fragment = ImageViewerFragment()
            val bundle = Bundle()
            bundle.putParcelable(IMAGE_ARG, image)
            fragment.arguments = bundle
            fragment.imageLoader = imageLoader
            return fragment
        }

        fun getImageFromArgs(arguments: Bundle?): Image? {
            return arguments?.getParcelable(IMAGE_ARG)
        }
    }
}