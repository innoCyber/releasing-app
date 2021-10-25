package ptml.releasing.cargo_search.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ptml.releasing.app.base.BaseViewModel
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.repository.ImeiRepository
import ptml.releasing.app.form.FormMappers
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.remoteconfig.RemoteConfigUpdateChecker
import ptml.releasing.cargo_search.model.PODOperationStep
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

 class NoNetworkPODViewModel (
    val repository: Repository
) : ViewModel() {

    private val _podSpinnerItems = MutableLiveData<ArrayList<PODOperationStep>>()
    val podSpinnerItems: LiveData<ArrayList<PODOperationStep>> = _podSpinnerItems

    fun getFormConfig(){
        GlobalScope.launch {
            val result = repository.getFormConfigAsync().await()
            // _podSpinnerItems.value = result.data as ArrayList<PODOperationStep>
            if (result.data !=null){
                for (i in result.data){
                    if (i.title.toLowerCase(Locale.ROOT).contains("pod")){
                        _podSpinnerItems.value = i.options as ArrayList<PODOperationStep>
                        Log.d("kuygfs", "getFormConfig: ${i.options}")
//                    for(items in i.options){
//                        //.value = items.name
//                        Log.d("dgrgrw", "getFormConfig: ${items.name}")
//                    }
                    }
                }}
        }
    }

}