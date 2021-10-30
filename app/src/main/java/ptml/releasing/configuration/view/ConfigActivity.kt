package ptml.releasing.configuration.view

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.include_configure_profile_top_controls.*
import kotlinx.android.synthetic.main.include_configure_profile_top_controls.select_voyage_spinner
import kotlinx.android.synthetic.main.select_voyage_layout.*
import permissions.dispatcher.*
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.EditTextDialog
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.app.exception.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.Status
import ptml.releasing.configuration.models.*
import ptml.releasing.configuration.view.adapter.ConfigSpinnerAdapter
import ptml.releasing.configuration.viewmodel.ConfigViewModel
import ptml.releasing.databinding.ActivityConfigBinding
import timber.log.Timber
import java.util.*


@RuntimePermissions
class ConfigActivity : BaseActivity<ConfigViewModel, ActivityConfigBinding>() {

    private var cargoTypes: List<CargoType>? = null

    private var operationStepAdapter: ConfigSpinnerAdapter<ReleasingOperationStep>? = null

    private var shippingLineAdapter: ConfigSpinnerAdapter<ShippingLine>? = null

    private var voyageAdapter: ConfigSpinnerAdapter<ReleasingVoyage>? = null

    private val errorHandler by lazy {
        ErrorHandler(this)
    }

    var isGrimaldiContainer: Boolean = false
    var isLoadOnBoard: Boolean = false
    var grimaldiContainerVoyageID: Int = 0


    override fun onBackPressed() {
        super.onBackPressed()
        navigator.goToSearch(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)
        initErrorDrawable(binding.includeError.imgError)
        binding.bottom.btnDeleteLayout.visibility = View.GONE
        binding.top.root.visibility = View.INVISIBLE
        binding.bottom.root.visibility = View.INVISIBLE
        binding.scrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            binding.swipeRefreshLayout.isEnabled = scrollY == 0
        }

        viewModel.cargoTypes.observe(this, Observer { cargoTypes ->
            viewModel.configuration.observe(this, Observer { selectedItem ->
                setUpCargoType(cargoTypes, selectedItem)
                binding.swipeRefreshLayout.isRefreshing = false
            })

        })

        viewModel.getOperationStepList().observe(this, Observer { operationSteps ->
            viewModel.configuration.observe(this, Observer { selectedOperation ->
                setUpOperationStep(operationSteps, selectedOperation)
            })

        })

        viewModel.getShippingLine().observe(this, Observer { shippingLines ->
            viewModel.configuration.observe(this, Observer { configuration ->
                setUpShippingLine(shippingLines, configuration)
            })

        })



        viewModel.getVoyageList().observe(this, Observer { voyages ->
            viewModel.configuration.observe(this, Observer { _ ->
                setUpVoyages(voyages)
            })

        })


        viewModel.getNetworkState().observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (it == NetworkState.LOADING) {
                    binding.swipeRefreshLayout.isRefreshing = true
                    binding.top.root.visibility = View.GONE
                    binding.bottom.root.visibility = View.GONE
//                    showLoading(
//                        binding.includeProgress.root,
//                        binding.includeProgress.tvMessage,
//                        R.string.getting_configuration
//                    )
                } else if (it == NetworkState.LOADED) {
                    binding.swipeRefreshLayout.isRefreshing = false
                  //  hideLoading(binding.includeProgress.root)
                    binding.top.root.visibility = View.VISIBLE
                    binding.bottom.root.visibility = View.VISIBLE
                }

                //disable the fab when data is loading
                binding.fab.isEnabled = it != NetworkState.LOADING

                if (it.status == Status.FAILED) {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.top.root.visibility = View.GONE
                    binding.bottom.root.visibility = View.GONE

                    val error = errorHandler.getErrorMessage(it.throwable)
                    binding.includeError.btnReloadLayout.setOnClickListener {
                        if (errorHandler.isImeiError(error)) {
                            showEnterImeiDialog()
                        } else {
                            getConfigWithPermissionCheck()
                           // refreshConfigWithPermissionCheck()
                        }
                    }
                    binding.includeError.btnReload.text =
                        if (errorHandler.isImeiError(error)) getString(R.string.enter_imei) else getString(
                            R.string.reload
                        )

                    showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
                    hideLoading(binding.includeProgress.root)
                } else {
                    hideLoading(binding.includeError.root)
                }
            }
        })


        viewModel.getSavedSuccess().observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    notifyUser(getString(R.string.config_saved_success))
//                    setResult(Activity.RESULT_OK)
//                    finish()
                    navigator.goToSearchWithBundle(this,isGrimaldiContainer,isLoadOnBoard, grimaldiContainerVoyageID)
                }
            }
        })

        viewModel.configuration.observe(this, Observer {
            val terminal = it?.terminal


            if(terminal == null){
                binding.top.terminalText.setTextColor(Color.RED)
            }
            binding.top.terminalText.text = terminal?.value?: "No terminal assigned"
            binding.top.terminalText.tag = terminal?: ReleasingTerminal(categoryTypeId = -1)
        })


        binding.bottom.btnProfiles.setOnClickListener {
            setConfigWithPermissionCheck()
        }

        binding.includeError.btnReloadLayout.setOnClickListener {
            getConfigWithPermissionCheck()
        }

//        binding.fab.setOnClickListener {
//            refreshConfigWithPermissionCheck()
//        }
        binding.swipeRefreshLayout.setOnRefreshListener {
           // refreshConfigWithPermissionCheck()
            getConfigWithPermissionCheck()
        }

        binding.fab.setOnClickListener {
            getConfigWithPermissionCheck()
        }

    }



    override fun onImeiGotten(imei: String?) {
        super.onImeiGotten(imei)

        binding.swipeRefreshLayout.isRefreshing = true
        viewModel.refreshConfiguration(imei ?: "")
        viewModel.getConfig(imei ?: "")
    }

    private fun showEnterImeiDialog() {
        val imeiNumber = imei
        val dialog =
            EditTextDialog.newInstance(
                imeiNumber,
                object : EditTextDialog.EditTextDialogListener {
                    override fun onSave(value: String) {
                        imei = value
                        viewModel.updateImei(value)
                        refreshConfigWithPermissionCheck()
                    }
                },
                getString(R.string.enter_imei_dialog_title),
                getString(R.string.enter_imei_dialog_hint),
                false
            )
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, dialog.javaClass.name)
    }

    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getConfig() {
        viewModel.getConfig(imei ?: "")
    }

    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun refreshConfig() {
        viewModel.refreshConfiguration(imei ?: "")
    }

    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun setConfig() {
        val operationStep =
            binding.top.selectOperationSpinner.selectedItem as ReleasingOperationStep?
        if ((binding.top.terminalText.tag as ReleasingTerminal?)?.categoryTypeId == -1) {
            val dialogFragment = InfoDialog.newInstance(
                title = getString(R.string.no_terminal_assigned_header),
                message = getString(R.string.no_terminal_assigned_text),
                buttonText = getString(android.R.string.ok),
                listener = object : InfoDialog.InfoListener {
                    override fun onConfirm() {}
                })
            dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name) }

        else if (operationStep?.value==" " || operationStep?.value.isNullOrBlank()){
                showAlertDialog("No Operation step selected","Select one to proceed")
            }
        else{
                setConfig(operationStep)
        }

    }

    private fun setConfig(operationStep: ReleasingOperationStep?) {
        val selectedVoyage = binding.top.selectVoyageSpinner.selectedItem as ReleasingVoyage
        val shippingLine = binding.top.selectShippingLineSpinner.selectedItem as ShippingLine?

        if (operationStep?.id == 20 && shippingLine?.value?.toLowerCase(Locale.ROOT)!!.contains("grimaldi")){
            isGrimaldiContainer = true
            isLoadOnBoard = true
            grimaldiContainerVoyageID = selectedVoyage.id ?: 0
        }

        if (operationStep?.id == 20 && (selectedVoyage.value == " " || shippingLine?.value == " " || selectedVoyage.value.isNullOrBlank() || shippingLine?.value.isNullOrBlank())) {
            showAlertDialog("No values selected for Shipping Line/ Voyage","Select one to proceed")
        }

        else if (operationStep?.id == 29 && (shippingLine?.value == " " || shippingLine?.value.isNullOrBlank())) {
            showAlertDialog("No values selected for Shipping Line","Select one to proceed")
        }

        else if(selectedVoyage.id == -1 && operationStep?.id == 20){
           showErrorDialog("No voyage is available for this operation, " +
                   "please refer to helpdesk.eramp@ptml-ng.com.")
        }else {

            viewModel.setConfig(
                binding.top.terminalText.tag as ReleasingTerminal?,
                operationStep,
                cargoTypes?.get(binding.top.tab.selectedTabPosition),
                binding.top.selectShippingLineSpinner.selectedItem as ShippingLine,
                selectedVoyage,
               false,// binding.top.cameraSwitch.isChecked,
                imei ?: "",
                selectedVoyage.id

            )
        }
    }



    @OnShowRationale(android.Manifest.permission.READ_PHONE_STATE)
    fun showInitRecognizerRationale(request: PermissionRequest) {
        val dialogFragment = InfoDialog.newInstance(
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }


    private fun setUpCargoType(cargoTypes: List<CargoType>, selected: Configuration) {
        try {
            this.cargoTypes = cargoTypes
            binding.top.tab.removeAllTabs()
            cargoTypes.forEach {
                binding.top.tab.addTab(binding.top.tab.newTab().apply {
                    text = it.value
                    tag = it
                })
            }

            //binding.top.tab.removeTabAt(0)
            binding.top.tab.getTabAt(0)?.view?.visibility = View.GONE

            binding.top.tab.selectTab( binding.top.tab.getTabAt(1) ,true)

            binding.top.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewModel.cargoTypeSelected(cargoTypes[binding.top.tab.selectedTabPosition] ?: CargoType())

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })


        } catch (e: Exception) {
            Timber.e(e)
            showLoading(
                binding.includeError.root,
                binding.includeError.tvMessage,
                R.string.error_occurred
            )
        }
    }


    private fun setUpOperationStep(
        operationStepList: MutableList<ReleasingOperationStep>,
        selected: Configuration
    ) {

        binding.top.selectOperationSpinner.run {
            operationStepAdapter =
                ConfigSpinnerAdapter(applicationContext, R.id.tv_category, operationStepList)
            adapter = operationStepAdapter
            val selectedItem = operationStepList.indexOf(selected.operationStep)
               // setSelection(if (selectedItem == -1) 0 else selectedItem)
            onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        when (operationStepList[position].id) {
                            29 -> {
                                binding.top.tvSelectShippingLine.visibility = View.VISIBLE
                                binding.top.selectShippingLineSpinner.visibility = View.VISIBLE
                                binding.top.tvSelectVoyage.visibility = View.GONE
                                binding.top.selectVoyageSpinner.visibility = View.GONE
                            }
                            20 -> {
                                binding.top.tvSelectShippingLine.visibility = View.VISIBLE
                                binding.top.selectShippingLineSpinner.visibility = View.VISIBLE
                                binding.top.tvSelectVoyage.visibility = View.VISIBLE
                                binding.top.selectVoyageSpinner.visibility = View.VISIBLE
                            }
                            else -> {
                                binding.top.tvSelectShippingLine.visibility = View.GONE
                                binding.top.selectShippingLineSpinner.visibility = View.GONE
                                binding.top.tvSelectVoyage.visibility = View.GONE
                                binding.top.selectVoyageSpinner.visibility = View.GONE
                            }
                        }

                    }
                }
        }

    }

    private fun setUpShippingLine(shippingLines: List<ShippingLine>, selected: Configuration) {
        binding.top.selectShippingLineSpinner.run {
            shippingLineAdapter =
                ConfigSpinnerAdapter(applicationContext, R.id.tv_category, shippingLines)
            adapter = shippingLineAdapter
            val selectedItem = shippingLines.indexOf(selected.shippingLine)
            //setSelection(if (selectedItem == -1) 0 else selectedItem)
            setSelection(if (selectedItem == -1) 0 else 0)
        }
    }

    private fun setUpVoyages(voyages: List<ReleasingVoyage>) {
        binding.top.selectVoyageSpinner.run {
            voyageAdapter =
                ConfigSpinnerAdapter(applicationContext,
                    R.id.tv_category,
                    if (voyages.isEmpty()) listOf(
                        ReleasingVoyage().apply { id = -1; value = "No voyages" }) else voyages
                )
            adapter = voyageAdapter
        }


    }

    private fun showAlertDialog(title: String, message: String) {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@ConfigActivity)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setIcon(R.drawable.ic_baseline_warning_24)
        alertDialog.setNegativeButton(
            "Ok"
        ) { _, _ -> }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }



    override fun getViewModelClass() = ConfigViewModel::class.java


    override fun getBindingVariable() = BR.viewModel

    override fun getLayoutResourceId() = R.layout.activity_config

}