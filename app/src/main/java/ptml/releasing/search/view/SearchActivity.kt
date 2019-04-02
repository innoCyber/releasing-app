package ptml.releasing.search.view

import android.os.Bundle
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.databinding.ActivitySearchBinding
import ptml.releasing.search.viewmodel.SearchViewModel

class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)
    }

    override fun getLayoutResourceId()  = R.layout.activity_search

    override fun getBindingVariable()  = BR.viewModel

    override fun getViewModelClass() = SearchViewModel::class.java
}