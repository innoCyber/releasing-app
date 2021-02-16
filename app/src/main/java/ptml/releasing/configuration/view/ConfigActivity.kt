package ptml.releasing.configuration.view

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import permissions.dispatcher.*
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.EditTextDialog
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.exception.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.Status
import ptml.releasing.configuration.models.*
import ptml.releasing.configuration.view.adapter.ConfigSpinnerAdapter
import ptml.releasing.configuration.viewmodel.ConfigViewModel
import ptml.releasing.databinding.ActivityConfigBinding
import timber.log.Timber

@RuntimePermissions
class ConfigActivity : BaseActivity<ConfigViewModel, ActivityConfigBinding>() {

    private var cargoAdapter: ConfigSpinnerAdapter<CargoType>? = null

    private var operationStepAdapter: ConfigSpinnerAdapter<ReleasingOperationStep>? = null

    private var terminalAdapter: ConfigSpinnerAdapter<ReleasingTerminal>? = null

    private val errorHandler by lazy {
        ErrorHandler(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)
        disableTerminalSpinner()
        initErrorDrawable(binding.includeError.imgError)
        binding.bottom.btnDeleteLayout.visibility = View.GONE
        binding.top.root.visibility = View.INVISIBLE
        binding.bottom.root.visibility = View.INVISIBLE




        viewModel.cargoTypes.observe(this, Observer { cargoTypes ->
            viewModel.configuration.observe(this, Observer {selectedItem ->
                setUpCargoType(cargoTypes, selectedItem)
            })

        })

        viewModel.getOperationStepList().observe(this, Observer {operationSteps ->
            viewModel.configuration.observe(this, Observer {selectedOperation ->
                setUpOperationStep(operationSteps, selectedOperation)
            })

        })
        viewModel.getTerminalList().observe(this, Observer {
            setUpTerminal(it)
        })


        viewModel.getNetworkState().observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (it == NetworkState.LOADING) {
                    showLoading(
                        binding.includeProgress.root,
                        binding.includeProgress.tvMessage,
                        R.string.getting_configuration
                    )
                } else {
                    hideLoading(binding.includeProgress.root)
                    binding.top.root.visibility = View.VISIBLE
                    binding.bottom.root.visibility = View.VISIBLE
                }

                if (it.status == Status.FAILED) {
                    binding.includeError.imgClose.isVisible = true
                    binding.includeError.imgClose.setOnClickListener {
                        hideLoading(binding.includeError.root)
                    }
                    val error = errorHandler.getErrorMessage(it.throwable)
                    binding.includeError.btnReloadLayout.setOnClickListener {
                        if (errorHandler.isImeiError(error)) {
                            showEnterImeiDialog()
                        } else {
                            refreshConfigWithPermissionCheck()
                        }
                    }
                    binding.includeError.btnReload.text =
                        if (errorHandler.isImeiError(error)) getString(R.string.enter_imei) else getString(
                            R.string.reload
                        )
                    showLoading(
                        binding.includeError.root,
                        binding.includeError.tvMessage,
                        error
                    )
                    showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
                } else {
                    hideLoading(binding.includeError.root)
                }
            }


        })


        viewModel.getSavedSuccess().observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    notifyUser(getString(R.string.config_saved_success))
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        })

        viewModel.configuration.observe(this, Observer {
            val terminal = it.terminal
            val operationStep = it.operationStep
            val cargoType = it.cargoType
            val cameraEnabled = it.cameraEnabled

            binding.top.cameraSwitch.isChecked = cameraEnabled
            binding.top.selectCargoSpinner.setSelection(cargoAdapter?.getPosition(cargoType) ?: 0)
            val position = operationStepAdapter?.getPosition(
                operationStep
            ) ?: 0
            binding.top.selectOperationSpinner.setSelection(
                position
            )
            binding.top.selectTerminalSpinner.setSelection(
                terminalAdapter?.getPosition(terminal) ?: 0
            )
        })


        binding.bottom.btnProfilesLayout.setOnClickListener {
            setConfigWithPermissionCheck()
        }

        binding.includeError.btnReloadLayout.setOnClickListener {
            getConfigWithPermissionCheck()
        }

        binding.fab.setOnClickListener {
            refreshConfigWithPermissionCheck()
        }

    }

    private fun disableTerminalSpinner() {
        binding.top.selectTerminalSpinner.run {
            isEnabled = false
            isClickable = false
            alpha = 0.3F
        }
        binding.top.tvSelectTerminal.alpha = 0.3F
    }

    override fun onImeiGotten(imei: String?) {
        super.onImeiGotten(imei)
        viewModel.refreshConfiguration(imei ?: "")
        viewModel.getConfig(imei ?: "")
    }

    private fun showEnterImeiDialog() {
        val imeiNumber = imei
        val dialog =
            EditTextDialog.newInstance(
                imeiNumber,
                object : EditTextDialog.EditTextDialogListener {
                    override fun onSave(value: String) {
                        imei = value
                        viewModel.updateImei(value)
                        refreshConfigWithPermissionCheck()
                    }
                },
                getString(R.string.enter_imei_dialog_title),
                getString(R.string.enter_imei_dialog_hint),
                false
            )
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, dialog.javaClass.name)
    }

    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getConfig() {
        viewModel.getConfig(imei ?: "")
    }

    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun refreshConfig() {
        viewModel.refreshConfiguration(imei ?: "")
    }

    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun setConfig() {
        viewModel.setConfig(
            binding.top.selectTerminalSpinner.selectedItem as ReleasingTerminal?,
            binding.top.selectOperationSpinner.selectedItem as ReleasingOperationStep?,
            binding.top.selectCargoSpinner.selectedItem as CargoType?,
            binding.top.cameraSwitch.isChecked, imei ?: ""
        )
    }

    @OnShowRationale(android.Manifest.permission.READ_PHONE_STATE)
    fun showInitRecognizerRationale(request: PermissionRequest) {
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
    fun showDeniedForInitRecognizer() {
        notifyUser(binding.root, getString(R.string.phone_state_permission_denied))
    }

    @OnNeverAskAgain(android.Manifest.permission.READ_PHONE_STATE)
    fun neverAskForInitRecognizer() {
        notifyUser(binding.root, getString(R.string.phone_state_permission_never_ask))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }


    private fun setUpCargoType(cargoTypes: List<CargoType>, selected: Configuration) {
        try {
            binding.top.selectCargoSpinner.run {
                cargoAdapter =
                    ConfigSpinnerAdapter(applicationContext, R.id.tv_category, cargoTypes)
                adapter = cargoAdapter
                val selectedItem = cargoTypes.indexOf(selected.cargoType)
                setSelection(if(selectedItem == -1 ) 0 else selectedItem)
                onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            Timber.d("Nothing was selected")
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            viewModel.cargoTypeSelected(cargoAdapter?.getItem(position) ?: CargoType())
                        }
                    }
            }


        } catch (e: Exception) {
            Timber.e(e)
            showLoading(
                binding.includeError.root,
                binding.includeError.tvMessage,
                R.string.error_occurred
            )
        }
    }

    private fun setUpOperationStep(operationStepList: List<ReleasingOperationStep>, selected: Configuration) {

        binding.top.selectOperationSpinner.run {
            operationStepAdapter =
                ConfigSpinnerAdapter(applicationContext, R.id.tv_category, operationStepList)
            adapter = operationStepAdapter
            val selectedItem = operationStepList.indexOf(selected.operationStep)
            setSelection(if(selectedItem == -1) 0 else selectedItem)
        }
    }

    private fun setUpTerminal(terminalList: List<ReleasingTerminal>) {
        terminalAdapter = ConfigSpinnerAdapter(applicationContext, R.id.tv_category, terminalList)
        binding.top.selectTerminalSpinner.adapter = terminalAdapter
    }


    override fun getViewModelClass() = ConfigViewModel::class.java


    override fun getBindingVariable() = BR.viewModel

    override fun getLayoutResourceId() = R.layout.activity_config

}