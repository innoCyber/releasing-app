package ptml.releasing.printer.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.printer.model.Settings
import javax.inject.Inject

class PrinterSettingsViewModel @Inject constructor(
    repository: Repository,
    appCoroutineDispatchers: AppCoroutineDispatchers
) :
    BaseViewModel(repository, appCoroutineDispatchers) {

    private val _printerSettings = MutableLiveData<Settings>()
    private val _close = MutableLiveData<Unit>()

    val printerSettings: LiveData<Settings> = _printerSettings
    val close: LiveData<Unit> = _close


    fun getSettings() {
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            val settings = repository.getSettings()
            withContext(appCoroutineDispatchers.main) {
                _printerSettings.postValue(settings)
            }
        }
    }

    fun setSettings(currentPrinter: String?, currentPrinterName: String?, label: String?) {
        val settings = _printerSettings.value
        compositeJob = CoroutineScope(appCoroutineDispatchers.db).launch {
            settings?.currentPrinter = currentPrinter
            settings?.currentPrinterName = currentPrinterName
            settings?.labelCpclData = label

            repository.saveSettings(settings)
            withContext(appCoroutineDispatchers.main) {
                _close.postValue(Unit)
            }
        }
    }


}