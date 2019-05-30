package ptml.releasing.device_configuration.view

import android.os.Bundle
import android.view.View
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
import ptml.releasing.cargo_search.view.SearchActivity
import ptml.releasing.databinding.ActivityDeviceConfigBinding
import ptml.releasing.device_configuration.viewmodel.DeviceConfigViewModel
import timber.log.Timber

@RuntimePermissions
class DeviceConfigActivity : BaseActivity<DeviceConfigViewModel, ActivityDeviceConfigBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeProgress.root.visibility = View.VISIBLE
        initErrorDrawable(binding.includeError.imgError)
        if (!viewModel.checkIfFirst()) {
            startNewActivity(SearchActivity::class.java, true)
            return
        }

        viewModel.baseLiveData.observe(this, Observer {
            if (true == it?.isSuccess) {
                hideLoading(binding.includeError.root)
                startNewActivity(SearchActivity::class.java, true)
            } else if (false == it?.isSuccess) {
                showError()
            }
        })

        viewModel.networkState.observe(this, Observer {
            if (NetworkState.LOADING == it) {
                showLoading(
                    binding.includeProgress.root,
                    binding.includeProgress.tvMessage,
                    R.string.configure_device_message
                )
                Timber.e("Loading...")
            }
            if (it?.status == Status.FAILED) {
                val error = ErrorHandler().getErrorMessage(it.throwable)
                showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
            } else {
                hideLoading(binding.includeError.root)
            }
        })

        verifyDeviceIdWithPermissionCheck()

        binding.includeDeviceConfigError.btnClose.setOnClickListener {
            finish()
        }
        binding.includeError.btnReload.setOnClickListener {
            verifyDeviceIdWithPermissionCheck()
        }


    }


    private fun showError() {
        binding.includeProgress.root.visibility = View.GONE
        binding.includeError.root.visibility = View.GONE
        binding.includeDeviceConfigError.root.visibility = View.VISIBLE
    }


    @NeedsPermission(
        android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun verifyDeviceId() {
        viewModel.verifyDeviceId((application as ReleasingApplication).provideImei())
    }

    @OnShowRationale(
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
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

    @OnPermissionDenied(
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun showDeniedForInitRecognizer() {
        notifyUser(binding.root, getString(R.string.phone_state_permission_denied))
    }

    @OnNeverAskAgain(
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun neverAskForInitRecognizer() {
        notifyUser(binding.root, getString(R.string.phone_state_permission_never_ask))
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun getViewModelClass() = DeviceConfigViewModel::class.java

    override fun getLayoutResourceId() = R.layout.activity_device_config

    override fun getBindingVariable() = BR._all


}