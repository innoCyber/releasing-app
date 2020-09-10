package ptml.releasing.utils

import android.service.notification.Condition
import ptml.releasing.images.model.Image

/**
Created by kryptkode on 8/7/2019
 */

const val SIZE = 5

val uploadedImages by lazy {
    provideImagesMap(true)
}


val notUploadedImages by lazy {
    provideImagesMap(false)
}

fun provideImagesMap(uploaded:Boolean) : Map<String, Image>{
    val map = mutableMapOf<String, Image>()
    for(i in 0 until  5){
        val image  = createImage(i, uploaded)
        map.put(image.name!!, image)
    }
    return map
}

fun createImage(index:Int, condition: Boolean):Image{
    return  Image(if(condition)"file://whatever/image.jpg" else "http://image_urlcom", "name:$index", !condition)
}