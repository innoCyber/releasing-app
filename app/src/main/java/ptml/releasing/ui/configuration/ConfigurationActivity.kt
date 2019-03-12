package ptml.releasing.ui.configuration

import ptml.releasing.ui.base.BaseFragmentActivity
import javax.inject.Inject

class ConfigurationActivity : BaseFragmentActivity<ConfigurationFragment>() {

    @Inject
    lateinit var configurationFragment: ConfigurationFragment
    override fun getFragment(): ConfigurationFragment {
        showUpEnabled(true)
        return configurationFragment
    }
}