package ptml.releasing.admin_config.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import me.seebrock3r.elevationtester.TweakableOutlineProvider
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.admin_config.viewmodel.AdminConfigViewModel
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.auth.view.LoginActivity
import ptml.releasing.damages.view.DamageActivity
import ptml.releasing.databinding.ActivityAdminConfigBinding
import ptml.releasing.search.view.SearchActivity

class AdminConfigActivity : BaseActivity<AdminConfigViewModel, ActivityAdminConfigBinding>() {

    private lateinit var outlineProvider: TweakableOutlineProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isConfigured.observe(this, Observer {
            binding.tvConfigMessageContainer.visibility =
                if (it) View.GONE else View.VISIBLE //hide or show the not configured message
        })

        viewModel.firstTimeLogin.observe(this, Observer {
            if(it){
                startNewActivity(LoginActivity::class.java)
            }
        })

        viewModel.firstTimeFindCargo.observe(this, Observer {
            if(it){
                startNewActivity(SearchActivity::class.java)
            }
        })

        viewModel.getSavedConfig()

        viewModel.openDownloadDamages.observe(this, Observer {
            if (it) {
                startNewActivity(DamageActivity::class.java)
            } else {
                showConfigurationErrorDialog()
            }
        })

        viewModel.openConfiguration.observe(this, Observer {
            startNewActivity(LoginActivity::class.java)
        })


        viewModel.openSearch.observe(this, Observer {
            if (it) {
                startNewActivity(SearchActivity::class.java)
            } else {
                showConfigurationErrorDialog()
            }
        })

        showUpEnabled(true)


        binding.includeAdminConfig.btnImei.setOnClickListener {
            showImeiDialog()
        }

        binding.includeAdminConfig.btnConfiguration.setOnClickListener {
            viewModel.openConfiguration()
        }

        binding.includeAdminConfig.btnDownload.setOnClickListener {
            viewModel.openDownloadDamages()
        }


        binding.includeAdminConfig.btnSearch.setOnClickListener {
            viewModel.openSearch()
        }


    }


    override fun onResume() {
        super.onResume()
        viewModel.getSavedConfig()
    }

    private fun showImeiDialog() {
        val dialogFragment =  InfoDialog.newInstance(
            title = getString(R.string.device_IMEI),
            message = (application as ReleasingApplication).provideImei(),
            buttonText = getString(R.string.dismiss))
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    private fun showConfigurationErrorDialog() {
        /*     InfoConfirmDialog.showDialog(context = this,
                     title = getString(R.string.config_error),
                     message = getString(R.string.config_error_message),
                     topIcon = R.drawable.ic_error, listener = object : InfoConfirmDialog.InfoListener {
                 override fun onConfirm() {
     //                    viewModel.openConfiguration()
                 }
             })*/

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

    override fun getViewModelClass() = AdminConfigViewModel::class.java


    override fun getBindingVariable() = BR.viewModel

    override fun getLayoutResourceId() = R.layout.activity_admin_config
}