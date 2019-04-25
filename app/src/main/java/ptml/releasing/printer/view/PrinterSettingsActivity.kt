package ptml.releasing.printer.view

import android.app.Activity
import android.os.Bundle
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.printer.discovery.BluetoothDiscoverer
import com.zebra.sdk.printer.discovery.DiscoveredPrinter
import com.zebra.sdk.printer.discovery.DiscoveryHandler
import dagger.android.support.DaggerAppCompatActivity
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.utils.Constants
import ptml.releasing.damages.view_model.SelectDamageViewModel
import ptml.releasing.databinding.ActivityPrinterSettingsBinding
import ptml.releasing.printer.viewmodel.PrinterSettingsViewModel


class PrinterSettingsActivity : BaseActivity<PrinterSettingsViewModel, ActivityPrinterSettingsBinding>() {
    private var currentPrinter: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.admin_settings_advanced_title)
        showUpEnabled(true)

        viewModel.printerSettings.observe(this, Observer {
            binding.AdminPrinterSettingsEdtLabelCpclData.setText(if(it.labelCpclData.isNullOrEmpty()) Constants.DEFAULT_PRINTER_CODE else it.labelCpclData)
            binding.AdminPrinterSettingsEdtPrinterValue.setText(it.currentPrinterName)
        })

        viewModel.getSettings()
        viewModel.close.observe(this, Observer {
            setResult(Activity.RESULT_OK)
            finish()
        })

        binding.AdminPrinterSettingsEdtLabelCpclData.setText(Constants.DEFAULT_PRINTER_CODE)

        binding.AdminPrinterSettingsBtnReset.setOnClickListener {
            binding.AdminPrinterSettingsEdtLabelCpclData.setText(
                Constants.DEFAULT_PRINTER_CODE
            )
        }

        binding.AdminPrinterSettingsBtnClose.setOnClickListener {
            saveAndClose()
        }

        binding.AdminPrinterSettingsBtnChangePrinter.setOnClickListener {
            val builderSingle = AlertDialog.Builder(this@PrinterSettingsActivity)
            builderSingle.setTitle("Discovering printers...")

            val arrayAdapter = ArrayAdapter<DiscoveredPrinter>(
                this@PrinterSettingsActivity,
                android.R.layout.select_dialog_singlechoice
            )

            builderSingle.setNegativeButton("cancel") { dialog, which -> dialog.dismiss() }

            builderSingle.setAdapter(arrayAdapter) { dialog, which ->
                val address = arrayAdapter.getItem(which)!!.address
                val name = arrayAdapter.getItem(which)!!.discoveryDataMap["FRIENDLY_NAME"]

                for (settingsKey in arrayAdapter.getItem(which)!!.discoveryDataMap.keys) {
                    println("Key: " + settingsKey + " Value: " + arrayAdapter.getItem(which)!!.discoveryDataMap[settingsKey])
                }

                currentPrinter = address
                binding.AdminPrinterSettingsEdtPrinterValue.setText(name)
                dialog.dismiss()
            }

            val printerDialog = builderSingle.show()

            Thread(Runnable {
                Looper.prepare()

                try {
                    BluetoothDiscoverer.findPrinters(this@PrinterSettingsActivity,
                        object : DiscoveryHandler {
                            override fun foundPrinter(discoveredPrinter: DiscoveredPrinter) {

                                runOnUiThread {
                                    arrayAdapter.add(discoveredPrinter)
                                    arrayAdapter.notifyDataSetChanged()
                                }
                            }

                            override fun discoveryFinished() {

                                runOnUiThread {
                                    if (arrayAdapter.count == 0)
                                        builderSingle.setTitle("No printers found.")
                                    else
                                        builderSingle.setTitle("Select a printer:")
                                }
                            }

                            override fun discoveryError(errorMessage: String) {

                                runOnUiThread {
                                    printerDialog.dismiss()
                                    AlertDialog.Builder(this@PrinterSettingsActivity)
                                        .setMessage(errorMessage)
                                        .setPositiveButton(android.R.string.ok) { dialog, which -> dialog.dismiss() }
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show()
                                }
                            }
                        }) { true }
                } catch (ce: ConnectionException) {
                    ce.printStackTrace()
                } finally {
                    Looper.myLooper()?.quit()
                }
            }).start()
        }
    }

    private fun saveAndClose() {
        viewModel.setSettings(
            currentPrinter, binding.AdminPrinterSettingsEdtPrinterValue.text.toString(),
            binding.AdminPrinterSettingsEdtLabelCpclData.text.toString()
        )
    }

    override fun onBackPressed() {
        saveAndClose()
    }


    override fun getLayoutResourceId() = R.layout.activity_printer_settings

    override fun getBindingVariable() = BR.viewModel

    override fun getViewModelClass() = PrinterSettingsViewModel::class.java
}
