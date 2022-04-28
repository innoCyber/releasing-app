package ptml.releasing.cargo_search.view

import android.os.Bundle
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ptml.releasing.R
import ptml.releasing.cargo_search.model.PODOperationStep
import ptml.releasing.cargo_search.model.adapters.PODAdapter

class NoNetworkPODActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_network_p_o_d)

       getAndDisplaySpinnerItems()

    }

    private fun getAndDisplaySpinnerItems() {
        val bundle = intent.extras
        val podItems =
            (bundle?.getParcelableArrayList<PODOperationStep>("podItems") as ArrayList<PODOperationStep>)
        val containerNumber: String = bundle.getString("containerNumber") ?: ""

        val podSpinner = findViewById<Spinner>(R.id.pod_spinner)
        val containerNumberTV = findViewById<TextView>(R.id.container_number)
        containerNumberTV.text = containerNumber

        val customDropDownAdapter = PODAdapter(this, podItems)

        podSpinner.adapter = customDropDownAdapter
    }
}