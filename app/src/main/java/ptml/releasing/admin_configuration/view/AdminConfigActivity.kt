package ptml.releasing.admin_configuration.view

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import permissions.dispatcher.*
import ptml.releasing.R
import ptml.releasing.admin_configuration.models.ConfigurationResponse
import ptml.releasing.admin_configuration.view.adapter.ConfigSpinnerAdapter
import ptml.releasing.admin_configuration.viewmodel.AdminConfigViewModel
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoConfirmDialog
import ptml.releasing.app.utils.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.Status
import ptml.releasing.databinding.ActivityAdminConfigBinding
import timber.log.Timber

@RuntimePermissions
class AdminConfigActivity : BaseActivity<AdminConfigViewModel>() {
    lateinit var binding: ActivityAdminConfigBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_config)
        showUpEnabled(true)
        initErrorDrawable(binding.includeError.imgError)
        binding.bottom.btnConfigure.visibility = View.GONE
        binding.top.root.visibility = View.INVISIBLE
        binding.bottom.root.visibility = View.INVISIBLE

        viewModel.configData.observe(this, Observer {

            if (it.isSuccess) {
                setUpSpinners(it)
            } else {
                showDialog(it?.message)
            }
        })


        viewModel.network.observe(this, Observer {
            if (it == NetworkState.LOADING) {
                showLoading(binding.includeProgress.root, binding.includeProgress.tvMessage, R.string.getting_configuration)
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

    @OnShowRationale(android.Manifest.permission.READ_PHONE_STATE)
    fun showInitRecognizerRationale(request: PermissionRequest) {
        InfoConfirmDialog.showDialog(
                this,
                R.string.allow_permission,
                R.string.allow_phone_state_permission_msg,
                R.drawable.ic_info_white, ({ request.proceed() })
        )
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


    private fun setUpSpinners(response: ConfigurationResponse) {
        try {
            val cargoAdapter = ConfigSpinnerAdapter(applicationContext, R.id.tv_category, response.cargoTypeList!!)
            val operationStepAdapter = ConfigSpinnerAdapter(applicationContext, R.id.tv_category, response.operationStepList!!)
            val terminalAdapter = ConfigSpinnerAdapter(applicationContext, R.id.tv_category, response.terminalList!!)
            binding.top.selectCargoSpinner.adapter = cargoAdapter
            binding.top.selectOperationSpinner.adapter = operationStepAdapter
            binding.top.selectTerminalSpinner.adapter = terminalAdapter
        } catch (e: Exception) {
            Timber.e(e)
            showLoading(binding.includeError.root, binding.includeError.tvMessage, R.string.error_occurred)
        }
    }

    override fun getViewModelClass() = AdminConfigViewModel::class.java

}