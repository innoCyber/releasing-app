package ptml.releasing.cargo_info.view

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.zebra.sdk.comm.BluetoothConnectionInsecure
import ptml.releasing.BR
import ptml.releasing.BuildConfig
import ptml.releasing.R
import ptml.releasing.admin_config.view.AdminConfigActivity
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.form.*
import ptml.releasing.app.utils.*
import ptml.releasing.cargo_info.view_model.CargoInfoViewModel
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ConfigureDeviceData
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.damages.view.DamagesActivity
import ptml.releasing.printer.model.Settings
import timber.log.Timber
import java.util.*


class CargoInfoActivity : BaseActivity<CargoInfoViewModel, ptml.releasing.databinding.ActivityCargoInfoBinding>() {

    companion object {
        const val RESPONSE = "response"
        const val QUERY = "query"
        const val DAMAGES_RC = 1234
    }

    var formBuilder: FormBuilder? = null
    var damageView: View? = null
    var printerView: View? = null

    var validatorListener = object : FormValidator.ValidatorListener {
        override fun onError() {
            val msg = getString(R.string.errors_present_in_form)
            notifyUser(binding.root, msg)
        }
    }

    private val formListener = object : FormListener() {
        @Suppress("NON_EXHAUSTIVE_WHEN")
        override fun onClickFormButton(type: FormType, view: View) {
            when (type) {
                FormType.PRINTER -> {

                    printerView = view
                    viewModel.getSettings()
                }

                FormType.IMAGES -> {

                }

                FormType.DAMAGES -> {
                    damageView = view
                    startActivityForResult(
                        Intent(this@CargoInfoActivity, DamagesActivity::class.java),
                        DAMAGES_RC
                    )
                }
            }
        }

        override fun onError(message: String) {
            Timber.e("Error: %s", message)
        }


        override fun onClickSave() {
            //create a new form validator
            val formValidator = FormValidator(formBuilder)
            formValidator.listener = validatorListener
            if (formValidator.validate()) {
                Timber.d("Validated")
                submitForm(formValidator)
            }
        }

        override fun onClickReset() {
            showResetConfirmDialog()
        }

        override fun onEndLoad() {

        }
    }

    private fun submitForm(formValidator: FormValidator) {
        val formSubmission = FormSubmission(formBuilder, formValidator)
        val findCargoResponse = intent?.extras?.getBundle(Constants.EXTRAS)?.getParcelable<FindCargoResponse>(RESPONSE)
        viewModel.submitForm(
            formSubmission,
            intent?.extras?.getBundle(Constants.EXTRAS)?.getString(QUERY),
            findCargoResponse?.cargoId
        )
    }

    private fun handlePrint(settings: Settings) {
        printerView?.isEnabled = false
        val t = Thread(Runnable {
            try {
                /*  val db = ReleasingDBAdapter(this@ReleasingActivity)
                  db.open()*/

                val macAddress = settings.currentPrinter
                val labelCpclData = settings.labelCpclData
                /*db.getSettings().getLabelCpclData().replaceAll("var_barcode", cargo.getBarCode())*/


                // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                val thePrinterConn = BluetoothConnectionInsecure(macAddress)

                // Initialize
                Looper.prepare()

                // Open the connection - physical connection is established here.
                thePrinterConn.open()

                // This prints the label.

                // Send the data to printer as a byte array.
                thePrinterConn.write(labelCpclData?.toByteArray())

                // Make sure the data got to the printer before closing the connection
                Thread.sleep(500)

                // Close the insecure connection to release resources.
                thePrinterConn.close()

                Looper.myLooper()?.quit()

                runOnUiThread { printerView?.isEnabled = true }

            } catch (e: Exception) {

                runOnUiThread {
                    printerView?.isEnabled = true

                    AlertDialog.Builder(this@CargoInfoActivity)
                        .setTitle(R.string.general_msg_print_error)
                        .setMessage("Error printing label " + "${if (e.localizedMessage.isNullOrEmpty()) "" else ":" + e.localizedMessage} ")
                        .setPositiveButton(android.R.string.ok) { _, _ -> }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()

                    e.printStackTrace()
                }

                return@Runnable
            }
        })

        t.start()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)
        DamagesActivity.resetValues() //reset the static values
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

        viewModel.printerSettings.observe(this, Observer {
            handlePrint(it)
        })

        viewModel.networkState.observe(this, Observer {
            if (it == NetworkState.LOADING) {
                showLoading(binding.includeProgress.root, binding.includeProgress.tvMessage, R.string.submitting_form)
            } else {
                hideLoading(binding.includeProgress.root)
            }

            if (it?.status == Status.FAILED) {
                val error = ErrorHandler().getErrorMessage(it.throwable)
                showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
            } else {
                hideLoading(binding.includeError.root)
            }
        })

        viewModel.submitSuccess.observe(this, Observer {
            showSuccessDialog()
        })

        viewModel.errorMessage.observe(this, Observer {
            showErrorDialog(it)
        })
    }

    private fun showSuccessDialog() {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.form_submit_success_title),
            message = getString(R.string.form_submit_success_msg),
            buttonText = getString(R.string.close),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    finish()
                }
            }
        )
        dialogFragment.isCancelable = false
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }


    override fun onResume() {
        super.onResume()
        damageView?.findViewById<TextView>(R.id.tv_number)?.text = DamagesActivity.currentDamages.size.toString()
        val errorView: View? =
            if (damageView != null) (damageView?.parent as ViewGroup).findViewById<TextView>(R.id.tv_error) else null
        errorView?.visibility = if (DamagesActivity.currentDamages.size > 0) View.INVISIBLE else View.VISIBLE
    }


    private fun createForm(it: ConfigureDeviceResponse?) {
        var findCargoResponse = intent?.extras?.getBundle(Constants.EXTRAS)?.getParcelable<FindCargoResponse>(RESPONSE)
        Timber.d("From sever: %s", findCargoResponse)
        /*  if (BuildConfig.DEBUG) {
              findCargoResponse = FormLoader.loadFindCargoResponseFromAssets(applicationContext)
              Timber.w("From assets: %s", findCargoResponse)
          }*/
        formBuilder = FormBuilder(this)
        val formView = formBuilder
            ?.setListener(formListener)
            ?.init(findCargoResponse)
            ?.build(it?.data)
        binding.formContainer.addView(formView)
        binding.formBottom.addView(formBuilder?.getBottomButtons())
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

    private fun showResetConfirmDialog() {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.confirm_action),
            message = getString(R.string.proceed_to_reset_msg),
            buttonText = getString(R.string.yes),
            hasNeutralButton = true,
            neutralButtonText = getString(R.string.no),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    formBuilder?.reset()

                    //reset the errors for the buttons
                    val damageErrorView: View? =
                        if (damageView != null) (damageView?.parent as ViewGroup).findViewById<TextView>(R.id.tv_error) else null
                    damageErrorView?.visibility =
                        if (DamagesActivity.currentDamages.size > 0) View.INVISIBLE else View.VISIBLE

                    val printerErrorView: View? =
                        if (printerView != null) (printerView?.parent as ViewGroup).findViewById<TextView>(R.id.tv_error) else null
                    printerErrorView?.visibility =
                        if (DamagesActivity.currentDamages.size > 0) View.INVISIBLE else View.VISIBLE

                }
            })
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }


    override fun getViewModelClass() = CargoInfoViewModel::class.java

    override fun getLayoutResourceId() = R.layout.activity_cargo_info

    override fun getBindingVariable() = BR.viewModel
}