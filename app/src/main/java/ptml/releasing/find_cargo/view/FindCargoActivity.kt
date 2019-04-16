package ptml.releasing.find_cargo.view

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.form.FormBuilder
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.find_cargo.view_model.FindCargoViewModel
import java.util.*

class FindCargoActivity : BaseActivity<FindCargoViewModel, ptml.releasing.databinding.ActivityFindCargoBinding>() {


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
        val formBuilder = FormBuilder(this)
            .build(it?.data)
        binding.formContainer.addView(formBuilder)
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


    override fun getViewModelClass() = FindCargoViewModel::class.java

    override fun getLayoutResourceId() = R.layout.activity_find_cargo

    override fun getBindingVariable() = BR.viewModel
}