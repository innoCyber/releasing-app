package ptml.releasing.configuration.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import ptml.releasing.app.data.domain.repository.ImeiRepository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.livedata.Event
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.configuration.models.*
import timber.log.Timber
import javax.inject.Inject


class ConfigViewModel @Inject constructor(
    private val imeiRepository: ImeiRepository,
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) : BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    private val _cargoTypes = MutableLiveData<List<CargoType>>()
    val cargoTypes: LiveData<List<CargoType>> = _cargoTypes

    private val operationStepList = MutableLiveData<MutableList<ReleasingOperationStep>>()
    fun getOperationStepList(): LiveData<MutableList<ReleasingOperationStep>> = operationStepList

    private val voyageList = MutableLiveData<List<ptml.releasing.configuration.models.ReleasingVoyage>>()
    fun getVoyageList(): LiveData<List<ptml.releasing.configuration.models.ReleasingVoyage>> = voyageList

    private val shippingLineList = MutableLiveData<List<ShippingLine>>()
    fun getShippingLine(): LiveData<List<ShippingLine>> = shippingLineList

    private val terminalList = MutableLiveData<List<ReleasingTerminal>>()
    fun getTerminalList(): LiveData<List<ReleasingTerminal>> = terminalList

    val networkState = MutableLiveData<Event<NetworkState>>()
    fun getNetworkState(): LiveData<Event<NetworkState>> = networkState

    val savedSuccess = MutableLiveData<Event<Boolean>>()
    fun getSavedSuccess(): LiveData<Event<Boolean>> = savedSuccess

    val configuration = _configuration

    lateinit var adminConfigResponse: AdminConfigResponse

    fun getConfig(imei: String) {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return
        networkState.postValue(
            Event(
                NetworkState.LOADING
            )
        )
        compositeJob = CoroutineScope(dispatchers.network).launch {
            try {
                adminConfigResponse = repository.getAdminConfigurationAsync(imei).await()
                val config = repository.getSelectedConfigAsync()

                voyageList.postValue(voyageRepository.downloadRecentVoyages().map { ReleasingVoyage().apply { id = it.id; value = it.vesselName }  })
                withContext(dispatchers.main) {
                    _cargoTypes.postValue(adminConfigResponse.cargoTypeList)
                    val modifiedTerminal = modifyTerminal()
                    terminalList.postValue(modifiedTerminal)
                    operationStepList.postValue(adminConfigResponse.operationStepList)
                    shippingLineList.postValue(adminConfigResponse.shippingLineList)
                    _configuration.postValue(config)
                    networkState.postValue(Event(NetworkState.LOADED))
                }
            } catch (e: Throwable) {

                networkState.postValue(
                    Event(
                        NetworkState.error(e)
                    )
                )
            }
        }


    }


    private fun modifyTerminal(): MutableList<ReleasingTerminal>? {
        val modifiedTerminal = adminConfigResponse.terminalList
            ?.toMutableList()
        modifiedTerminal?.add(0, ReleasingTerminal(-1)
            .apply {
                value = "No terminal assigned"
            })
        return modifiedTerminal
    }

    fun setConfig(
        terminal: ReleasingTerminal?,
        operationStep: ReleasingOperationStep?,
        cargoType: CargoType?,
        shippingLine: ShippingLine?,
        voyage: ptml.releasing.configuration.models.ReleasingVoyage?,
        checked: Boolean,
        imei: String
    ) {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return
        networkState.postValue(Event(NetworkState.LOADING))


        val configuration =
            Configuration(
                terminal ?: return,
                operationStep ?: return,
                cargoType ?: return,
                shippingLine ?: return,
                voyage?: return,
                checked,
                "",
                0,
                ""
            )
        compositeJob = CoroutineScope(dispatchers.db).launch {
            try {
                repository.setSavedConfigAsync(configuration)
                println("Aminu $configuration")
                val result = repository.setConfigurationDeviceAsync(
                    cargoTypeId = cargoType.id,
                    terminal = terminal.id,
                    operationStepId = operationStep.id,
                    imei = imei
                ).await()
                Timber.d("Result gotten: %s", result)
                repository.setConfigured(true)
                savedSuccess.postValue(Event(true))
                networkState.postValue(Event(NetworkState.LOADED))
            } catch (e: Throwable) {
                Timber.e(e)
                networkState.postValue(
                    Event(NetworkState.error(e))
                )
            }

        }
    }

    fun cargoTypeSelected(cargoType: CargoType) {
        if (this::adminConfigResponse.isInitialized) {
            operationStepList.postValue(getOperationStepForCargo(cargoType))
        }

    }

    fun refreshConfiguration(imei: String) {
        if (networkState.value?.peekContent() == NetworkState.LOADING) return
        networkState.postValue(
            Event(
                NetworkState.LOADING
            )
        )

        compositeJob = CoroutineScope(dispatchers.network).launch {
            try {
                Timber.d("Refreshing configuration")
                val response = repository.downloadAdminConfigurationAsync(imei).await()

                withContext(dispatchers.main) {
                    Timber.d("Configuration gotten: %s", response)
                    _cargoTypes.postValue(response.cargoTypeList)
                    Timber.d("Checking if there is a saved configuration")
                    Timber.e("Loading done: %s", response)
                    networkState.postValue(
                        Event(
                            NetworkState.LOADED
                        )
                    )
                }
            } catch (e: Throwable) {
                Timber.e(e)
                networkState.postValue(
                    Event(
                        NetworkState.error(e)
                    )
                )
            }
        }


    }


    private fun getOperationStepForCargo(cargoType: CargoType): MutableList<ReleasingOperationStep> {
        val list = mutableListOf<ReleasingOperationStep>()
        for (operationStep in adminConfigResponse.operationStepList ?: mutableListOf()) {
            if (operationStep.cargo_type == cargoType.id) {
                list.add(operationStep)
            }
        }
        return list
    }

    fun getLastSelectedVoyage(): LiveData<ReleasingVoyage> {
        val result = MutableLiveData<ReleasingVoyage>(ReleasingVoyage(-1, "None"))
        viewModelScope.launch {
            result.value = voyageRepository.getLastSelectedVoyage()
        }
        return result
    }


    fun updateImei(imei: String) {
        viewModelScope.launch {
            imeiRepository.setIMEI(imei)
        }
    }
}