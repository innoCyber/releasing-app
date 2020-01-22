package ptml.releasing.app.utils

import android.content.Intent
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.cargo_search.view.SearchActivity
import ptml.releasing.login.view.LoginActivity
import ptml.releasing.resetpassword.view.ResetPasswordActivity
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */
class Navigator @Inject constructor() {

    fun goToSearch(baseActivity: BaseActivity<*, *>) {
        baseActivity.startActivity(Intent(baseActivity, SearchActivity::class.java))
        baseActivity.finish()
    }

    fun goToLogin(baseActivity: BaseActivity<*, *>) {
        baseActivity.startActivity(Intent(baseActivity, LoginActivity::class.java))
        baseActivity.finishAffinity()
    }

    fun goToReset(baseActivity: BaseActivity<*, *>) {
        baseActivity.startActivity(Intent(baseActivity, ResetPasswordActivity::class.java))
    }

}