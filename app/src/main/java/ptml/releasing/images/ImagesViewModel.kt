package ptml.releasing.images

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import ptml.releasing.R
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
class ImagesViewModel @Inject constructor(
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

    private val deleteNotify = SingleLiveEvent<NetworkState>()
    fun getDeleteNotifyState(): LiveData<NetworkState> = deleteNotify

    private var imagesMap = mutableMapOf<String, Image>()
    private var cargoCode: String? = null


    fun init(cargoCode: String) {
        this.cargoCode = cargoCode
        if (loadingState.value != NetworkState.LOADING) {
            loadingState.postValue(NetworkState.LOADING)

            launch(appCoroutineDispatchers.db) {
                try {
                    imagesMap.clear()
                    imagesMap.putAll(repository.getImages(cargoCode))
                    imageFiles.postValue(imagesMap.values.toList())
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
        launch(appCoroutineDispatchers.db) {
            try {
                cargoCode?.let {
                    if (fileUtils.isValidImageFile(file)) {
                        val image = createImage(file)
                        repository.addImage(it, image)
                        addFile.postValue(image)
                    } else {
                        Timber.d("Added file ${file.absolutePath} is not a valid image")
                    }
                }
            } catch (e: Throwable) {
                handleError(e)
            }
        }
    }

    fun handleFileRemoved(file: File) {
        launch(appCoroutineDispatchers.db) {
            try {
                cargoCode?.let {
                    if (fileUtils.isValidImageFile(file)) {
                        val image = createImage(file)
                        repository.removeImage(it, image)
                        removeFile.postValue(image)

                    } else {
                        Timber.d("Removed file ${file.absolutePath} is not a valid image")
                    }
                }
            } catch (e: Throwable) {
                handleError(e)
            }
        }
    }

    fun createImageFile(cargoCode: String): File {
        return repository.createImageFile(cargoCode)
    }

    fun getRootPath(cargoCode: String?): String {
        return repository.getRootPath(cargoCode)
    }

    private fun createImage(imageFile: File): Image {
        return repository.createImage(imageFile)
    }

    fun deleteFiles(
        imageList: List<Image>,
        cargoCode: String?
    ) {
        //delete files
        //remove from prefs
        //remove from server
        if (deleteNotify.value != NetworkState.LOADING) {
            deleteNotify.postValue(NetworkState.LOADING)
            launch(appCoroutineDispatchers.db) {
                try {
                    repository.delete(imageList, cargoCode)
                    loadingState.postValue(NetworkState.LOADED)
                } catch (e: Throwable) {
                    loadingState.postValue(NetworkState.error(e))
                    handleError(e)
                }
            }

        }
    }
}