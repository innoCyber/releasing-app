package ptml.releasing.images.upload

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.exception.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.Status
import ptml.releasing.app.utils.image.ImageDirObserver
import ptml.releasing.app.utils.image.ImageLoader
import ptml.releasing.app.views.SpacesItemDecoration
import ptml.releasing.databinding.ActivityUploadImagesBinding
import ptml.releasing.images.ImagesViewModel
import ptml.releasing.images.model.Image
import ptml.releasing.images.viewer.ImageViewerActivity
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject

/**
Created by kryptkode on 8/5/2019
 */
class UploadImagesActivity : BaseActivity<ImagesViewModel, ActivityUploadImagesBinding>() {


    @Inject
    lateinit var imageLoader: ImageLoader

    private var currentPhotoPath: String? = null
    private var cargoCode: String? = null

    private val adapterListener = object :
        UploadImageAdapter.UploadImageListener {
        override fun onItemClick(image: Image, position: Int) {
            //TODO: Open image full screen
            val data = ImageViewerActivity.createExtras(cargoCode, position)
            startNewActivity(ImageViewerActivity::class.java, data = data)
        }

        override fun tryDeleteFiles(imageList: List<Image>) {
            viewModel.deleteFiles( imageList, cargoCode)
        }

        override fun showDeleteConfirm(moreThanOne:Boolean) {
            val dialogFragment = InfoDialog.newInstance(
                title = getString(R.string.delete_confirm_title),
                message = if(moreThanOne) getString(R.string.delete_confirm_msgs) else getString(R.string.delete_confirm_msg),
                buttonText = getString(android.R.string.ok),
                listener = object : InfoDialog.InfoListener {
                    override fun onConfirm() {
                        adapter?.deleteSelectedFiles()
                    }
                })
            dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
        }
    }

    private var adapter: UploadImageAdapter? = null

    private var fileObserver: ImageDirObserver? = null

    private val fileObserverListener = object : ImageDirObserver.ImageDirListener {
        override fun onCreate(path: String?) {
            fileObserver?.let { observer ->
                path?.let {
                    val file = File(observer.getAbsolutePath(it))
                    viewModel.handleFileAdded(file)
                }
            }
        }

        override fun onDelete(path: String?) {
            fileObserver?.let { observer ->
                path?.let {
                    val file = File(observer.getAbsolutePath(it))
                    runOnUiThread {
                        viewModel.handleFileRemoved(file)
                    }
                }
            }

        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cargoCode = getUploadExtra(
            getDataFromIntent()
        )
        initViews()
        initObservers()
        initFileObserver()
        initInitialData()
    }

    private fun initInitialData() {
        viewModel.init(cargoCode ?: "")
    }


    private fun initFileObserver() {
        fileObserver = ImageDirObserver(getRootPathCompressed(), fileObserverListener)
        lifecycle.addObserver(fileObserver ?: return)
    }

    private fun initViews() {
        showUpEnabled(true)
        setActionBarTitle(getString(R.string.upload_image_title, cargoCode))

        adapter = UploadImageAdapter(this, imageLoader, adapterListener)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setEmptyView(binding.emptyState.emptyView)
        val spanCount = 2
        binding.recyclerView.layoutManager = GridLayoutManager(this, spanCount)
        binding.recyclerView.addItemDecoration(
            SpacesItemDecoration(
                resources.getDimension(R.dimen.grid_margin).toInt(),
                spanCount
            )
        )

        binding.emptyState.tvEmpty.setOnClickListener {
            viewModel.handleOpenCamera()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            initInitialData()
        }


    }

    private fun initObservers() {
        viewModel.getOpenCameraState().observe(this, Observer {
            openCamera()
        })

        viewModel.getAddFileState().observe(this, Observer { file ->
            adapter?.add(file)
        })

        viewModel.getRemoveFileState().observe(this, Observer { file ->
            adapter?.remove(file)
        })

        viewModel.getImageFilesState().observe(this, Observer {
            adapter?.setImageList(it)
        })

        viewModel.getLoadingState().observe(this, Observer {
            binding.swipeRefreshLayout.isRefreshing = it == NetworkState.LOADING

            if (it?.status == Status.FAILED) {
                val error = ErrorHandler(this).getErrorMessage(it.throwable)
                notifyUser(binding.root, error)
            }
        })
        viewModel.getDeleteNotifyState().observe(this, Observer {
            binding.swipeRefreshLayout.isRefreshing = it == NetworkState.LOADING

            if (it?.status == Status.FAILED) {
                notifyUser(binding.root, getString(R.string.delete_fail_message))
                adapter?.restoreRecentlyDeletedItems()
            }else{
                if(it == NetworkState.LOADING){
                    notifyUser(binding.root, getString(R.string.deleting_message))
                }else{
                    notifyUser(binding.root, getString(R.string.delete_success_message))
                }
            }
        })
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: Exception) {
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
                    startActivityForResult(takePictureIntent,
                        CAMERA_REQUEST
                    )
                }
            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        return viewModel.createImageFile(cargoCode ?: "").apply {
            // Save a file: path for use with ACTION_VIEW intents
            Timber.d("Created image file with path: $absolutePath")
            currentPhotoPath = absolutePath
        }
    }

    fun getRootPath(): String {
        return viewModel.getRootPath(cargoCode)
    }

    private fun getRootPathCompressed(): String {
        return viewModel.getRootPathCompressed(cargoCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            //compressImage
            viewModel.compressImageFile(currentPhotoPath, cargoCode)
        }
    }

    override fun getLayoutResourceId() = R.layout.activity_upload_images
    override fun getBindingVariable() = BR.viewModel
    override fun getViewModelClass() = ImagesViewModel::class.java


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