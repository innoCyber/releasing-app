package ptml.releasing.images

import androidx.lifecycle.LiveData
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.SingleLiveEvent
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import javax.inject.Inject

/**
Created by kryptkode on 8/5/2019
 */
class UploadImagesViewModel @Inject constructor(
    updateChecker: RemoteConfigUpdateChecker,
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    private val openCamera = SingleLiveEvent<Unit>()
    fun getOpenCameraState(): LiveData<Unit> = openCamera

    fun handleOpenCamera(){
        openCamera.value = Unit
    }
}