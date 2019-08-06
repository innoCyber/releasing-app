package ptml.releasing.app.utils

import android.content.Context
import android.net.Uri
import ptml.releasing.app.utils.Constants.IMAGE_EXT
import ptml.releasing.app.utils.Constants.VALID_IMAGE_SIZE_THRESHOLD
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
Created by kryptkode on 8/5/2019
 */

class FileUtils @Inject constructor(private val context: Context) {

    @Throws(IOException::class)
    fun createImageFile(fileNamePrefix: String): File {
        // Create an image file name
        return File.createTempFile(
            "${fileNamePrefix}_${createTempName()}_", /* prefix */
            ".$IMAGE_EXT", /* suffix */
            provideRootDir(fileNamePrefix) /* directory */
        )
    }

    private fun createTempName(): String {
        return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }

    private fun provideRootDir(fileNamePrefix: String): File {
        val storageDir = File(context.filesDir, fileNamePrefix)
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return storageDir
    }

    @Throws(IOException::class, SecurityException::class)
    fun provideImageFiles(rootDir: File): List<File> {
        return rootDir.listFiles { dir, name ->
            isValidImageFile(File(dir, name))
        }.toList()
    }


    @Throws(SecurityException::class)
    fun isValidImageFile(file: File): Boolean {
        val fileSize = file.length()
        Timber.d("ImageSize: $fileSize")
        return file.extension == IMAGE_EXT && file.isFile && fileSize > VALID_IMAGE_SIZE_THRESHOLD
    }

    fun getFileUri(file: File): String {
        return Uri.fromFile(file).toString()
    }

    @Throws(SecurityException::class)
    fun deleteFile(file: File) {
        file.delete()
    }

    fun getFileName(imageFile: File): String {
        return imageFile.name
    }

    fun getRootPath(cargoCode: String?): String {
        return "${context.filesDir}/$cargoCode"
    }
}