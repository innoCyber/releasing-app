package ptml.releasing.printer.view

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.printer.discovery.BluetoothDiscoverer
import com.zebra.sdk.printer.discovery.DiscoveredPrinter
import com.zebra.sdk.printer.discovery.DiscoveryHandler
import permissions.dispatcher.*
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.bt.BluetoothArrayAdapter
import ptml.releasing.app.utils.bt.BluetoothManager
import ptml.releasing.app.utils.bt.DiscoveringDevicesDialog
import ptml.releasing.databinding.ActivityPrinterSettingsBinding
import ptml.releasing.printer.viewmodel.PrinterSettingsViewModel
import timber.log.Timber

@RuntimePermissions
class PrinterSettingsActivity : BaseActivity<PrinterSettingsViewModel, ActivityPrinterSettingsBinding>() {
    private var currentPrinterAddress: String? = null
    private lateinit var bluetoothManager: BluetoothManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.admin_settings_advanced_title)
        showUpEnabled(true)

        bluetoothManager = BluetoothManager(this)
        bluetoothManager.adapterListener = object : BluetoothManager.AdapterListener {
            override fun onAdapterError() {
                Timber.e("No Bluetooth device")
                showErrorDialog(getString(R.string.no_bt_message))
            }
        }

        viewModel.getPrinterSettings().observe(this, Observer {
            binding.AdminPrinterSettingsEdtLabelCpclData.setText(if (it.labelCpclData.isNullOrEmpty()) Constants.DEFAULT_BARCODE_PRINTER_SETTINGS else it.labelCpclData)
            binding.AdminPrinterSettingsEdtPrinterValue.setText(it.currentPrinterName)
        })

        viewModel.getSettings()
        viewModel.getClose().observe(this, Observer {event->
            event.getContentIfNotHandled().let {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })

        binding.AdminPrinterSettingsEdtLabelCpclData.setText(Constants.DEFAULT_BARCODE_PRINTER_SETTINGS)

        binding.AdminPrinterSettingsBtnReset.setOnClickListener {
            binding.AdminPrinterSettingsEdtLabelCpclData.setText(
                Constants.DEFAULT_BARCODE_PRINTER_SETTINGS
            )
        }

        binding.AdminPrinterSettingsBtnClose.setOnClickListener {
            saveAndClose()
        }

        binding.AdminPrinterSettingsBtnChangePrinter.setOnClickListener {
            handleSelectPrinterClick()
        }
    }


    private fun handleSelectPrinterClick() {
        if(bluetoothManager.bluetoothAdapter == null){
            showErrorDialog(getString(R.string.no_bt_message))
            Timber.e("No Bluetooth device")
            return
        }
        if (bluetoothManager.bluetoothAdapter?.isEnabled == true) {
            getBluetoothDevicesWithPermissionCheck()
        } else {
            attemptToTurnBluetoothOn()
        }
    }

    private fun attemptToTurnBluetoothOn() {
        startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), RC_BT)
    }


    @NeedsPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun getBluetoothDevices() {
        val builderSingle = AlertDialog.Builder(this@PrinterSettingsActivity)
        builderSingle.setTitle("Discovering printers...")

        val arrayAdapter = ArrayAdapter<DiscoveredPrinter>(
            this@PrinterSettingsActivity,
            android.R.layout.select_dialog_singlechoice
        )

        builderSingle.setNegativeButton(
            "cancel"
        ) { dialog, _ -> dialog.dismiss() }

        builderSingle.setAdapter(arrayAdapter) { dialog, which ->
            val address = arrayAdapter.getItem(which)!!.address
            val name = arrayAdapter.getItem(which)!!.discoveryDataMap["FRIENDLY_NAME"]

            for (settingsKey in arrayAdapter.getItem(which)!!.discoveryDataMap.keys) {
                println("Key: " + settingsKey + " Value: " + arrayAdapter.getItem(which)!!.discoveryDataMap[settingsKey])
            }

            currentPrinterAddress = address
            binding.AdminPrinterSettingsEdtPrinterValue.setText(name)
            dialog.dismiss()
        }

        val printerDialog = builderSingle.show()

        Thread(Runnable {
            Looper.prepare()
            Timber.d("Starting to connect...")
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
                                    .setPositiveButton(
                                        android.R.string.ok
                                    ) { dialog, _ -> dialog.dismiss() }
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show()
                            }
                        }
                    }) { true }
            } catch (ce: ConnectionException) {
                Timber.e(ce)
            } finally {
                Looper.myLooper()?.quit()
            }
        }).start()
    }

    @OnShowRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun showLocationRationale(request: PermissionRequest) {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.allow_permission),
            message = getString(R.string.allow_location_permission_msg),
            buttonText = getString(android.R.string.ok),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    request.proceed()
                }
            })
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    @OnPermissionDenied(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun showDeniedForLocation() {
        notifyUser(binding.root, getString(R.string.location_permission_denied))
    }

    @OnNeverAskAgain(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    fun neverAskForLocation() {
        notifyUser(binding.root, getString(R.string.location_permission_never_ask))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_BT) {
            if (resultCode == Activity.RESULT_OK) {
                //populate devices
                getBluetoothDevicesWithPermissionCheck()
            } else {
                Handler().postDelayed({
                    showTurnBlueToothPrompt()
                }, 500)

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }


    private fun showPairedDevicesDialog() {

        val arrayAdapter = BluetoothArrayAdapter(
            this@PrinterSettingsActivity,
            bluetoothManager.provideBondedDeviceList()
        )

        val builderSingle = AlertDialog.Builder(this@PrinterSettingsActivity)
        builderSingle.setTitle(getString(R.string.paired_devices))
        builderSingle.setAdapter(arrayAdapter) { dialog, position ->
            val device = arrayAdapter.getItem(position)
            currentPrinterAddress = device?.address
            binding.AdminPrinterSettingsEdtPrinterValue.setText(device?.name)
            dialog.dismiss()
        }
        builderSingle.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        builderSingle.setPositiveButton(getString(R.string.pair_new_device)) { dialog, _ ->
            findNewDevices()
            dialog.dismiss()
        }
        builderSingle.show()
    }

    private fun findNewDevices() {
        val discoveringDevicesDialog =
            DiscoveringDevicesDialog.newInstance(object : DiscoveringDevicesDialog.DiscoveringListener {
                override fun onCancel() {
                    bluetoothManager.stopDiscovery()
                }
            })
        bluetoothManager.startDiscovery()
        bluetoothManager.listener = object : BluetoothManager.Listener {
            override fun onDiscoveryComplete() {
                discoveringDevicesDialog.dismiss()
                if (bluetoothManager.bluetoothDeviceList.isEmpty()) {
                    showNoDevicesAvailableDialog()
                } else {
                    showAvailableDevicesDialog()
                }
            }
        }
        discoveringDevicesDialog.show(supportFragmentManager, discoveringDevicesDialog.javaClass.name)
    }

    private fun showNoDevicesAvailableDialog() {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.search_complete),
            message = getString(R.string.no_devices_found),
            buttonText = getString(android.R.string.ok)
        )
        dialogFragment.isCancelable = false
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }


    private fun showAvailableDevicesDialog() {
        val devicesList = mutableListOf<BluetoothDevice>()
        devicesList.addAll(bluetoothManager.bluetoothDeviceList.values)

        val arrayAdapter = BluetoothArrayAdapter(
            this@PrinterSettingsActivity,
            devicesList
        )

        val builderSingle = AlertDialog.Builder(this@PrinterSettingsActivity)
        builderSingle.setTitle(getString(R.string.pair_new_device))
        builderSingle.setAdapter(arrayAdapter) { dialog, position ->
            val device = arrayAdapter.getItem(position)

            currentPrinterAddress = device?.address
            binding.AdminPrinterSettingsEdtPrinterValue.setText(device?.name)
            dialog.dismiss()
        }
        builderSingle.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
        builderSingle.show()
    }

    private fun showTurnBlueToothPrompt() {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.alert),
            message = getString(R.string.select_printer_error_message),
            buttonText = getString(android.R.string.ok),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
                    attemptToTurnBluetoothOn()
                }
            }, hasNegativeButton = true,
            negativeButtonText = getString(android.R.string.cancel)

        )
        dialogFragment.isCancelable = false
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }


    private fun saveAndClose() {
        viewModel.setSettings(
            currentPrinterAddress, binding.AdminPrinterSettingsEdtPrinterValue.text.toString(),
            binding.AdminPrinterSettingsEdtLabelCpclData.text.toString()
        )
    }


    override fun onStart() {
        super.onStart()
        bluetoothManager.registerReceiver()
    }

    override fun onStop() {
        super.onStop()
        bluetoothManager.unRegisterReceiver()
        bluetoothManager.stopDiscovery()
    }

    override fun onBackPressed() {
        saveAndClose()
    }


    override fun getLayoutResourceId() = R.layout.activity_printer_settings

    override fun getBindingVariable() = BR.viewModel

    override fun getViewModelClass() = PrinterSettingsViewModel::class.java

    companion object {
        const val RC_BT = 232
    }


}
