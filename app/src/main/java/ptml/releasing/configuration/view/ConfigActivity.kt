package ptml.releasing.configuration.view

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.Observer
import permissions.dispatcher.*
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.utils.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.Status
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.configuration.models.OperationStep
import ptml.releasing.configuration.models.Terminal
import ptml.releasing.configuration.view.adapter.ConfigSpinnerAdapter
import ptml.releasing.configuration.viewmodel.ConfigViewModel
import ptml.releasing.databinding.ActivityConfigBinding
import timber.log.Timber

@RuntimePermissions
class ConfigActivity : BaseActivity<ConfigViewModel, ActivityConfigBinding>() {

    private var cargoAdapter: ConfigSpinnerAdapter<CargoType>? = null

    private var operationStepAdapter: ConfigSpinnerAdapter<OperationStep>? = null

    private var terminalAdapter: ConfigSpinnerAdapter<Terminal>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)
        initErrorDrawable(binding.includeError.imgError)
        binding.bottom.btnDeleteLayout.visibility = View.GONE
        binding.top.root.visibility = View.INVISIBLE
        binding.bottom.root.visibility = View.INVISIBLE


        binding.top.selectCargoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Timber.d("Nothing was selected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.cargoTypeSelected(cargoAdapter?.getItem(position) ?: CargoType())
            }
        }

        viewModel.configData.observe(this, Observer {
            setUpSpinners(it)
        })

        viewModel.operationStepList.observe(this, Observer {
            setUpOperationStep(it)
        })
        viewModel.terminalList.observe(this, Observer {
//            setUpTerminal(it)
        })


        viewModel.network.observe(this, Observer {
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

            if (it?.status == Status.FAILED) {
                val error = ErrorHandler().getErrorMessage(it.throwable)
                showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
            } else {
                hideLoading(binding.includeError.root)
            }
        })


        viewModel.savedSuccess.observe(this, Observer {
            if (it) {
                notifyUser(getString(R.string.config_saved_success))
                setResult(Activity.RESULT_OK)
                finish()
            }
        })

        viewModel.configuration.observe(this, Observer {
            val terminal = it.terminal
            val operationStep = it.operationStep
            val cargoType = it.cargoType
            val cameraEnabled = it.cameraEnabled

            binding.top.cameraSwitch.isChecked = cameraEnabled
            binding.top.selectCargoSpinner.setSelection(cargoAdapter?.getPosition(cargoType) ?: 0)
            binding.top.selectOperationSpinner.setSelection(operationStepAdapter?.getPosition(operationStep) ?: 0)
            binding.top.selectTerminalSpinner.setSelection(terminalAdapter?.getPosition(terminal) ?: 0)
        })


        binding.bottom.btnProfilesLayout.setOnClickListener {
            setConfigWithPermissionCheck()
        }

        binding.includeError.btnReloadLayout.setOnClickListener {
            getConfigWithPermissionCheck()
        }


        //begin the request
        getConfigWithPermissionCheck()

    }


    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getConfig() {
        viewModel.getConfig((application as ReleasingApplication).provideImei())
    }

    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun setConfig() {
        viewModel.setConfig(
            binding.top.selectTerminalSpinner.selectedItem as Terminal,
            binding.top.selectOperationSpinner.selectedItem as OperationStep,
            binding.top.selectCargoSpinner.selectedItem as CargoType,
            binding.top.cameraSwitch.isChecked, (application as ReleasingApplication).provideImei()
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }


    private fun setUpSpinners(response: AdminConfigResponse) {
        try {
            cargoAdapter = ConfigSpinnerAdapter(applicationContext, R.id.tv_category, response.cargoTypeList)
            binding.top.selectCargoSpinner.adapter = cargoAdapter

            terminalAdapter = ConfigSpinnerAdapter(applicationContext, R.id.tv_category, response.terminalList)
            binding.top.selectTerminalSpinner.adapter = terminalAdapter

        } catch (e: Exception) {
            Timber.e(e)
            showLoading(binding.includeError.root, binding.includeError.tvMessage, R.string.error_occurred)
        }
    }

    private fun setUpOperationStep(operationStepList: List<OperationStep>) {
        operationStepAdapter = ConfigSpinnerAdapter(applicationContext, R.id.tv_category, operationStepList)
        binding.top.selectOperationSpinner.adapter = operationStepAdapter
    }

    private fun setUpTerminal(terminalList: List<Terminal>) {
        terminalAdapter = ConfigSpinnerAdapter(applicationContext, R.id.tv_category, terminalList)
        binding.top.selectTerminalSpinner.adapter = terminalAdapter
    }


    override fun getViewModelClass() = ConfigViewModel::class.java


    override fun getBindingVariable() = BR.viewModel

    override fun getLayoutResourceId() = R.layout.activity_config

}