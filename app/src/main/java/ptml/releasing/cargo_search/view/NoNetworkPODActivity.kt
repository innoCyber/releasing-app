package ptml.releasing.cargo_search.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ptml.releasing.R
import ptml.releasing.cargo_search.model.DownloadVoyageResponse
import ptml.releasing.cargo_search.model.adapters.PODAdapter

class NoNetworkPODActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_network_p_o_d)
        val bundle = intent.extras

        val containerNumber: String = bundle?.getString("containerNumber") ?: ""
        val isShipSide = bundle?.getBoolean("isShipSide") ?: false

//        val podItems = (bundle?.getParcelable<DownloadVoyageResponse>("podItems") )
//        val podSpinner = findViewById<Spinner>(R.id.pod_spinner)

        val containerNumberTV = findViewById<TextView>(R.id.container_number)
        containerNumberTV.text = containerNumber


//        val customDropDownAdapter = podItems?.optionsPOD?.let { PODAdapter(this, it) }
//        podSpinner.adapter = customDropDownAdapter
//
//        if (isShipSide) {
//            podSpinner.visibility = View.GONE
//            findViewById<ImageView>(R.id.ic_arrow_down).visibility = View.GONE
//        }

    }


    fun goTOSearchActivity(view: View){
        supportFinishAfterTransition()
    }

}