package ptml.releasing.app.utils.image.viewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.FileUtils
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.images.model.Image
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
Created by kryptkode on 8/6/2019
 */
class ImageViewerViewModel @Inject constructor(
    private val fileUtils: FileUtils,
    updateChecker: RemoteConfigUpdateChecker,
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

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
                    imageFiles.postValue(fileUtils.provideImageFiles(rootDir).map{
                        createImage(it)
                    })
                    loadingState.postValue(NetworkState.LOADED)
                } catch (e: Throwable) {
                    handleError(e)
                }
            }
        }
    }


    private fun handleError(e: Throwable) {
        loadingState.postValue(NetworkState.error(e))
        Timber.e(e)
    }

    private fun createImage(file: File): Image {
        return Image(file)
    }

}