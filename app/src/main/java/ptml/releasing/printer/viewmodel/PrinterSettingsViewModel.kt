package ptml.releasing.printer.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Event
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.printer.model.Settings
import javax.inject.Inject

class PrinterSettingsViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers, updateChecker: RemoteConfigUpdateChecker
) :
    BaseViewModel(updateChecker, repository, appCoroutineDispatchers) {

    private val printerSettings = MutableLiveData<Settings>()
    fun getPrinterSettings(): LiveData<Settings> = printerSettings

    private val close = MutableLiveData<Event<Unit>>()
    fun getClose(): LiveData<Event<Unit>> = close


    fun getSettings() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val settings = repository.getSettings()
            withContext(appCoroutineDispatchers.main) {
                printerSettings.postValue(settings)
            }
        }
    }

    fun setSettings(currentPrinter: String?, currentPrinterName: String?, label: String?) {
        val settings = printerSettings.value
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            settings?.currentPrinter = currentPrinter
            settings?.currentPrinterName = currentPrinterName
            settings?.labelCpclData = label

            repository.saveSettings(settings)
            withContext(appCoroutineDispatchers.main) {
                close.postValue(Event(Unit))
            }
        }
    }


}