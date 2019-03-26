package ptml.releasing.home.view

import android.os.Bundle
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseFragmentActivity
import ptml.releasing.databinding.ActivityHomeBinding

class HomeActivity : BaseFragmentActivity< ActivityHomeBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    override fun getLayoutResourceId() = R.layout.activity_home

    override fun getBindingVariable() = BR._all
}