package ptml.releasing.app.utils.image

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import ptml.releasing.R
import ptml.releasing.app.views.PlaceHolderDrawable
import java.io.File

/**
Created by kryptkode on 8/5/2019
 */

class ImageLoaderImpl(context: Context) : ImageLoader{

    private val placeHolderDrawable = PlaceHolderDrawable(context)

    override fun loadImage(imageUri:String?, imageView: ImageView) {
        GlideApp.with(imageView)
            .load(imageUri)
            .placeholder(placeHolderDrawable)
            .error(ColorDrawable(ContextCompat.getColor(imageView.context, R.color.colorGray)))
            .into(imageView)
    }
}