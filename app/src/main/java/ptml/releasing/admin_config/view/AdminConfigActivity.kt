package ptml.releasing.admin_config.view

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.admin_config.viewmodel.AdminConfigViewModel
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.auth.view.LoginActivity
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.damages.view.DamageActivity
import ptml.releasing.databinding.ActivityAdminConfigBinding
import java.util.*

class AdminConfigActivity : BaseActivity<AdminConfigViewModel, ActivityAdminConfigBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getSavedConfig()
        viewModel.isConfigured.observe(this, Observer {
            binding.tvConfigMessageContainer.visibility =
                if (it) View.GONE else View.VISIBLE //hide or show the not configured message
        })




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


        showUpEnabled(true)

        binding.includeAdminConfig.btnConfigurationLayout.setOnClickListener {
            viewModel.openConfiguration()
        }

        binding.includeAdminConfig.btnDownloadLayout.setOnClickListener {
            viewModel.openDownloadDamages()
        }


    }


    override fun onResume() {
        super.onResume()
        viewModel.getSavedConfig()
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