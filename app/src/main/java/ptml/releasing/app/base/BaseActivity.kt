package ptml.releasing.app.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import permissions.dispatcher.*
import ptml.releasing.R
import ptml.releasing.app.dialogs.ChooseOperatorInputDialog
import ptml.releasing.app.dialogs.EditTextDialog
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.utils.Constants
import ptml.releasing.barcode_scan.BarcodeScanActivity
import ptml.releasing.cargo_info.view.CargoInfoActivity
import ptml.releasing.cargo_search.view.SearchActivity
import ptml.releasing.login.view.LoginActivity
import timber.log.Timber
import javax.inject.Inject

@RuntimePermissions
abstract class BaseActivity<T, D> : DaggerAppCompatActivity() where T : BaseViewModel, D : ViewDataBinding {
    private val networkSubject = MutableLiveData<Boolean>()
    private lateinit var receiver: BroadcastReceiver
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var snackBar: Snackbar? = null
    private var firstTime = true


    protected lateinit var binding: D
    protected lateinit var viewModel: T

    @Inject
    protected lateinit var viewModeFactory: ViewModelProvider.Factory


    companion object {
        const val RC_BARCODE = 112
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModeFactory)
            .get(getViewModelClass())

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

        viewModel.savedOperatorName.observe(this, Observer {
            notifyUser(binding.root, getString(R.string.operator_name_saved_success_msg, it))
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

        viewModel.openEnterDialog.observe(this, Observer {
            openEnterDialog(it)
        })


        viewModel.openOperatorDialog.observe(this, Observer {
            openOperatorDialog()
        })

        viewModel.logOutDialog.observe(this, Observer {
            showLogOutConfirmDialog()
        })

        viewModel.logOutOperator.observe(this, Observer {
            notifyUser(binding.root, getString(R.string.operator_log_out_msg, it))
        })

    }

    private fun showLogOutConfirmDialog() {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.confirm_action),
            message = getString(R.string.log_out_confirm_message),
            buttonText = getString(R.string.yes),
            hasNeutralButton = true,
            neutralButtonText = getString(R.string.no),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    viewModel.logOutOperator()
                }
            })
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    private fun openOperatorDialog() {
        val dialog = ChooseOperatorInputDialog.newInstance(object : ChooseOperatorInputDialog.ChooseOperatorListener {
            override fun onScan() {
                viewModel.openBarCodeScanner()
            }

            override fun onEnter() {
                viewModel.openEnterDialog()
            }
        })
        dialog.show(supportFragmentManager, dialog.javaClass.name)
    }

    private fun openEnterDialog(it: String?) {
        val dialog = EditTextDialog.newInstance(it, object : EditTextDialog.EditTextDialogListener {
            override fun onSave(value: String) {
                viewModel.saveOperatorName(value)
            }
        }, getString(R.string.enter_operator_name), getString(R.string.operator_name), false)
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, dialog.javaClass.name)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getOperatorName()
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


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_BARCODE && resultCode == RESULT_OK) {
            val operatorName = data?.getStringExtra(Constants.BAR_CODE)
            //save
            Timber.d("Scanned Operator name: %s", operatorName)
            viewModel.saveOperatorName(operatorName)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    open fun initBeforeView() {

    }


    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutResourceId())
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupConnectionListener()
        } else {
            initializeReceiver()
        }

        networkSubject.observe(this, Observer {
            when (it) {
                true -> showSnackBar()
                else -> showSnackBarError()
            }
        })
    }

    override fun onStop() {
        super.onStop()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val connectivityManager = applicationContext.getSystemService<ConnectivityManager>()
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        } else {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        }

    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupConnectionListener() {
        Timber.d("Setting up connectivity listener")
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network?) {
                Timber.d("ReleasingApplication lost connection")
                networkSubject.postValue(false)
            }

            override fun onAvailable(network: Network?) {
                Timber.d("ReleasingApplication gained connection")
                networkSubject.postValue(true)
            }
        }
        val networkRequest = NetworkRequest.Builder().build()
        val connectivityManager = applicationContext.getSystemService<ConnectivityManager>()
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
//        observeNetworkChanges(networkSubject)
    }

    private fun initializeReceiver() {
        val filter = IntentFilter(Constants.NETWORK_STATE_INTENT)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
//        observeNetworkChanges(networkSubject)
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
        val manager = this
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return !(manager.activeNetworkInfo != null && manager.activeNetworkInfo.isConnectedOrConnecting)
    }


    fun showLoading(view: View, textView: TextView, @StringRes message: Int) {
        textView.text = getString(message)

        val bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up)

        view.startAnimation(bottomUp)
        view.visibility = View.VISIBLE
    }

    fun hideLoading(view: View) {

        val slide = AnimationUtils.loadAnimation(
            this,
            R.anim.up_down
        )
        view.startAnimation(slide)
        view.visibility = View.GONE
    }

    fun showErrorDialog(message: String?) {
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
        val errorDrawable = ContextCompat.getDrawable(this, R.drawable.ic_error)
        val mutatedDrawable = errorDrawable?.mutate()
        DrawableCompat.setTint(mutatedDrawable!!, ContextCompat.getColor(this, R.color.colorRed))
        imageView.setImageDrawable(mutatedDrawable)
    }
//    abstract fun observeNetworkChanges(connectivityObservable: Observable<Boolean>)


    protected abstract fun getViewModelClass(): Class<T>


    protected fun initOperator(operatorName: String?) {
        Timber.d("Passed Operator name is %s", operatorName)
        findViewById<View>(R.id.include_operator_badge)?.visibility = View.VISIBLE
        val operatorIndicator = findViewById<ImageView>(R.id.img_indicator)
        operatorIndicator.setImageResource(if (TextUtils.isEmpty(operatorName)) R.drawable.operator_circle_red else R.drawable.operator_circle)
        val operatorNameTextView = findViewById<TextView>(R.id.tv_operator_name)
        operatorNameTextView?.text = if (TextUtils.isEmpty(operatorName)) getString(R.string.no_operator_logged_in) else operatorName
        val changeOperator = findViewById<Button>(R.id.btn_change)
        changeOperator?.text =
            if (TextUtils.isEmpty(operatorName)) getString(R.string.add_operator) else getString(R.string.log_off)
        changeOperator?.setOnClickListener {
            if (TextUtils.isEmpty(operatorName)) {
                viewModel.openOperatorDialog()
            } else {
                viewModel.showLogOutConfirmDialog()
            }
        }

    }

    protected fun hideOperator() {
        findViewById<View>(R.id.include_operator_badge)?.visibility = View.GONE
    }


}
