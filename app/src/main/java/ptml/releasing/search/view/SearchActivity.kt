package ptml.releasing.search.view

import android.os.Bundle
import androidx.lifecycle.Observer
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.admin_configuration.models.Configuration
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.databinding.ActivitySearchBinding
import ptml.releasing.search.viewmodel.SearchViewModel

class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)

        viewModel.goBack.observe(this, Observer {
            if(it){
                onBackPressed()
            }
        })

        viewModel.savedConfiguration.observe(this, Observer {
            updateTop(it)
        })

        viewModel.getSavedConfig()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSavedConfig()
    }



    private fun updateTop(it: Configuration) {
        binding.includeHomeTop.btnCargoType.text = it.cargoType.value
        binding.includeHomeTop.btnOperationStep.text = it.operationStep.value
        binding.includeHomeTop.btnTerminal.text = it.terminal.value
    }

    override fun getLayoutResourceId()  = R.layout.activity_search

    override fun getBindingVariable()  = BR.viewModel

    override fun getViewModelClass() = SearchViewModel::class.java
}