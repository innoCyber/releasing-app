package ptml.releasing.search.view

import android.os.Build
import android.os.Bundle
import android.view.View
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



    }

    override fun onResume() {
        super.onResume()
        viewModel.getSavedConfig()
    }



    private fun updateTop(it: Configuration) {
        binding.includeHome.tvCargoFooter.text = it.cargoType.value
        binding.includeHome.tvOperationStepFooter.text = it.operationStep.value
        binding.includeHome.tvTerminalFooter.text = it.terminal.value

        if(it.cargoType.value?.toLowerCase(Locale.US) == CargoType.VEHICLE){
            binding.includeHome.imgCargoType.setImageDrawable(ContextCompat.getDrawable(themedContext, R.drawable.ic_car))
        }else{
            binding.includeHome.imgCargoType.setImageDrawable(ContextCompat.getDrawable(themedContext, R.drawable.ic_container))
        }

        if(it.cameraEnabled){
            showCameraScan()
        }else{
            hideCameraScan()
        }

    }

    private fun showCameraScan(){
        binding.includeSearch.tvScan.visibility = View.VISIBLE
        binding.includeSearch.dividerL.visibility = View.VISIBLE
        binding.includeSearch.dividerR.visibility = View.VISIBLE
        binding.includeSearch.imgQrCode.visibility = View.VISIBLE
        binding.includeSearch.imgQrCodeLayout.visibility = View.VISIBLE

    }

    private fun hideCameraScan(){
        binding.includeSearch.tvScan.visibility = View.GONE
        binding.includeSearch.dividerL.visibility = View.GONE
        binding.includeSearch.dividerR.visibility = View.GONE
        binding.includeSearch.imgQrCode.visibility = View.GONE
        binding.includeSearch.imgQrCodeLayout.visibility = View.GONE
    }

    override fun getLayoutResourceId()  = R.layout.activity_search

    override fun getBindingVariable()  = BR.viewModel

    override fun getViewModelClass() = SearchViewModel::class.java
}