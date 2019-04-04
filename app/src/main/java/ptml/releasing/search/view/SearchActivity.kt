package ptml.releasing.search.view

import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import me.seebrock3r.elevationtester.TweakableOutlineProvider
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.databinding.ActivitySearchBinding
import ptml.releasing.search.viewmodel.SearchViewModel
import java.util.*

class SearchActivity : BaseActivity<SearchViewModel, ActivitySearchBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.goBack.observe(this, Observer {
            if(it){
                onBackPressed()
            }
        })

        viewModel.savedConfiguration.observe(this, Observer {
            updateTop(it)
        })

        viewModel.getSavedConfig()

        showUpEnabled(true)

        val cornerRadius = binding.includeHomeTop.btnCargoType.cornerRadius
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.includeHomeTop.btnCargoType.outlineProvider = TweakableOutlineProvider(cornerRadius)
            binding.includeHomeTop.btnTerminal.outlineProvider = TweakableOutlineProvider(cornerRadius)
            binding.includeHomeTop.btnOperationStep.outlineProvider = TweakableOutlineProvider(cornerRadius)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getSavedConfig()
    }



    private fun updateTop(it: Configuration) {
        binding.includeHomeTop.btnCargoType.text = it.cargoType.value
        binding.includeHomeTop.btnOperationStep.text = it.operationStep.value
        binding.includeHomeTop.btnTerminal.text = it.terminal.value

        if(it.cargoType.value?.toLowerCase(Locale.US) == CargoType.VEHICLE){
            binding.includeHomeTop.btnCargoType.icon = ContextCompat.getDrawable(themedContext, R.drawable.ic_car)
        }else{
            binding.includeHomeTop.btnCargoType.icon = ContextCompat.getDrawable(themedContext, R.drawable.ic_container)
        }
    }

    override fun getLayoutResourceId()  = R.layout.activity_search

    override fun getBindingVariable()  = BR.viewModel

    override fun getViewModelClass() = SearchViewModel::class.java
}