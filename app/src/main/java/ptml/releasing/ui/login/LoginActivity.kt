package ptml.releasing.ui.login

import ptml.releasing.ui.base.BaseFragmentActivity
import javax.inject.Inject

class LoginActivity : BaseFragmentActivity<LoginFragment>() {

    @Inject
    lateinit var loginFragment: LoginFragment

    override fun getFragment(): LoginFragment {
        showUpEnabled(true)
        return loginFragment
    }
}