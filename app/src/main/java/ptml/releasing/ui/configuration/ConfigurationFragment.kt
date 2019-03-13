package ptml.releasing.ui.configuration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ptml.releasing.R
import ptml.releasing.databinding.FragmentConfigureProfileBinding
import ptml.releasing.db.models.config.response.ConfigurationResponse
import ptml.releasing.ui.base.BaseFragment
import ptml.releasing.ui.configuration.adapter.ConfigurationSpinnerAdapter
import ptml.releasing.utils.ErrorHandler
import ptml.releasing.utils.NetworkState
import ptml.releasing.utils.NotificationUtils
import ptml.releasing.utils.Status
import javax.inject.Inject

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

        configurationViewModel = ViewModelProviders.of(this, viewModeFactory)
            .get(ConfigurationViewModel::class.java)


        configurationViewModel.configData.observe(this, Observer {

        })


        configurationViewModel.network.observe(this, Observer {
            if (it == NetworkState.LOADING) {
                showLoading(binding.includeProgress.root, binding.includeProgress.tvMessage, R.string.logining_in)
            } else {
                hideLoading(binding.includeProgress.root)
            }

            if (it?.status == Status.FAILED) {
                val error = ErrorHandler(context!!).getErrorMessage(it.throwable)
                NotificationUtils.notifyUser(binding.root, error)
            }
        })


    }

    private fun getUpSpinners(response: ConfigurationResponse){
        val cargoAdapter = ConfigurationSpinnerAdapter(context, R.id.tv_category, response.cargoTypeList)
        val operationStepAdapter = ConfigurationSpinnerAdapter(context, R.id.tv_category, response.operationStepList)
        val terminalAdapter = ConfigurationSpinnerAdapter(context, R.id.tv_category, response.terminalList)


    }
}