package ptml.releasing.home.view

import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseFragment
import ptml.releasing.databinding.FragmentHomeBinding
import ptml.releasing.home.viewmodel.HomeViewModel

class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>() {
    override fun getBindingVariable() = BR._all

    override fun getLayoutResourceId() = R.layout.fragment_home

    override fun getViewModelClass() = HomeViewModel::class.java

}