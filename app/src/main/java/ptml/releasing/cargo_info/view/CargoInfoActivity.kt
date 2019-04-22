package ptml.releasing.cargo_info.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import ptml.releasing.BR
import ptml.releasing.BuildConfig
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.form.FormBuilder
import ptml.releasing.app.form.FormListener
import ptml.releasing.app.form.FormType
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.FormLoader
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.cargo_info.view_model.CargoInfoViewModel
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.damages.view.DamagesActivity
import ptml.releasing.download_damages.view.DamageActivity
import timber.log.Timber
import java.util.*


class CargoInfoActivity : BaseActivity<CargoInfoViewModel, ptml.releasing.databinding.ActivityCargoInfoBinding>() {

    companion object{
        const val  RESPONSE = "response"
        const val QUERY = "query"
        const val DAMAGES_RC = 1234
    }

    var formBuilder: FormBuilder? = null
    var damageView: View? = null

    val formListener = object : FormListener(){
        @Suppress("NON_EXHAUSTIVE_WHEN")
        override fun onClickFormButton(type: FormType, view:View) {
            when(type){
                FormType.PRINTER ->{

                }

                FormType.IMAGES ->{

                }

                FormType.DAMAGES ->{
                    damageView = view
                    startActivityForResult(
                        Intent(this@CargoInfoActivity, DamagesActivity::class.java),
                        DAMAGES_RC
                    )
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)

        val input = intent?.extras?.getBundle(Constants.EXTRAS)?.getString(QUERY)
        binding.tvNumber.text = input

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


    override fun onResume() {
        super.onResume()
        damageView?.findViewById<TextView>(R.id.tv_number)?.text = DamagesActivity.currentDamages.size.toString()
    }


    private fun createForm(it: ConfigureDeviceResponse?) {


        var findCargoResponse = intent?.extras?.getBundle(Constants.EXTRAS)?.getParcelable<FindCargoResponse>(RESPONSE)
        Timber.d("From sever: %s", findCargoResponse)
        if(BuildConfig.DEBUG){
            findCargoResponse = FormLoader.loadFindCargoResponseFromAssets(applicationContext)
            Timber.w("From assets: %s", findCargoResponse)
        }


        formBuilder = FormBuilder(this)
        val formView = formBuilder
            ?.setListener(formListener)
            ?.init(findCargoResponse)
            ?.build(it?.data)
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