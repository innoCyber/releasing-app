package ptml.releasing.cargo_info.view

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import ptml.releasing.BR
import ptml.releasing.BuildConfig
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.form.FormBuilder
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.FormLoader
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.cargo_info.view_model.CargoInfoViewModel
import ptml.releasing.cargo_search.model.FindCargoResponse
import timber.log.Timber
import java.util.*

class CargoInfoActivity : BaseActivity<CargoInfoViewModel, ptml.releasing.databinding.ActivityCargoInfoBinding>() {

    companion object{
        const val  RESPONSE = "response"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)


        viewModel.goBack.observe(this, Observer {
            onBackPressed()
        })

        viewModel.savedConfiguration.observe(this, Observer {
            updateTop(it)
        })

        viewModel.getSavedConfig()

        viewModel.formConfig.observe(this, Observer {
            createForm(it)
        })

        viewModel.getFormConfig()







    }


    private fun createForm(it: ConfigureDeviceResponse?) {
        var findCargoResponse = intent?.extras?.getBundle(Constants.EXTRAS)?.getParcelable<FindCargoResponse>(RESPONSE)
        Timber.d("From sever: %s", findCargoResponse)
        if(BuildConfig.DEBUG){
            findCargoResponse = FormLoader.loadFindCargoResponseFromAssets(applicationContext)
            Timber.w("From assets: %s", findCargoResponse)
        }

        val formView = FormBuilder(this)
            .init(findCargoResponse)
            .build(it?.data)
        binding.formContainer.addView(formView)


    }


    private fun updateTop(it: Configuration) {
        binding.includeHome.tvCargoFooter.text = it.cargoType.value
        binding.includeHome.tvOperationStepFooter.text = it.operationStep.value
        binding.includeHome.tvTerminalFooter.text = it.terminal.value

        if (it.cargoType.value?.toLowerCase(Locale.US) == CargoType.VEHICLE) {
            binding.includeHome.imgCargoType.setImageDrawable(
                ContextCompat.getDrawable(
                    themedContext,
                    R.drawable.ic_car
                )
            )
        } else {
            binding.includeHome.imgCargoType.setImageDrawable(
                ContextCompat.getDrawable(
                    themedContext,
                    R.drawable.ic_container
                )
            )
        }

    }


    override fun getViewModelClass() = CargoInfoViewModel::class.java

    override fun getLayoutResourceId() = R.layout.activity_cargo_info

    override fun getBindingVariable() = BR.viewModel
}