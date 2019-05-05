package ptml.releasing.cargo_search.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import permissions.dispatcher.*
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.base.openBarCodeScannerWithPermissionCheck
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.utils.*
import ptml.releasing.cargo_info.view.CargoInfoActivity
import ptml.releasing.cargo_search.viewmodel.SearchViewModel
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.databinding.ActivitySearchBinding
import ptml.releasing.login.view.LoginActivity
import timber.log.Timber
import java.util.*

@RuntimePermissions
class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {

    companion object {
        const val RC = 4343
    }

    private val dialogListener = object : InfoDialog.NeutralListener {
        override fun onNeutralClick() {
            //2. Clicking on this option sets the cargo_id = 0
            //3. This then follows the same process as all other cargo
            viewModel.continueToUploadCargo()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getSavedConfig()
        viewModel.isConfigured.observe(this, Observer {
            binding.appBarHome.content.tvConfigMessageContainer.visibility = if (it) View.GONE else View.VISIBLE //hide or show the not configured message
            binding.appBarHome.content.includeHome.root.visibility = if (it) View.VISIBLE else View.GONE
            binding.appBarHome.content.includeSearch.root.visibility = if (it) View.VISIBLE else View.GONE

//            binding.appBarHome.content.includeHome.root.visibility = if (it) View.VISIBLE else View.GONE //hide or show the home buttons
        })


        viewModel.openAdMin.observe(this, Observer {
            startNewActivity(LoginActivity::class.java)
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
                showLoading(binding.appBarHome.content.includeProgress.root, binding.appBarHome.content.includeProgress.tvMessage, R.string.loading)
            } else {
                hideLoading(binding.appBarHome.content.includeProgress.root)
            }

            if (it?.status == Status.FAILED) {
                val error = ErrorHandler().getErrorMessage(it.throwable)
                showLoading(binding.appBarHome.content.includeError.root, binding.appBarHome.content.includeError.tvMessage, error)
            } else {
                hideLoading(binding.appBarHome.content.includeError.root)
            }
        })

        viewModel.errorMessage.observe(this, Observer {
            showSearchErrorDialog(it)
        })

        viewModel.cargoNumberValidation.observe(this, Observer {
            binding.appBarHome.content.includeSearch.tilInput.error = getString(it)
        })

        viewModel.findCargoResponse.observe(this, Observer {
            //pass it on to the cargo info activity
            val bundle = Bundle()
            bundle.putParcelable(CargoInfoActivity.RESPONSE, it)
            bundle.putString(CargoInfoActivity.QUERY, binding.appBarHome.content.includeSearch.editInput.text.toString())
            startNewActivity(CargoInfoActivity::class.java, data = bundle)
            hideLoading(binding.appBarHome.content.includeError.root)
            hideLoading(binding.appBarHome.content.includeProgress.root)
        })

        viewModel.scan.observe(this, Observer {
            openBarCodeScannerWithPermissionCheck(RC_BARCODE)
        })

        viewModel.noOperator.observe(this, Observer {
            //show dialog
            showOperatorErrorDialog()
        })

        binding.appBarHome.content.includeSearch.editInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.appBarHome.content.includeSearch.tilInput.error = null
            }
        })

        binding.appBarHome.content.includeError.btnReloadLayout.setOnClickListener {
            search()
        }

        binding.appBarHome.content.includeSearch.imgQrCode.setOnClickListener {
            viewModel.openBarcodeScan()
        }

        binding.appBarHome.content.btnBack.setOnClickListener {
            viewModel.openAdmin()
        }

        setSupportActionBar(binding.appBarHome.toolbar)
        val toggle = ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.appBarHome.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(navigationListener)
    }

    private fun showSearchErrorDialog(message: String?) {
        val dialogFragment = InfoDialog.newInstance(
                title = getString(R.string.error),
                message = message,
                buttonText = getString(android.R.string.cancel),
                hasNeutralButton = true,
                neutralButtonText = getString(R.string.continue_uploading_text),
                neutralListener = dialogListener)
        Timber.e("Error occurred during search: msg: %s", message)
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }


    private fun showOperatorErrorDialog() {
        val dialogFragment = InfoDialog.newInstance(
                title = getString(R.string.error),
                message = getString(R.string.no_operator_msg),
                buttonText = getString(android.R.string.ok),
                listener = object : InfoDialog.InfoListener {
                    override fun onConfirm() {
                        viewModel.openBarcodeScan()
                    }
                },
                hasNeutralButton = true,
                neutralButtonText = getString(android.R.string.cancel))
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }


    private fun search() {
        binding.appBarHome.content.includeSearch.btnVerify.hideSoftInputFromWindow()
        findCargoWithPermissionCheck(binding.appBarHome.content.includeSearch.editInput.text.toString())
    }


    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun findCargo(cargoNumber: String?) {
        viewModel.findCargo(cargoNumber, (application as ReleasingApplication).provideImei())
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC && resultCode == RESULT_OK) {
            binding.appBarHome.content.includeSearch.editInput.setText(data?.getStringExtra(Constants.BAR_CODE))
        }
    }

    private fun updateTop(it: Configuration) {
        binding.appBarHome.content.includeHome.tvCargoFooter.text = it.cargoType.value
        binding.appBarHome.content.includeHome.tvOperationStepFooter.text = it.operationStep.value
        binding.appBarHome.content.includeHome.tvTerminalFooter.text = it.terminal.value

        if (it.cargoType.value?.toLowerCase(Locale.US) == CargoType.VEHICLE) {
            binding.appBarHome.content.includeHome.imgCargoType.setImageDrawable(
                    ContextCompat.getDrawable(
                            themedContext,
                            R.drawable.ic_car
                    )
            )
        } else {
            binding.appBarHome.content.includeHome.imgCargoType.setImageDrawable(
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
        binding.appBarHome.content.includeSearch.tvScan.visibility = View.VISIBLE
        binding.appBarHome.content.includeSearch.dividerL.visibility = View.VISIBLE
        binding.appBarHome.content.includeSearch.dividerR.visibility = View.VISIBLE
        binding.appBarHome.content.includeSearch.imgQrCode.visibility = View.VISIBLE
        binding.appBarHome.content.includeSearch.imgQrCodeLayout.visibility = View.VISIBLE

    }

    private fun hideCameraScan() {
        binding.appBarHome.content.includeSearch.tvScan.visibility = View.GONE
        binding.appBarHome.content.includeSearch.dividerL.visibility = View.GONE
        binding.appBarHome.content.includeSearch.dividerR.visibility = View.GONE
        binding.appBarHome.content.includeSearch.imgQrCode.visibility = View.GONE
        binding.appBarHome.content.includeSearch.imgQrCodeLayout.visibility = View.GONE
    }


    override fun onResume() {
        super.onResume()
        viewModel.getSavedConfig()
    }

    override fun initBeforeView() {
        super.initBeforeView()
        if (viewModel.isConfigured()) {
//            startNewActivity(SearchActivity::class.java)
        } else {
            startNewActivity(LoginActivity::class.java)
        }
    }

    private fun showConfigurationErrorDialog() {
        val dialogFragment = InfoDialog.newInstance(
                title = getString(R.string.config_error),
                message = getString(R.string.config_error_message),
                buttonText = getString(android.R.string.ok),
                listener = object : InfoDialog.InfoListener {
                    override fun onConfirm() {
//                    viewModel.openConfiguration()
                    }
                })
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    private val navigationListener =
            NavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_preferences -> {
                        //TODO handle nav
                    }

                    R.id.nav_about -> {
                        //TODO handle nav
                        showImeiDialog()
                    }

                    R.id.nav_scan_operator ->{
                        viewModel.openBarCodeScanner()
                    }
                }
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                true
            }

    private fun showImeiDialog() {
        val dialogFragment = InfoDialog.newInstance(
                title = getString(R.string.device_IMEI),
                message = (application as ReleasingApplication).provideImei(),
                buttonText = getString(R.string.dismiss)
        )
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    override fun getLayoutResourceId() = R.layout.activity_search

    override fun getBindingVariable() = BR.viewModel

    override fun getViewModelClass() = SearchViewModel::class.java
}