package ptml.releasing.cargo_search.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
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
import ptml.releasing.app.utils.hideSoftInputFromWindow
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.databinding.ActivitySearchBinding
import ptml.releasing.cargo_info.view.CargoInfoActivity
import ptml.releasing.cargo_search.viewmodel.SearchViewModel
import ptml.releasing.configuration.view.onRequestPermissionsResult
import java.util.*

@RuntimePermissions
class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.goBack.observe(this, Observer {
            onBackPressed()
        })


        viewModel.savedConfiguration.observe(this, Observer {
            updateTop(it)
        })

        viewModel.verify.observe(this, Observer {
            search()
        })

        viewModel.getSavedConfig()


        viewModel.networkState.observe(this, Observer {
            if (it == NetworkState.LOADING) {
                showLoading(binding.includeProgress.root, binding.includeProgress.tvMessage, R.string.loading)
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

        viewModel.errorMessage.observe(this, Observer {
            showErrorDialog(it)
        })

        viewModel.cargoNumberValidation.observe(this, Observer {
            binding.includeSearch.tilInput.error = getString(it)
        })

        viewModel.findCargoResponse.observe(this, Observer {
            //pass it on to the cargo info activity
            val bundle = Bundle()
            bundle.putParcelable(CargoInfoActivity.RESPONSE, it)
            bundle.putString(CargoInfoActivity.QUERY, binding.includeSearch.editInput.text.toString())
            startNewActivity(CargoInfoActivity::class.java, data = bundle)
            hideLoading(binding.includeError.root)
            hideLoading(binding.includeProgress.root)
        })
        showUpEnabled(true)

        binding.includeSearch.editInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.includeSearch.tilInput.error = null
            }
        })

        binding.includeError.btnReloadLayout.setOnClickListener {
            search()
        }
    }

    private fun search() {
        binding.includeSearch.btnVerify.hideSoftInputFromWindow()
        findCargoWithPermissionCheck(binding.includeSearch.editInput.text.toString())
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSavedConfig()
    }

    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun findCargo(cargoNumber: String?) {
        viewModel.findCargo(cargoNumber, (application as ReleasingApplication).provideImei())
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

        if (it.cameraEnabled) {
            showCameraScan()
        } else {
            hideCameraScan()
        }

    }

    private fun showCameraScan() {
        binding.includeSearch.tvScan.visibility = View.VISIBLE
        binding.includeSearch.dividerL.visibility = View.VISIBLE
        binding.includeSearch.dividerR.visibility = View.VISIBLE
        binding.includeSearch.imgQrCode.visibility = View.VISIBLE
        binding.includeSearch.imgQrCodeLayout.visibility = View.VISIBLE

    }

    private fun hideCameraScan() {
        binding.includeSearch.tvScan.visibility = View.GONE
        binding.includeSearch.dividerL.visibility = View.GONE
        binding.includeSearch.dividerR.visibility = View.GONE
        binding.includeSearch.imgQrCode.visibility = View.GONE
        binding.includeSearch.imgQrCodeLayout.visibility = View.GONE
    }


    override fun getLayoutResourceId() = R.layout.activity_search

    override fun getBindingVariable() = BR.viewModel

    override fun getViewModelClass() = SearchViewModel::class.java
}