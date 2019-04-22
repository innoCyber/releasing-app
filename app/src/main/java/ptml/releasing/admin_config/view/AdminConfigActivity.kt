package ptml.releasing.admin_config.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_admin_settings_advanced_printer.*
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.admin_config.viewmodel.AdminConfigViewModel
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.configuration.view.ConfigActivity
import ptml.releasing.download_damages.view.DamageActivity
import ptml.releasing.databinding.ActivityAdminConfigBinding
import ptml.releasing.cargo_search.view.SearchActivity
import ptml.releasing.printer.AdminPrinterSettingsAdvancedActivity

class AdminConfigActivity : BaseActivity<AdminConfigViewModel, ActivityAdminConfigBinding>() {

    companion object {
        const val RC = 111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isConfigured.observe(this, Observer {
            binding.tvConfigMessageContainer.visibility =
                if (it) View.GONE else View.VISIBLE //hide or show the not configured message
        })


        viewModel.getSavedConfig()

        viewModel.openDownloadDamages.observe(this, Observer {
            if (it) {
                startNewActivity(DamageActivity::class.java)
            } else {
                showConfigurationErrorDialog()
            }
        })

        viewModel.openPrinterSettings.observe(this, Observer {
          startNewActivity(AdminPrinterSettingsAdvancedActivity::class.java)
        })

        viewModel.openConfiguration.observe(this, Observer {
            val intent = Intent(this, ConfigActivity::class.java)
            startActivityForResult(intent, RC )
        })


        showUpEnabled(true)

        binding.includeAdminConfig.btnConfiguration.setOnClickListener {
            viewModel.openConfiguration()
        }

        binding.includeAdminConfig.btnDownload.setOnClickListener {
            viewModel.openDownloadDamages()
        }

        binding.includeAdminConfig.btnSettings.setOnClickListener {
            viewModel.openPrinterSetting()
        }




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == RC && resultCode == Activity.RESULT_OK){
            startNewActivity(SearchActivity::class.java)
            finish()
        }else{
            super.onActivityResult(requestCode, resultCode, data)
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