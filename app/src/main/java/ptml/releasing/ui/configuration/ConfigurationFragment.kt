package ptml.releasing.ui.configuration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ptml.releasing.R
import ptml.releasing.databinding.FragmentConfigureProfileBinding
import ptml.releasing.ui.base.BaseFragment
import javax.inject.Inject

class ConfigurationFragment @Inject constructor():BaseFragment() {

    lateinit var binding: FragmentConfigureProfileBinding
    lateinit var configurationViewModel: ConfigurationViewModel

    @Inject
    lateinit var viewModeFactory: ViewModelProvider.Factory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configurationViewModel = ViewModelProviders.of(this, viewModeFactory)
            .get(ConfigurationViewModel::class.java)

    }
}