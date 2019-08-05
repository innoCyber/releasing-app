package ptml.releasing.app.utils.image

import android.widget.ImageView
import java.io.File

/**
Created by kryptkode on 8/5/2019
 */
interface ImageLoader {
    fun loadImageFromFile(file:File, imageView: ImageView)
}