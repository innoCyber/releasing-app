@file:Suppress("DEPRECATION")

package ptml.releasing.app.base

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import permissions.dispatcher.*
import ptml.releasing.R
import ptml.releasing.adminlogin.view.LoginActivity
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.utils.*
import ptml.releasing.app.utils.extensions.hideKeyBoardOnTouchOfNonEditableViews
import ptml.releasing.app.utils.network.NetworkListener
import ptml.releasing.app.utils.network.NetworkStateWrapper
import ptml.releasing.barcode_scan.BarcodeScanActivity
import ptml.releasing.cargo_info.view.CargoInfoActivity
import ptml.releasing.cargo_search.view.SearchActivity
import timber.log.Timber
import javax.inject.Inject

@RuntimePermissions
abstract class BaseActivity<V, D> :
    DaggerAppCompatActivity() where V : BaseViewModel, D : ViewDataBinding {
    private val networkSubject = MutableLiveData<Boolean>()
    private lateinit var receiver: BroadcastReceiver
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private var networkStateWrapper: NetworkStateWrapper? = null
    private var snackBar: Snackbar? = null
    private var firstTime = true


    protected lateinit var binding: D
    protected lateinit var viewModel: V

    @Inject
    protected lateinit var viewModeFactory: ViewModelProvider.Factory

    @Inject
    lateinit var networkUtils: NetworkUtils

    private var progressDialog: ProgressDialog? = null

    var imei: String? = null

    @Inject
    lateinit var imeiHelper: ImeiHelper

    @Inject
    lateinit var navigator: Navigator


    companion object {
        const val RC_BARCODE = 112
        const val RC_SEARCH = 113
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressDialog = ProgressDialog(this)

        val networkListener = NetworkListener(this)
        networkListener.networkInfoLive.observe(this, Observer {
            networkStateWrapper = it
            invalidateOptionsMenu()
            if (it.connected) {
                handleNetworkConnect()
            } else {
                handleNetworkDisconnected()
            }
        })
        lifecycle.addObserver(networkListener)

        viewModel = ViewModelProviders.of(this, viewModeFactory)
            .get(getViewModelClass())

        viewModel.checkToResetAppUpdateValues()

        initBeforeView()

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        initBinding()

        viewModel.openBarCodeScanner.observe(this, Observer {
            openBarCodeScannerWithPermissionCheck(RC_BARCODE)
        })

        viewModel.openConfiguration.observe(this, Observer {
            startNewActivity(LoginActivity::class.java)
        })


        viewModel.operatorName.observe(this, Observer {
            when (this) {
                /*  is AdminConfigActivity -> {
                     hideOperator()
                  }*/

                is SearchActivity -> {
                    initOperator(it)
                }


                is CargoInfoActivity -> {
                    initOperator(it)
                }

                else -> {
                    hideOperator()
                }
            }

        })

        viewModel.logOutDialog.observe(this, Observer {
            showLogOutConfirmDialog()
        })

        viewModel.getGoToLogin().observe(this, Observer { event ->
            event?.getContentIfNotHandled()?.let {
                navigator.goToLogin(this)
            }
        })

        viewModel.showUpdateApp.observe(this, Observer {
            showMustUpdateDialog()
        })


        viewModel.updateLoadingState.observe(this, Observer {
            Timber.e("$it")
            if (it != NetworkState.LOADING) {
                viewModel.applyUpdates()
            }
        })

        viewModel.startDamagesUpdate.observe(this, Observer {
            //start intent service to update
            Timber.d("Starting intent service to update damages")
            startUpdateDamagesServiceWithPermissionCheck()
        })

        viewModel.startQuickRemarksUpdate.observe(this, Observer {
            //start intent service to update
            Timber.d("Starting intent service to update quick remarks")
            startUpdateQuickRemarksServiceWithPermissionCheck()
        })

        viewModel.updateDamagesLoadingState.observe(this, Observer {
            if (it == NetworkState.LOADING) {
                progressDialog?.setTitle(getString(R.string.update_damages_title))
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage(getString(R.string.update_damages_message))
                progressDialog?.show()
            } else {
                if (!viewModel.updatingQuickRemarks()) {
                    progressDialog?.dismiss()
                }
                if (it.status == Status.FAILED) {
                    notifyUser(getString(R.string.damages_update_failed_msg))
                }
            }
        })

        viewModel.updateQuickRemarkLoadingState.observe(this, Observer {
            if (it == NetworkState.LOADING) {
                progressDialog?.setTitle(getString(R.string.update_quick_remarks_title))
                progressDialog?.setCancelable(false)
                progressDialog?.setMessage(getString(R.string.update_quick_remarks_message))
                progressDialog?.show()
            } else {
                if (!viewModel.updatingDamages()) {
                    progressDialog?.dismiss()
                }
                if (it.status == Status.FAILED) {
                    notifyUser(getString(R.string.quick_remark_update_failed_msg))
                }
            }
        })

        getIMEIWithPermissionCheck()
        hideKeyBoardOnTouchOfNonEditableViews()
    }

    @NeedsPermission(
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun startUpdateQuickRemarksService() {
        val imei = (application as ReleasingApplication).provideImei()
        viewModel.updateQuickRemarks(imei)
    }

    @NeedsPermission(
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun startUpdateDamagesService() {
        val imei = (application as ReleasingApplication).provideImei()
        viewModel.updateDamages(imei)
    }

    @OnShowRationale(
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun showPhoneStatePermissionRationale(request: PermissionRequest) {
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
    fun showDeniedForPhoneStatePermission() {
        notifyUser(binding.root, getString(R.string.phone_state_permission_denied))
    }

    @OnNeverAskAgain(
        android.Manifest.permission.READ_PHONE_STATE
    )
    fun neverAskForPhoneStatePermission() {
        notifyUser(binding.root, getString(R.string.phone_state_permission_never_ask))
    }

    private fun showMustUpdateDialog() {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.update_required_title),
            message = getString(R.string.must_update_required_msg),
            buttonText = getString(R.string.update_required_ok),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    val playStoreUtils = PlayStoreUtils(this@BaseActivity)
                    playStoreUtils.openPlayStore()
                }
            })
        dialogFragment.isCancelable = false
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    private fun showShouldUpdateDialog() {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.update_required_title),
            message = getString(R.string.should_update_required_msg),
            buttonText = getString(R.string.update_required_ok),
            hasNegativeButton = true,
            negativeButtonText = getString(R.string.update_required_cancel),
            negativeListener = object : InfoDialog.NegativeListener {
                override fun onNeutralClick() {
                    viewModel.resetRuntimeShouldUpdate()
                    viewModel.resetShowingDialog()
                }
            },
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    val playStoreUtils = PlayStoreUtils(this@BaseActivity)
                    playStoreUtils.openPlayStore()
                    viewModel.resetRuntimeShouldUpdate()
                    viewModel.resetShowingDialog()
                }
            })
        dialogFragment.isCancelable = false
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }


    protected fun handleNetworkConnect() {
        viewModel.checkForUpdates()
    }

    protected fun handleNetworkDisconnected() {
        //no implementation
    }


    private fun showLogOutConfirmDialog() {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.confirm_action),
            message = getString(R.string.log_out_confirm_message),
            buttonText = getString(R.string.yes),
            hasNegativeButton = true,
            negativeButtonText = getString(R.string.no),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    viewModel.logOutOperator()
                }
            })
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }


    override fun onResume() {
        super.onResume()
        viewModel.getOperatorName()
        viewModel.checkToShowUpdateAppDialog()
    }


    @SuppressLint("MissingPermission")
    @NeedsPermission(
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun getIMEI() {
        imei = imeiHelper.getImei()
        viewModel.imei = imei
        onImeiGotten(imei)
    }

    open fun onImeiGotten(imei: String?) {

    }

    @NeedsPermission(android.Manifest.permission.CAMERA)
    fun openBarCodeScanner(requestCode: Int) {
        val intent = Intent(this@BaseActivity, BarcodeScanActivity::class.java)
        startActivityForResult(intent, requestCode)
    }


    @OnShowRationale(android.Manifest.permission.CAMERA)
    fun showCameraRationale(request: PermissionRequest) {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.allow_permission),
            message = getString(R.string.allow_camera_permission_msg),
            buttonText = getString(android.R.string.ok),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    request.proceed()
                }
            })
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    @OnPermissionDenied(android.Manifest.permission.CAMERA)
    fun showDeniedForCamera() {
        notifyUser(binding.root, getString(R.string.camera_permission_denied))
    }

    @OnNeverAskAgain(android.Manifest.permission.CAMERA)
    fun neverAskForCamera() {
        notifyUser(binding.root, getString(R.string.camera_permission_never_ask))
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
        if (requestCode == RC_SEARCH && resultCode == RESULT_OK) {
            //save
            val scanned = data?.getStringExtra(Constants.BAR_CODE)
            Timber.d("Scanned: %s", scanned)
            viewModel.scanForSearch(scanned)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    open fun initBeforeView() {

    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResourceId())
        binding.lifecycleOwner = this
        binding.setVariable(getBindingVariable(), viewModel)
        binding.executePendingBindings()
    }


    @LayoutRes
    abstract fun getLayoutResourceId(): Int

    abstract fun getBindingVariable(): Int


    fun showUpEnabled(enabled: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(enabled)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    fun setHomeUpDrawable(@DrawableRes drawable: Int) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(drawable)
    }

    fun setHomeUpDrawable(drawable: Drawable) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(drawable)
    }


    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    @Suppress("DEPRECATION")
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val networkStateMenuItem = menu?.findItem(R.id.action_network_state)
        if (networkStateWrapper?.connected == true) {
            when (networkStateWrapper?.networkInfo?.type) {
                ConnectivityManager.TYPE_WIFI -> {
                    networkStateMenuItem?.title = getString(R.string.network_state_wifi)
                }

                ConnectivityManager.TYPE_MOBILE -> {
                    networkStateMenuItem?.title = getString(R.string.network_state_mobile)
                }
                else -> {
                    networkStateMenuItem?.title = getString(R.string.network_state_offline)
                }
            }
        } else {
            networkStateMenuItem?.title = getString(R.string.network_state_offline)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_network, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else if (item.itemId == R.id.action_network_state) {
            notifyUser(binding.root, getMessageByNetworkState(networkStateWrapper))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getMessageByNetworkState(networkStateWrapper: NetworkStateWrapper?): String {
        return when (networkStateWrapper?.connected) {
            true -> {
                when (networkStateWrapper.networkInfo?.type) {
                    ConnectivityManager.TYPE_WIFI -> {
                        getString(R.string.online_message, getString(R.string.network_state_wifi))
                    }

                    ConnectivityManager.TYPE_MOBILE -> {
                        getString(R.string.online_message, getString(R.string.network_state_mobile))
                    }
                    else -> {
                        getString(R.string.offline_message)
                    }
                }
            }
            else -> {
                getString(R.string.offline_message)
            }
        }
    }


    fun startNewActivity(name: Class<*>, finish: Boolean = false, data: Bundle? = null) {
        val intent = Intent(this, name)
        intent.putExtra(Constants.EXTRAS, data)
        startActivity(intent)
        if (finish) {
            finish()
        }
    }


    private fun showSnackBarError() {
        snackBar?.dismiss()
        snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.no_internet),
            Snackbar.LENGTH_INDEFINITE
        )
        val param = snackBar?.view?.layoutParams as FrameLayout.LayoutParams
        val snackBarLayout = snackBar?.view as Snackbar.SnackbarLayout
        snackBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed))
        snackBarLayout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            .setTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))
        param.gravity = Gravity.TOP


        snackBar?.view?.layoutParams = param
        if (!firstTime) {
            snackBar?.show()
            firstTime = false
        }
        Timber.d("Showing snack bar")
    }


    private fun showSnackBar() {
        snackBar?.dismiss()
        snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.internet_restored),
            Snackbar.LENGTH_SHORT
        )
        val param = snackBar?.view?.layoutParams as FrameLayout.LayoutParams
        val snackBarLayout = snackBar?.view as Snackbar.SnackbarLayout
        snackBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
        snackBarLayout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            .setTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))
        param.gravity = Gravity.TOP
        snackBar?.view?.layoutParams = param
        if (!firstTime) {
            snackBar?.show()
            firstTime = false
        }
        Timber.d("Showing snack bar")

    }


    fun notifyUser(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun notifyUser(view: View, message: String) {
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        /* FrameLayout.LayoutParams param = (FrameLayout.LayoutParams) snackBar.getView().getLayoutParams();*/
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
        snackBarLayout.setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorAccent))
        (snackBarLayout.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView)
            .setTextColor(ContextCompat.getColor(applicationContext, android.R.color.white))
        /*snackBar.getView().setLayoutParams(param);*/
        snackBar.show()
    }


    @Suppress("DEPRECATION")
    fun isOffline(): Boolean {
        return networkUtils.isOffline()
    }


    fun showLoading(view: View, textView: TextView, @StringRes message: Int) {
        showLoading(view, textView, getString(message))
    }

    fun showLoading(view: View, textView: TextView, message: String) {
        textView.text = message

        val bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up)

        view.startAnimation(bottomUp)
        view.visibility = View.VISIBLE
    }

    fun hideLoading(view: View) {
        view.visibility = View.GONE
    }

    @Suppress("NAME_SHADOWING")
    fun showErrorDialog(message: String?) {
        var message = message
        if (message == null || message.isEmpty()) {
            message = getString(R.string.error_occurred)
        }
        showDialog(getString(R.string.error), message)
    }

    fun showDialog(title: String?, message: String?) {
        val dialogFragment = InfoDialog.newInstance(
            title = title,
            message = message,
            buttonText = getString(android.R.string.ok)
        )
        Timber.e("msg: %s", message)
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }


    fun initErrorDrawable(imageView: ImageView) {
        val drawable = VectorDrawableCompat.create(resources, R.drawable.ic_error, null)
        val mutatedDrawable = drawable?.mutate()
        DrawableCompat.setTint(mutatedDrawable!!, ContextCompat.getColor(this, R.color.colorRed))
        imageView.setImageDrawable(mutatedDrawable)
    }
//    abstract fun observeNetworkChanges(connectivityObservable: Observable<Boolean>)


    protected abstract fun getViewModelClass(): Class<V>


    protected fun initOperator(operatorName: String) {
        Timber.d("Passed Operator name is %s", operatorName)
        findViewById<View>(R.id.include_operator_badge)?.visibility = View.VISIBLE
        val operatorIndicator = findViewById<ImageView>(R.id.img_indicator)
        operatorIndicator.setImageResource(R.drawable.operator_circle)
        val operatorNameTextView = findViewById<TextView>(R.id.tv_operator_name)
        operatorNameTextView?.text =
            if (TextUtils.isEmpty(operatorName)) getString(R.string.no_operator_logged_in) else operatorName
        val changeOperator = findViewById<Button>(R.id.btn_change)
        changeOperator?.text = getString(R.string.log_off)
        changeOperator?.setOnClickListener {
            viewModel.showLogOutConfirmDialog()
        }

    }

    protected fun hideOperator() {
        findViewById<View>(R.id.include_operator_badge)?.visibility = View.GONE
    }


}
