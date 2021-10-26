package ptml.releasing.cargo_search.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.utils.Constants

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import permissions.dispatcher.RuntimePermissions
import ptml.releasing.app.data.Repository
import ptml.releasing.cargo_info.view.CargoInfoActivity
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.cargo_search.model.PODOperationStep
import ptml.releasing.cargo_search.model.adapters.PODAdapter
import ptml.releasing.cargo_search.viewmodel.NNPVMFactory
import ptml.releasing.cargo_search.viewmodel.NoNetworkPODViewModel
import ptml.releasing.cargo_search.viewmodel.SearchViewModel
import ptml.releasing.configuration.models.ReleasingOptions
import ptml.releasing.databinding.ActivitySearchBinding
import java.util.*
import kotlin.collections.ArrayList

class NoNetworkPODActivity : AppCompatActivity() {

    private lateinit var viewModel: NoNetworkPODViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_network_p_o_d)
        viewModel = ViewModelProvider(this).get(NoNetworkPODViewModel::class.java)

        val bundle = intent.extras
        val containerNumber: String = bundle?.getString("containerNumber") ?: ""
        val podSpinner = findViewById<Spinner>(R.id.pod_spinner)
        val containerNumberTV = findViewById<TextView>(R.id.container_number)
        containerNumberTV.text = containerNumber
//        viewModel.podSpinnerItems.observe(this, Observer {
//
//            val customDropDownAdapter = PODAdapter(this, it)
//
//            podSpinner.adapter = customDropDownAdapter
//        })

    }



//    private fun getAndDisplaySpinnerItems() {
//        val bundle = intent.extras
//        val podItems = (bundle?.getParcelableArrayList<ReleasingOptions>("podItems") as ArrayList<ReleasingOptions>)
//
//        val podSpinner = findViewById<Spinner>(R.id.pod_spinner)
//        val containerNumberTV = findViewById<TextView>(R.id.container_number)
//        containerNumberTV.text = containerNumber
//
//        Log.d("getAndDispla", "getAndDisplaySpinnerItems: $podItems")
////        val customDropDownAdapter = PODAdapter(this, podItems)
////
////        podSpinner.adapter = customDropDownAdapter
//    }

}