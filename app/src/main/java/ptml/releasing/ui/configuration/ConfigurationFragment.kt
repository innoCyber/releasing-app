package ptml.releasing.ui.configuration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import permissions.dispatcher.*
import ptml.releasing.R
import ptml.releasing.app.App
import ptml.releasing.databinding.FragmentConfigureProfileBinding
import ptml.releasing.db.models.config.ConfigurationResponse
import ptml.releasing.ui.base.BaseFragment
import ptml.releasing.ui.configuration.adapter.ConfigurationSpinnerAdapter
import ptml.releasing.ui.dialogs.InfoConfirmDialog
import ptml.releasing.utils.ErrorHandler
import ptml.releasing.utils.NetworkState
import ptml.releasing.utils.Status
import javax.inject.Inject

@RuntimePermissions
class ConfigurationFragment @Inject constructor():BaseFragment() {

    lateinit var binding: FragmentConfigureProfileBinding
    lateinit var configurationViewModel: ConfigurationViewModel

    @Inject
    lateinit var viewModeFactory: ViewModelProvider.Factory


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_configure_profile, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bottom.btnConfigure.visibility = View.GONE



        configurationViewModel = ViewModelProviders.of(this, viewModeFactory)
            .get(ConfigurationViewModel::class.java)


        configurationViewModel.configData.observe(this, Observer {

            if(it.isSuccess){
                setUpSpinners(it)
            }else{
                showDialog(it?.message)
            }
        })


        configurationViewModel.network.observe(this, Observer {
            if (it == NetworkState.LOADING) {
                showLoading(binding.includeProgress.root, binding.includeProgress.tvMessage, R.string.getting_configuration)
            } else {
                hideLoading(binding.includeProgress.root)
            }

            if (it?.status == Status.FAILED) {
                val error = ErrorHandler(context!!).getErrorMessage(it.throwable)
                showErrorDialog(error)
            }
        })


        //begin the request
        getConfigWithPermissionCheck()
    }


    @NeedsPermission(android.Manifest.permission.READ_PHONE_STATE)
    fun getConfig() {
        configurationViewModel.getConfig((activity?.application as App).provideImei())
    }

    @OnShowRationale(android.Manifest.permission.READ_PHONE_STATE)
    fun showInitRecognizerRationale(request: PermissionRequest) {
        InfoConfirmDialog.showDialog(
            context,
            R.string.allow_permission,
            R.string.allow_phone_state_permission_msg,
            R.drawable.ic_info_white, ({ request.proceed() })
        )
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


    private fun setUpSpinners(response: ConfigurationResponse){
        val cargoAdapter = ConfigurationSpinnerAdapter(context, R.id.tv_category, response.cargoTypeList)
        val operationStepAdapter = ConfigurationSpinnerAdapter(context, R.id.tv_category, response.operationStepList)
        val terminalAdapter = ConfigurationSpinnerAdapter(context, R.id.tv_category, response.terminalList)

        binding.top.selectCargoSpinner.adapter = cargoAdapter
        binding.top.selectOperationSpinner.adapter = operationStepAdapter
        binding.top.selectTerminalSpinner.adapter = terminalAdapter

    }

    private fun showErrorDialog(message:String){
        InfoConfirmDialog.showDialog(context, getString(R.string.error), message, getString(R.string.retry), R.drawable.ic_error) {
            getConfigWithPermissionCheck()
        }
    }


}