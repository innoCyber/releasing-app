package ptml.releasing.ui.setup

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import permissions.dispatcher.*
import ptml.releasing.R
import ptml.releasing.app.App
import ptml.releasing.ui.DeviceConfigErrorActivity
import ptml.releasing.ui.MainActivity
import ptml.releasing.ui.base.BaseActivity
import ptml.releasing.ui.dialogs.InfoConfirmDialog
import ptml.releasing.utils.ErrorHandler
import ptml.releasing.utils.NetworkState
import ptml.releasing.utils.NotificationUtils
import ptml.releasing.utils.Status
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@RuntimePermissions
class SetupActivity : BaseActivity() {
    lateinit var binding: ptml.releasing.databinding.ProgressBarBinding

    @Inject
    lateinit var viewModeFactory: ViewModelProvider.Factory

    lateinit var setupActivityViewModel: SetupActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.progress_bar)

        setupActivityViewModel = ViewModelProviders.of(this, viewModeFactory)
            .get(SetupActivityViewModel::class.java)

        setupActivityViewModel.baseLiveData.observe(this, Observer {
            if(true == it?.isSuccess){
                startNewActivity(MainActivity::class.java)
            }else if(false == it?.isSuccess){
                startNewActivity(DeviceConfigErrorActivity::class.java)
            }
        })

        setupActivityViewModel.networkState.observe(this, Observer {
            if(NetworkState.LOADING == it){
                binding.progressBar.visibility = View.VISIBLE
                binding.tvMessage.visibility = View.VISIBLE
                Timber.e("Loading...")
            }
            if (it?.status == Status.FAILED) {
                showSnackBarError(it)
            }
        })



        verifyDeviceIdWithPermissionCheck()
    }


    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun verifyDeviceId() {
        setupActivityViewModel.verifyDeviceId((application as App).provideImei())
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
        NotificationUtils.notifyUser(binding.root, getString(R.string.phone_state_permission_denied))
    }

    @OnNeverAskAgain(android.Manifest.permission.READ_PHONE_STATE)
    fun neverAskForInitRecognizer() {
        NotificationUtils.notifyUser(binding.root, getString(R.string.phone_state_permission_never_ask))
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }


    private fun showSnackBarError(it: NetworkState) {
       val error = ErrorHandler(this).getErrorMessage(it.throwable)
        binding.progressBar.visibility = View.GONE
        binding.tvMessage.visibility = View.GONE
        val snackbar = Snackbar.make(binding.root,
            error, Snackbar.LENGTH_INDEFINITE)
        val snackBarLayout = snackbar.getView() as Snackbar.SnackbarLayout
        snackBarLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        (snackBarLayout.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView)
            .setTextColor(this.resources.getColor(android.R.color.white))

        (snackBarLayout.findViewById<View>(com.google.android.material.R.id.snackbar_action) as TextView)
            .setTextColor(this.resources.getColor(android.R.color.white))

        snackbar.setAction(getString(R.string.retry)) {
            snackbar.dismiss()
            verifyDeviceIdWithPermissionCheck()
        }
        snackbar.show()
    }

    private fun startNewActivity(name:Class<*>){
        val intent = Intent(this, name)
        startActivity(intent)
        finish()
    }
}