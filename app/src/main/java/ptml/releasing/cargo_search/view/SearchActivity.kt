package ptml.releasing.cargo_search.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.material.navigation.NavigationView
import permissions.dispatcher.*
import ptml.releasing.BR
import ptml.releasing.BuildConfig
import ptml.releasing.R
import ptml.releasing.adminlogin.view.LoginActivity
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.base.openBarCodeScannerWithPermissionCheck
import ptml.releasing.app.dialogs.InfoConfirmDialog
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.utils.*
import ptml.releasing.cargo_info.view.CargoInfoActivity
import ptml.releasing.cargo_search.model.CargoNotFoundResponse
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.cargo_search.viewmodel.SearchViewModel
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.view.ConfigActivity
import ptml.releasing.databinding.ActivitySearchBinding
import timber.log.Timber
import java.util.*


@RuntimePermissions
class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {

    companion object {
        const val RC_CONFIG = 434
        const val RC_CARGO_INFO = 343
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initErrorDrawable(binding.appBarHome.content.includeError.imgError)
        viewModel.getSavedConfig()
        viewModel.isConfigured.observe(this, Observer {
            binding.appBarHome.content.tvConfigMessageContainer.visibility =
                if (it) View.GONE else View.VISIBLE //hide or show the not configured message
            binding.appBarHome.content.includeHome.root.visibility =
                if (it) View.VISIBLE else View.GONE
            binding.appBarHome.content.includeSearch.root.visibility =
                if (it) View.VISIBLE else View.GONE

//            binding.appBarHome.content.includeHome.root.visibility = if (it) View.VISIBLE else View.GONE //hide or show the home buttons
        })


        viewModel.openAdMin.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                startNewActivity(LoginActivity::class.java)
            }
        })

        viewModel.savedConfiguration.observe(this, Observer {
            updateTop(it)
        })

        viewModel.verify.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                search()
            }
        })

        viewModel.getSavedConfig()


        viewModel.networkState.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (it.status == Status.FAILED) {
                    val error = ErrorHandler().getErrorMessage(it.throwable)
                    showLoading(
                        binding.appBarHome.content.includeError.root,
                        binding.appBarHome.content.includeError.tvMessage,
                        error
                    )
                } else {
                    hideLoading(binding.appBarHome.content.includeError.root)
                }
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
            Timber.e("Gotten response: %s", it)
            animateBadge(it)
        })

        viewModel.scan.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                openBarCodeScannerWithPermissionCheck(RC_SEARCH)
            }
        })


        viewModel.openDeviceConfiguration.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                val intent = Intent(this, ConfigActivity::class.java)
                startActivityForResult(intent, RC_CONFIG)
            }
        })


        viewModel.searchScanned.observe(this, Observer {
            binding.appBarHome.content.includeSearch.editInput.setText(it)
        })

        binding.tvVersion.text = getString(R.string.version_text, BuildConfig.VERSION_NAME)
        binding.appBarHome.content.includeSearch.editInput.setAllCapInputFilter()
        binding.appBarHome.content.includeSearch.editInput.setImeDoneListener(object :
            DonePressedListener {
            override fun onDonePressed() {
                viewModel.verify()
            }
        })

        binding.appBarHome.content.includeSearch.editInput.addTextChangedListener(object :
            TextWatcher {
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

        binding.appBarHome.content.includeSearch.btnVerify.setOnClickListener {
            it.setBackgroundResource(R.drawable.save_btn_bg_blue)
            viewModel.verify()
        }

        binding.appBarHome.content.includeSearch.imgQrCode.setOnClickListener {
            viewModel.openBarcodeScan()
        }

        binding.appBarHome.content.btnBack.visibility = View.GONE

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

        if (!resources.getBoolean(R.bool.isLarge)) {
            val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
            val params = binding.navView.layoutParams
            params.width = width
            binding.navView.layoutParams = params
        }

    }

    private fun animateBadge(it: FindCargoResponse?) {

        binding.appBarHome.content.imgOk.visibility = View.VISIBLE

        // Construct and run the parallel animation of the
        // scale properties (SCALE_X, and SCALE_Y).
        val anim = ScaleAnimation(
            3f,
            1f,
            3f,
            1f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        anim.duration = resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        anim.interpolator = DecelerateInterpolator()

        binding.appBarHome.content.imgOk.startAnimation(anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                binding.appBarHome.content.imgOk.visibility = View.GONE
                startCargoInfoActivity(it)

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }


    fun startCargoInfoActivity(it: FindCargoResponse?) {
        val bundle = Bundle()
        bundle.putParcelable(CargoInfoActivity.RESPONSE, it)
        bundle.putString(
            CargoInfoActivity.QUERY,
            binding.appBarHome.content.includeSearch.editInput.text.toString()
        )
        val intent = Intent(this@SearchActivity, CargoInfoActivity::class.java)
        intent.putExtra(Constants.EXTRAS, bundle)
        startActivityForResult(intent, RC_CARGO_INFO)
        hideLoading(binding.appBarHome.content.includeError.root)
        hideLoading(binding.appBarHome.content.includeProgress.root)
    }

    private fun showSearchErrorDialog(response: CargoNotFoundResponse?) {

        if (response?.shipSide == true) {
            InfoConfirmDialog.showDialog(
                this,
                getString(R.string.error),
                if (response.message?.isNotEmpty() == true) response.message else getString(R.string.error_occurred),
                getString(R.string.continue_uploading_text),
                object : InfoConfirmDialog.InfoListener {
                    override fun onConfirm() {
                        viewModel.continueToUploadCargo()
                    }
                })

        } else {

            val dialogFragment = InfoDialog.newInstance(
                title = getString(R.string.error),
                message = if (response?.message?.isNotEmpty() == true) response.message else getString(
                    R.string.error_occurred
                ),
                buttonText = getString(R.string.dismiss)
            )

            dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
        }

        Timber.e("Error occurred during search: msg: %s", response)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CONFIG && resultCode == RESULT_OK) {
            binding.appBarHome.content.includeSearch.editInput.setText(
                data?.getStringExtra(
                    Constants.BAR_CODE
                )
            )
        } else if (requestCode == RC_CARGO_INFO && resultCode == RESULT_OK) {
            binding.appBarHome.content.includeSearch.editInput.setText("")
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun updateTop(it: Configuration) {
        binding.appBarHome.content.includeHome.tvCargoFooter.text = it.cargoType.value
        binding.appBarHome.content.includeHome.tvOperationStepFooter.text = it.operationStep.value
        binding.appBarHome.content.includeHome.tvTerminalFooter.text = it.terminal.value

        if (it.cargoType.value?.toLowerCase(Locale.US) == CargoType.VEHICLE) {
            binding.appBarHome.content.includeHome.imgCargoType.setImageDrawable(
                VectorDrawableCompat.create(
                    resources,
                    R.drawable.ic_car,
                    null
                )
            )
        } else if (it.cargoType.value?.toLowerCase(Locale.US) == CargoType.GENERAL) {
            binding.appBarHome.content.includeHome.imgCargoType.setImageDrawable(
                VectorDrawableCompat.create(
                    resources,
                    R.drawable.ic_cargo,
                    null
                )
            )
        } else {
            binding.appBarHome.content.includeHome.imgCargoType.setImageDrawable(
                VectorDrawableCompat.create(
                    resources,
                    R.drawable.ic_container,
                    null
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
        binding.appBarHome.content.includeSearch.btnVerify.setBackgroundResource(R.drawable.save_btn_bg)
    }

    override fun initBeforeView() {
        super.initBeforeView()
        if (!viewModel.isConfigured()) {
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
                R.id.nav_admin_settings -> {
                    viewModel.openAdmin()
                }

                R.id.nav_about -> {
                    showImeiDialog()
                }

                R.id.nav_scan_operator -> {
                    viewModel.openBarCodeScanner()
                }
                R.id.nav_device_configuration -> {
                    viewModel.openDeviceConfiguration()
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