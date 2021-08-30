package ptml.releasing.admin_config.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import ptml.releasing.BR
import ptml.releasing.BuildConfig
import ptml.releasing.R
import ptml.releasing.admin_config.viewmodel.AdminConfigViewModel
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage
import ptml.releasing.app.dialogs.EditTextDialog
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.dialogs.SelectTerminalDialog
import ptml.releasing.app.dialogs.SelectVoyageDialog
import ptml.releasing.app.utils.livedata.Event
import ptml.releasing.app.utils.livedata.observe
import ptml.releasing.app.utils.livedata.observeEvent
import ptml.releasing.cargo_search.view.SearchActivity
import ptml.releasing.configuration.models.AdminConfigResponse
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.configuration.models.ReleasingTerminal
import ptml.releasing.configuration.view.ConfigActivity
import ptml.releasing.databinding.ActivityAdminConfigBinding
import ptml.releasing.download_damages.view.DamageActivity
import ptml.releasing.internet_error_logs.view.ErrorLogsActivity
import ptml.releasing.printer.view.PrinterSettingsActivity
import ptml.releasing.quick_remarks.view.QuickRemarkActivity

class AdminConfigActivity : BaseActivity<AdminConfigViewModel, ActivityAdminConfigBinding>() {

    companion object {
        const val RC_CONFIG = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)
        setupObservers()
        setupClickListeners()
        binding.includeAdminConfig.btnEnterImei.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }

    private fun setupClickListeners() {
        binding.includeAdminConfig.btnConfiguration.setOnClickListener {
            viewModel.openConfig()
        }


        binding.includeAdminConfig.btnSelectTerminal.setOnClickListener {
            viewModel.openTerminalSelection()
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
            viewModel.openServerUrl()
        }

        binding.includeAdminConfig.btnQuickRemarks.setOnClickListener {
            viewModel.openQuickRemark()
        }

        binding.includeAdminConfig.btnErrorLogs.setOnClickListener {
            viewModel.openErrorLogs()
        }

        binding.includeAdminConfig.btnEnableLogs.setOnClickListener {
            viewModel.handleAskToToggleEnableLogs()
        }

        binding.includeAdminConfig.btnEnterImei.setOnClickListener {
            viewModel.openEnterImei()
        }
    }

    private fun setupObservers() {
        viewModel.isConfigured.observe(this) {
            binding.tvConfigMessageContainer.visibility =
                if (it) View.GONE else View.VISIBLE //hide or show the not configured message
        }

        viewModel.getSavedConfig()

        viewModel.openDownloadDamages.observeEvent(this) {
            if (it) {
                startNewActivity(DamageActivity::class.java)
            } else {
                showConfigurationErrorDialog()
            }
        }

        viewModel.openQuickRemark.observeEvent(this) {
            if (it) {
                startNewActivity(QuickRemarkActivity::class.java)
            } else {
                showConfigurationErrorDialog()
            }
        }

        viewModel.openPrinterSettings.observeEvent(this) {
            startNewActivity(PrinterSettingsActivity::class.java)
        }

        viewModel.openConfig.observeEvent(this) {
            val intent = Intent(this, ConfigActivity::class.java)
            startActivityForResult(intent, RC_CONFIG)
        }

        viewModel.serverUrl.observeEvent(this) {
            showServerUrlDialog(it)
        }

        viewModel.terminal.observeEvent(this) {adminConfigResponse ->
            viewModel.selectedTerminal.observe(this, Observer {selectedItem ->
                showSelectTerminalDialog(adminConfigResponse, selectedItem)
            })

        }



        viewModel.openSearch.observeEvent(this) {
            onBackPressed()
        }

        viewModel.openErrorLogs.observeEvent(this) {
            startNewActivity(ErrorLogsActivity::class.java)
        }

        viewModel.internetErrorEnabled.observe(this) {
            binding.includeAdminConfig.textEnableLogs.text =
                getString(if (it) R.string.disable_logs else R.string.enable_logs)
        }

        viewModel.openConfirmShowLogs.observeEvent(this) {
            val dialogFragment = InfoDialog.newInstance(
                title = getString(R.string.confirm_action),
                message = getString(if (it == true) R.string.disable_logs_confirm_message else R.string.enable_logs_confirm_message),
                buttonText = getString(android.R.string.ok),
                listener = object : InfoDialog.InfoListener {
                    override fun onConfirm() {
                        viewModel.handleToggleEnableLogs()
                    }
                })
            dialogFragment.isCancelable = false
            dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
        }

        viewModel.imeiNumber.observeEvent(this) {
            showEnterImeiDialog(it)
        }
    }

    override fun onBackPressed() {
        startNewActivity(SearchActivity::class.java)
        ActivityCompat.finishAffinity(this)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_CONFIG && resultCode == Activity.RESULT_OK) {
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

    private fun showServerUrlDialog(url: String?) {
        val serverUrl = if (TextUtils.isEmpty(url)) {
            BuildConfig.BASE_URL
        } else {
            url
        }
        val dialog =
            EditTextDialog.newInstance(serverUrl, object : EditTextDialog.EditTextDialogListener {
                override fun onSave(value: String) {
                    viewModel.saveServerUrl(value)
                }
            })
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, dialog.javaClass.name)
    }

    private fun showSelectTerminalDialog(config: AdminConfigResponse, selected: Event<Configuration>) {

        val dialog =
            config.terminalList?.let {
                selected.getContentIfNotHandled()?.let { selected ->
                    SelectTerminalDialog.newInstance(it, selected, object : SelectTerminalDialog.SelectTerminalListener {
                        override fun onSave(terminal: ReleasingTerminal) {
                            if(selected.operationStep == null || selected.cargoType == null){
                                viewModel.adminConfigAsync.observe(this@AdminConfigActivity, Observer {adminConfigResponse ->
                                    saveConfiguration(terminal, adminConfigResponse.getContentIfNotHandled())
                                })
                            }else{
                                saveConfiguration(terminal, config)
                            }
                        }

                        private fun saveConfiguration(terminal: ReleasingTerminal, config: AdminConfigResponse?) {
                            val configuration =
                                Configuration(
                                    terminal,
                                    selected.operationStep ?: config?.operationStepList?.firstOrNull(),
                                    selected.cargoType ?: config?.cargoTypeList?.firstOrNull(),
                                    selected.shippingLine?: config!!.shippingLineList?.firstOrNull(),
                                    selected.voyage?: config!!.voyage?.firstOrNull(),
                                    false
                                )
                            viewModel.saveSelectedTerminal(configuration)
                            Toast.makeText(
                                this@AdminConfigActivity,
                                "Terminal is now set to ${terminal.value}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
                }
            }
        if (dialog != null) {
            dialog.isCancelable = false
            dialog.show(supportFragmentManager, dialog.javaClass.name)
        }

    }



    private fun showEnterImeiDialog(deviceId: String?) {
        val imeiNumber = if (imei.isNullOrEmpty()) {
            imei
        } else {
            deviceId
        }
        val dialog =
            EditTextDialog.newInstance(imeiNumber, object : EditTextDialog.EditTextDialogListener {
                override fun onSave(value: String) {
                    viewModel.updateImei(value)
                }
            }, getString(R.string.enter_imei_dialog_title), getString(R.string.enter_imei_dialog_hint), false)
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, dialog.javaClass.name)
    }

    override fun getViewModelClass() = AdminConfigViewModel::class.java


    override fun getBindingVariable() = BR.viewModel

    override fun getLayoutResourceId() = R.layout.activity_admin_config
}