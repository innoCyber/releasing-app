package ptml.releasing.download_damages.view

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import permissions.dispatcher.*
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.utils.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.Status
import ptml.releasing.databinding.ActivityDamageBinding
import ptml.releasing.download_damages.model.Damage
import ptml.releasing.download_damages.viewmodel.DamageViewModel
import timber.log.Timber

@RuntimePermissions
class DamageActivity : BaseActivity<DamageViewModel, ActivityDamageBinding>() {

    private val listener = object : DamageListener {
        override fun onItemClick(item: Damage?) {
            Timber.d("clicked: %s", item)
        }
    }
    private val adapter = DamageAdapter(listener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)
        binding.recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        val decorator = DividerItemDecoration(binding.recyclerView.context, layoutManager.orientation)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addItemDecoration(decorator)


        viewModel.getNetworkState().observe(this, Observer {event->
            event.getContentIfNotHandled()?.let {
                if (NetworkState.LOADING == it) {
                    showLoading(binding.includeProgress.root, binding.includeProgress.tvMessage, R.string.downloading_damages)
                    Timber.e("Loading...")
                } else {
                    hideLoading(binding.includeProgress.root)
                }

                //disable the fab when data is loading
                binding.fab.isEnabled = it != NetworkState.LOADING

                if (it.status == Status.FAILED) {
                    val error = ErrorHandler(this).getErrorMessage(it.throwable)
                    showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
                } else {
                    hideLoading(binding.includeError.root)
                }
            }
        })

        viewModel.getResponse().observe(this, Observer {
            adapter.damageList.clear()
            adapter.damageList.addAll(it)
            adapter.notifyDataSetChanged()
        })



        binding.includeError.btnReloadLayout.setOnClickListener {
            downloadDamagesWithPermissionCheck()
        }

        binding.fab.setOnClickListener {
            downloadDamagesWithPermissionCheck()
        }

        getDamagesWithPermissionCheck()
    }


    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun downloadDamages() {
        viewModel.downloadDamagesFromServer((application as ReleasingApplication).provideImei())
    }

    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getDamages() {
        viewModel.getDamages((application as ReleasingApplication).provideImei())
    }

    @OnShowRationale(android.Manifest.permission.READ_PHONE_STATE)
    fun showInitRecognizerRationale(request: PermissionRequest) {
        val dialogFragment =  InfoDialog.newInstance(
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



    override fun getLayoutResourceId() = R.layout.activity_damage
    override fun getBindingVariable() = BR.viewModel
    override fun getViewModelClass() = DamageViewModel::class.java


}