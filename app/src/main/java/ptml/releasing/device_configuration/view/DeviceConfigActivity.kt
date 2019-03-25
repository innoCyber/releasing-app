package ptml.releasing.device_configuration.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import permissions.dispatcher.*
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoConfirmDialog
import ptml.releasing.auth.view.LoginActivity
import ptml.releasing.app.utils.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.Status
import ptml.releasing.databinding.ActivityDeviceConfigBinding

import ptml.releasing.device_configuration.viewmodel.DeviceConfigViewModel
import ptml.releasing.home.view.HomeActivity
import timber.log.Timber

@RuntimePermissions
class DeviceConfigActivity : BaseActivity<DeviceConfigViewModel, ActivityDeviceConfigBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeProgress.root.visibility = View.VISIBLE
        initErrorDrawable(binding.includeError.imgError)

        viewModel.baseLiveData.observe(this, Observer {
            if (true == it?.isSuccess) {
                hideLoading(binding.includeError.root)
                startNewActivity(HomeActivity::class.java, true)
            } else if (false == it?.isSuccess) {
                showError()
            }
        })

        viewModel.networkState.observe(this, Observer {
            if (NetworkState.LOADING == it) {
                showLoading(binding.includeProgress.root, binding.includeProgress.tvMessage, R.string.configure_device_message)
                Timber.e("Loading...")
            }
            if (it?.status == Status.FAILED) {
                val error = ErrorHandler().getErrorMessage(it.throwable)
                showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
            } else {
                hideLoading(binding.includeError.root)
            }
        })


        binding.includeDeviceConfigError.btnClose.setOnClickListener {
            finish()
        }
        binding.includeError.btnReload.setOnClickListener {
            verifyDeviceIdWithPermissionCheck()
        }

        verifyDeviceIdWithPermissionCheck()
    }


    private fun showError() {
        binding.includeProgress.root.visibility = View.GONE
        binding.includeError.root.visibility = View.GONE
        binding.includeDeviceConfigError.root.visibility = View.VISIBLE
    }


    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun verifyDeviceId() {
        viewModel.verifyDeviceId((application as ReleasingApplication).provideImei())
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

    override fun getViewModelClass() = DeviceConfigViewModel::class.java

    override fun getLayoutResourceId() = R.layout.activity_device_config

    override fun getBindingVariable() = BR._all


}