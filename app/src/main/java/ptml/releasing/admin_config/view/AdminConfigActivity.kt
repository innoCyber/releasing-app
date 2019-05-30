package ptml.releasing.admin_config.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import ptml.releasing.BR
import ptml.releasing.BuildConfig
import ptml.releasing.R
import ptml.releasing.admin_config.viewmodel.AdminConfigViewModel
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.EditTextDialog
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.configuration.view.ConfigActivity
import ptml.releasing.databinding.ActivityAdminConfigBinding
import ptml.releasing.download_damages.view.DamageActivity
import ptml.releasing.printer.view.PrinterSettingsActivity
import ptml.releasing.quick_remarks.view.QuickRemarkActivity

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

        viewModel.openQuickRemark.observe(this, Observer {
            if (it) {
                startNewActivity(QuickRemarkActivity::class.java)
            } else {
                showConfigurationErrorDialog()
            }
        })

        viewModel.openPrinterSettings.observe(this, Observer {
            startNewActivity(PrinterSettingsActivity::class.java)
        })

        viewModel.openConfig.observe(this, Observer {
            val intent = Intent(this, ConfigActivity::class.java)
            startActivityForResult(intent, RC)
        })

        viewModel.serverUrl.observe(this, Observer {
            showServerUrlDialog(it)
        })

        viewModel.openSearch.observe(this, Observer {
            onBackPressed()
        })


        showUpEnabled(true)

        binding.includeAdminConfig.btnConfiguration.setOnClickListener {
            viewModel.openConfig()
        }

        binding.includeAdminConfig.btnDownload.setOnClickListener {
            viewModel.openDownloadDamages()
        }

        binding.includeAdminConfig.btnSettings.setOnClickListener {
            viewModel.openPrinterSetting()
        }



        binding.includeAdminConfig.btnSearch.setOnClickListener {
            viewModel.openSearch()
        }

        binding.includeAdminConfig.btnServer.setOnClickListener {
            viewModel.openServer()
        }

        binding.includeAdminConfig.btnQuickRemarks.setOnClickListener {
            viewModel.openQuickRemark()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC && resultCode == Activity.RESULT_OK) {
            finish()
        } else {
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
        dialogFragment.isCancelable = false
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    private fun showServerUrlDialog(url:String?){
        val serverUrl = if(TextUtils.isEmpty(url)){
            BuildConfig.BASE_URL
        }else{
            url
        }
        val dialog = EditTextDialog.newInstance(serverUrl, object : EditTextDialog.EditTextDialogListener{
            override fun onSave(value: String) {
                viewModel.saveServerUrl(value)
            }
        })
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, dialog.javaClass.name)
    }

    override fun getViewModelClass() = AdminConfigViewModel::class.java


    override fun getBindingVariable() = BR.viewModel

    override fun getLayoutResourceId() = R.layout.activity_admin_config
}