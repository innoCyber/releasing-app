package ptml.releasing.cargo_info.view

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.zebra.sdk.comm.BluetoothConnectionInsecure
import permissions.dispatcher.*
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.Status
import ptml.releasing.app.utils.bt.BluetoothManager
import ptml.releasing.cargo_info.model.FormDataWrapper
import ptml.releasing.cargo_info.view_model.CargoInfoViewModel
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.damages.view.DamagesActivity
import ptml.releasing.form.*
import ptml.releasing.form.models.FormConfiguration
import ptml.releasing.printer.model.Settings
import ptml.releasing.printer.view.PrinterSettingsActivity
import timber.log.Timber
import java.util.*

@RuntimePermissions
class CargoInfoActivity :
    BaseActivity<CargoInfoViewModel, ptml.releasing.databinding.ActivityCargoInfoBinding>() {


    companion object {
        const val RESPONSE = "response"
        const val CARGO_CODE = "query"
        const val DAMAGES_RC = 1234
    }

    private val findCargoResponse by lazy {
        intent?.extras?.getBundle(Constants.EXTRAS)?.getParcelable<FindCargoResponse>(RESPONSE)
    }

    private lateinit var bluetoothManager: BluetoothManager
    private var printerBarcodeSettings: Settings? = null
    var formBuilder: FormBuilder? = null
    var damageView: View? = null
    var printerView: View? = null

    private var validatorListener = object : FormValidator.ValidatorListener {
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
                    viewModel.onPrintBarcode()
                }

                FormType.IMAGES -> {

                }

                FormType.DAMAGES -> {
                    damageView = view
                    DamagesActivity.typeContainer = findCargoResponse?.typeContainer
                    val intent = Intent(this@CargoInfoActivity, DamagesActivity::class.java)
                    startActivityForResult(
                        intent,
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
            validateSaveSubmit()
        }

        override fun onClickReset() {
            showResetConfirmDialog()
        }

        override fun onEndLoad() {

        }

        override fun onDataChange(data: FormConfiguration?, change: Any?) {
            Timber.d("Data changed: ${data?.id}")
            if (data?.type == FormType.VOYAGE.type) {
                Timber.d("Data changed for voyage: $change")
                viewModel.storeLastSelectedVoyage(change)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)
        DamagesActivity.resetValues() //reset the static values
        val input = intent?.extras?.getBundle(Constants.EXTRAS)?.getString(CARGO_CODE)
        binding.tvNumber.text = input

        bluetoothManager = BluetoothManager(this)
        bluetoothManager.adapterListener = object : BluetoothManager.AdapterListener {
            override fun onAdapterError() {
                showErrorDialog(getString(R.string.no_bt_message))
            }
        }

        viewModel.goBack.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                onBackPressed()
            }
        })

        viewModel.savedConfiguration.observe(this, Observer {
            updateTop(it)
        })

        viewModel.getSavedConfig()

        viewModel.formConfig.observe(this, Observer {
            createForm(it)
        })

        getFormConfigWithPermissionCheck()

        viewModel.printerBarcodeSettings.observe(this, Observer {
            this.printerBarcodeSettings = it
            tryToPrintIfPermissionsAreGranted()
        })

        viewModel.networkState.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (it == NetworkState.LOADING) {
                    showLoading(
                        binding.includeProgress.root,
                        binding.includeProgress.tvMessage,
                        R.string.submitting_form
                    )
                } else {
                    hideLoading(binding.includeProgress.root)
                }

                if (it.status == Status.FAILED) {
                    val error = ErrorHandler().getErrorMessage(it.throwable)
                    showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
                } else {
                    hideLoading(binding.includeError.root)
                }
            }
        })

        viewModel.submitSuccess.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                notifyUser(getString(R.string.form_submit_success_msg))
                setResult(Activity.RESULT_OK)
                finish()
            }
        })

        viewModel.errorMessage.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                showErrorDialog(it)
            }
        })



        binding.includeError.btnReload.setOnClickListener {
            validateSaveSubmit()
        }
    }

    override fun onResume() {
        super.onResume()
        damageView?.findViewById<TextView>(R.id.tv_number)?.text =
            DamagesActivity.currentDamages.size.toString()
        val errorView: View? =
            if (damageView != null) (damageView?.parent as ViewGroup).findViewById<TextView>(R.id.tv_error) else null
        errorView?.visibility =
            if (DamagesActivity.currentDamages.size > 0) View.INVISIBLE else View.VISIBLE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PrinterSettingsActivity.RC_BT) {
            if (resultCode == Activity.RESULT_OK) {
                printBarcodeWithPermissionCheck(findCargoResponse?.barcode ?: "")
            } else {
                showTurnBlueToothPrompt()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun validateSaveSubmit() {
        val formValidator = FormValidator(formBuilder, formBuilder?.data)
        formValidator.listener = validatorListener
        if (formValidator.validate()) {
            Timber.d("Validated")
            submitFormWithPermissionCheck(formValidator)
        }
    }

    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getFormConfig() {
        viewModel.getFormConfig(
            (application as ReleasingApplication).provideImei(),
            findCargoResponse
        )
    }

    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun submitForm(formValidator: FormValidator) {
        val formSubmission = FormSubmission(formBuilder, formBuilder?.data, formValidator)
        viewModel.submitForm(
            formSubmission,
            findCargoResponse,
            intent?.extras?.getBundle(Constants.EXTRAS)?.getString(CARGO_CODE),
            (application as ReleasingApplication).provideImei()
        )
    }

    @NeedsPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun printBarcode(barcode: String) {
        printerView?.isEnabled = false
        val t = Thread(Runnable {
            try {
                /*  val db = ReleasingDBAdapter(this@ReleasingActivity)
                  db.open()*/

                val macAddress = printerBarcodeSettings?.currentPrinter
                val labelCpclData =
                    printerBarcodeSettings?.labelCpclData?.replace("var_barcode", barcode)
                /*db.getSettings().getLabelCpclData().replaceAll("var_barcode", cargo.getBarCode())*/
                Timber.e("Printer code: %s", labelCpclData)
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

    private fun tryToPrintIfPermissionsAreGranted() {
        if (bluetoothManager.bluetoothAdapter == null) {
            showErrorDialog(getString(R.string.no_bt_message))
            Timber.e("No Bluetooth device")
            return
        }

        if (bluetoothManager.bluetoothAdapter?.isEnabled == true) {
            printBarcodeWithPermissionCheck(findCargoResponse?.barcode ?: "")
        } else {
            attemptToTurnBluetoothOn()
        }
    }

    private fun attemptToTurnBluetoothOn() {
        startActivityForResult(
            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
            PrinterSettingsActivity.RC_BT
        )
    }

    @VisibleForTesting
    fun createForm(wrapper: FormDataWrapper?) {
        Timber.d("From sever: %s", findCargoResponse)
        formBuilder = FormBuilder(this)
        val formView = formBuilder
            ?.setListener(formListener)
            ?.build(wrapper?.formConfigureDeviceResponse?.data, wrapper?.remarks, wrapper?.voyages)

        formBuilder
            ?.init(viewModel.formMappers.formPrefillMapper.mapFromModel(findCargoResponse!!))
            ?.initializeData()

        binding.formContainer.addView(formView)
        if (formBuilder?.error == false) {
            binding.formBottom.addView(formBuilder?.getBottomButtons())
        }
    }


    private fun updateTop(it: Configuration) {
        binding.includeHome.tvCargoFooter.text = it.cargoType.value
        binding.includeHome.tvOperationStepFooter.text = it.operationStep.value
        binding.includeHome.tvTerminalFooter.text = it.terminal.value

        if (it.cargoType.value?.toLowerCase(Locale.US) == CargoType.VEHICLE) {
            binding.includeHome.imgCargoType.setImageDrawable(
                VectorDrawableCompat.create(
                    resources,
                    R.drawable.ic_car,
                    null
                )
            )
        } else if (it.cargoType.value?.toLowerCase(Locale.US) == CargoType.GENERAL) {
            binding.includeHome.imgCargoType.setImageDrawable(
                VectorDrawableCompat.create(
                    resources,
                    R.drawable.ic_cargo,
                    null
                )
            )
        } else {
            binding.includeHome.imgCargoType.setImageDrawable(
                VectorDrawableCompat.create(
                    resources,
                    R.drawable.ic_container,
                    null
                )
            )
        }

    }

    private fun showResetConfirmDialog() {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.confirm_action),
            message = getString(R.string.proceed_to_reset_msg),
            buttonText = getString(R.string.yes),
            hasNegativeButton = true,
            negativeButtonText = getString(R.string.no),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    formBuilder?.reset()

                    //reset the errors for the buttons
                    val damageErrorView: View? =
                        if (damageView != null) (damageView?.parent as ViewGroup).findViewById<TextView>(
                            R.id.tv_error
                        ) else null
                    damageErrorView?.visibility =
                        if (DamagesActivity.currentDamages.size > 0) View.INVISIBLE else View.VISIBLE

                    val printerErrorView: View? =
                        if (printerView != null) (printerView?.parent as ViewGroup).findViewById<TextView>(
                            R.id.tv_error
                        ) else null
                    printerErrorView?.visibility =
                        if (DamagesActivity.currentDamages.size > 0) View.INVISIBLE else View.VISIBLE

                }
            })
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    private fun showTurnBlueToothPrompt() {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.alert),
            message = getString(R.string.select_printer_error_message),
            buttonText = getString(android.R.string.ok),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    attemptToTurnBluetoothOn()
                }
            }, hasNegativeButton = true,
            negativeButtonText = getString(android.R.string.cancel)

        )
        dialogFragment.isCancelable = false
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    @OnShowRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun showLocationRationale(request: PermissionRequest) {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.allow_permission),
            message = getString(R.string.allow_location_permission_msg),
            buttonText = getString(android.R.string.ok),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    request.proceed()
                }
            })
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    @OnPermissionDenied(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun showDeniedForLocation() {
        notifyUser(binding.root, getString(R.string.location_permission_denied))
    }

    @OnNeverAskAgain(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun neverAskForLocation() {
        notifyUser(binding.root, getString(R.string.location_permission_never_ask))
    }

    @OnShowRationale(android.Manifest.permission.READ_PHONE_STATE)
    fun showPhoneStateRationale(request: PermissionRequest) {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.allow_permission),
            message = getString(R.string.allow_phone_state_permission_msg),
            buttonText = getString(android.R.string.ok),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    request.proceed()
                }
            })
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    @OnPermissionDenied(android.Manifest.permission.READ_PHONE_STATE)
    fun showDeniedForPhoneState() {
        notifyUser(binding.root, getString(R.string.phone_state_permission_denied))
    }

    @OnNeverAskAgain(android.Manifest.permission.READ_PHONE_STATE)
    fun neverAskForPhoneState() {
        notifyUser(binding.root, getString(R.string.phone_state_permission_never_ask))
    }


    override fun getViewModelClass() = CargoInfoViewModel::class.java

    override fun getLayoutResourceId() = R.layout.activity_cargo_info

    override fun getBindingVariable() = BR.viewModel
}
