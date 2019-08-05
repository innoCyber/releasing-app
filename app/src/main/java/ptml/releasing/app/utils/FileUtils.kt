package ptml.releasing.app.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
Created by kryptkode on 8/5/2019
 */

class FileUtils @Inject constructor(private val context: Context){

    @Throws(IOException::class)
    fun createImageFile(fileNamePrefix:String): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = provideRootDir(fileNamePrefix)
        return File.createTempFile(
            "${fileNamePrefix}_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun provideRootDir(fileNamePrefix: String): File {
        val storageDir = File(context.filesDir, fileNamePrefix)
        if(!storageDir.exists()){
            storageDir.mkdirs()
        }
        return storageDir
    }

    @Throws(IOException::class)
    fun provideFiles(directoryName:String): List<File> {
        val rootDir = provideRootDir(directoryName)
        return rootDir.listFiles().toList()
    }
}