package ptml.releasing.home.view

import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.databinding.ActivityHomeBinding
import ptml.releasing.home.viewmodel.HomeViewModel

class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {


    override fun getLayoutResourceId() = R.layout.activity_home

    override fun getBindingVariable() = BR._all

    override fun getViewModelClass() = HomeViewModel::class.java
}