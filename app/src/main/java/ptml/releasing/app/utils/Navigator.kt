package ptml.releasing.app.utils

import android.content.Intent
import android.os.Bundle
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.cargo_search.view.SearchActivity
import ptml.releasing.configuration.view.ConfigActivity
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

    fun goToConfiguration(baseActivity: BaseActivity<*, *>) {
        baseActivity.startActivity(Intent(baseActivity, ConfigActivity::class.java))
        baseActivity.finish()
    }

    fun goToLogin(baseActivity: BaseActivity<*, *>) {
        baseActivity.startActivity(Intent(baseActivity, ptml.releasing.login.view.LoginActivity::class.java))
        baseActivity.finishAffinity()
    }

    fun goToSearchWithBundle(baseActivity: BaseActivity<*, *>) {
        val send = Intent(baseActivity, SearchActivity::class.java)
        val b = Bundle()
        b.putBoolean("fromSavedConfigButton", true)
        send.putExtras(b)
        baseActivity.startActivity(send)
    }

    fun goToReset(baseActivity: BaseActivity<*, *>) {
        baseActivity.startActivity(Intent(baseActivity, ResetPasswordActivity::class.java))
    }

}