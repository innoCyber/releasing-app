package ptml.releasing.images

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.FileUtils
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.SingleLiveEvent
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.images.model.Image
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
Created by kryptkode on 8/5/2019
 */
class UploadImagesViewModel @Inject constructor(
    val fileUtils: FileUtils,
    updateChecker: RemoteConfigUpdateChecker,
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    private val openCamera = SingleLiveEvent<Unit>()
    fun getOpenCameraState(): LiveData<Unit> = openCamera

    private val addFile = SingleLiveEvent<Image>()
    fun getAddFileState(): LiveData<Image> = addFile

    private val removeFile = SingleLiveEvent<Image>()
    fun getRemoveFileState(): LiveData<Image> = removeFile

    private val imageFiles = MutableLiveData<List<Image>>()
    fun getImageFilesState(): LiveData<List<Image>> = imageFiles

    private val loadingState = MutableLiveData<NetworkState>()
    fun getLoadingState(): LiveData<NetworkState> = loadingState


    fun init(rootPath: String) {
        val rootDir = File(rootPath)
        loadAllImageFilesInDir(rootDir)
    }

    private fun loadAllImageFilesInDir(rootDir: File) {
        if (loadingState.value != NetworkState.LOADING) {
            loadingState.postValue(NetworkState.LOADING)
            CoroutineScope(appCoroutineDispatchers.db).launch {
                try {
                    val list = fileUtils.provideImageFiles(rootDir).map { createImage(it) }
                    imageFiles.postValue(list)
                    loadingState.postValue(NetworkState.LOADED)
                } catch (e: Throwable) {
                    loadingState.postValue(NetworkState.error(e))
                    handleError(e)
                }
            }
        }


    }

    private fun handleError(e: Throwable) {
        Timber.e(e)
    }

    fun handleOpenCamera() {
        openCamera.value = Unit
    }

    fun handleFileAdded(file: File) {
        try {
            if (fileUtils.isValidImageFile(file)) {
                addFile.postValue(createImage(file))
            } else {
                Timber.d("Added file ${file.absolutePath} is not a valid image")
            }
        } catch (e: Throwable) {
            handleError(e)
        }

    }

    fun handleFileRemoved(file: File) {
        try {
            if (fileUtils.isValidImageFile(file)) {
                removeFile.postValue(createImage(file))
            } else {
                Timber.d("Removed file ${file.absolutePath} is not a valid image")
            }
        } catch (e: Throwable) {
            handleError(e)
        }
    }

    private fun createImage(file: File): Image {
        return Image(file)
    }
}