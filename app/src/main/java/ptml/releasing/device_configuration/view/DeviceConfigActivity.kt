package ptml.releasing.device_configuration.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.utils.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.Status
import ptml.releasing.databinding.ActivityDeviceConfigBinding
import ptml.releasing.device_configuration.viewmodel.DeviceConfigViewModel
import ptml.releasing.login.view.LoginActivity
import timber.log.Timber

class DeviceConfigActivity : BaseActivity<DeviceConfigViewModel, ActivityDeviceConfigBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.includeProgress.root.visibility = View.VISIBLE
        initErrorDrawable(binding.includeError.imgError)
        if (!viewModel.checkIfFirst()) {
            startNewActivity(LoginActivity::class.java, true)
            return
        }

        viewModel.openSearchActivity().observe(this, Observer {
            hideLoading(binding.includeError.root)
            startNewActivity(LoginActivity::class.java, true)
        })

        viewModel.showDeviceError().observe(this, Observer {
            showError()
        })

        viewModel.getNetworkState().observe(this, Observer {event->
            event.getContentIfNotHandled()?.let {
                if (NetworkState.LOADING == it) {
                    showLoading(
                        binding.includeProgress.root,
                        binding.includeProgress.tvMessage,
                        R.string.configure_device_message
                    )
                    Timber.e("Loading...")
                }

                if (it.status == Status.FAILED) {
                    val error = ErrorHandler().getErrorMessage(it.throwable)
                    showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
                } else {
                    hideLoading(binding.includeError.root)
                }
            }
        })


        binding.includeDeviceConfigError.btnClose.setOnClickListener {
            finish()
        }
        binding.includeError.btnReload.setOnClickListener {
            verifyDevice(imei)
        }


    }

    override fun onImeiGotten(imei: String?) {
        verifyDevice(imei)
    }

    private fun verifyDevice(imei: String?) {
        viewModel.verifyDeviceId(imei ?: "")
    }

     fun showError() {
        binding.includeProgress.root.visibility = View.GONE
        binding.includeError.root.visibility = View.GONE
        binding.includeDeviceConfigError.root.visibility = View.VISIBLE
        binding.includeDeviceConfigError.tvMessage.text = getString(R.string.verification_error_message, imei)
    }

    override fun getViewModelClass() = DeviceConfigViewModel::class.java

    override fun getLayoutResourceId() = R.layout.activity_device_config

    override fun getBindingVariable() = BR._all


}