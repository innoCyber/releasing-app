package ptml.releasing.images

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.FileUtils
import ptml.releasing.app.utils.image.ImageDirObserver
import ptml.releasing.app.utils.image.ImageLoader
import ptml.releasing.app.views.SpacesItemDecoration
import ptml.releasing.damages.model.AssignedDamage
import ptml.releasing.databinding.ActivityUploadImagesBinding
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import javax.inject.Inject

/**
Created by kryptkode on 8/5/2019
 */
class UploadImagesActivity : BaseActivity<UploadImagesViewModel, ActivityUploadImagesBinding>() {

    @Inject
    lateinit var fileUtils: FileUtils

    @Inject
    lateinit var imageLoader: ImageLoader

    private var mCurrentPhotoPath: String? = null
    private var cargoCode: String? = null

    private val adapterListener = object : UploadImageAdapter.UploadImageListener {
        override fun onItemClick(file: File) {
            //TODO: Open image full screen
        }
    }

    private var adapter : UploadImageAdapter? = null

    private val fileObserverListener = object : ImageDirObserver.ImageDirListener {
        override fun onCreate(path: String?) {

        }

        override fun onDelete(path: String?) {

        }

    }

    private var fileObserver: ImageDirObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cargoCode = getUploadExtra(getDataFromIntent())
        adapter = UploadImageAdapter(imageLoader, adapterListener)
        initViews()
        initObservers()
        initFileObserver()
    }


    private fun initFileObserver() {
        fileObserver = ImageDirObserver(getRootPath(), fileObserverListener)
        lifecycle.addObserver(fileObserver ?: return)
    }

    private fun initViews() {
        supportActionBar?.title = getString(R.string.upload_image_title, cargoCode)
        binding.recyclerView.adapter = adapter
        val spanCount = 2
        binding.recyclerView.layoutManager = GridLayoutManager(this, spanCount)
        binding.recyclerView.addItemDecoration(
            SpacesItemDecoration(
                resources.getDimension(R.dimen.grid_margin).toInt(),
                spanCount
            )
        )

    }

    private fun initObservers() {
        viewModel.getOpenCameraState().observe(this, Observer {
            openCamera()
        })
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Timber.e(ex)
                    notifyUser(getString(R.string.change_pic_create_file_error))
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "$packageName.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                }
            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        return fileUtils.createImageFile(cargoCode ?: "").apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    fun getRootPath(): String {
        return "${filesDir.absolutePath}/$cargoCode"
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            //open cropper
        }
    }

    override fun getLayoutResourceId() = R.layout.activity_upload_images
    override fun getBindingVariable() = BR.viewModel
    override fun getViewModelClass() = UploadImagesViewModel::class.java


    companion object {
        private const val CAMERA_REQUEST = 1
        var currentImages = mutableListOf<File>()
        private const val CARGO_CODE_EXTRA = "CARGO_CODE_EXTRA"
        fun createUploadExtra(cargoCode: String?): Bundle {
            val data = Bundle()
            data.putString(CARGO_CODE_EXTRA, cargoCode)
            return data
        }

        fun getUploadExtra(data: Bundle?): String? {
            return data?.getString(CARGO_CODE_EXTRA)
        }
    }
}