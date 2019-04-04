package ptml.releasing.home.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.admin_config.view.AdminConfigActivity
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.auth.view.LoginActivity
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.databinding.ActivityHomeBinding
import ptml.releasing.home.viewmodel.HomeViewModel
import ptml.releasing.search.view.SearchActivity
import java.util.*


class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.isConfigured.observe(this, Observer {
            binding.appBarHome.content.tvConfigMessageContainer.visibility = if (it) View.GONE else View.VISIBLE //hide or show the not configured message
            binding.appBarHome.content.includeHomeTop.root.visibility = if (it) View.VISIBLE else View.GONE //hide or show the home buttons

        })

        viewModel.firstTimeLogin.observe(this, Observer {
            if(it){
                startNewActivity(LoginActivity::class.java)
            }
        })

        viewModel.firstTimeFindCargo.observe(this, Observer {
             if(it){
                 startNewActivity(SearchActivity::class.java)
             }
        })

        viewModel.getSavedConfig()

        viewModel.openConfiguration.observe(this, Observer {
            startNewActivity(AdminConfigActivity::class.java)
        })
        viewModel.openSearch.observe(this, Observer {
            if (it) {
                startNewActivity(SearchActivity::class.java)
            } else {
                showConfigurationErrorDialog()
            }
        })

        viewModel.savedConfiguration.observe(this, Observer {
            updateTop(it)
        })




        setSupportActionBar(binding.appBarHome.toolbar)
        val toggle = ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.appBarHome.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(navigationListener)
        binding.appBarHome.content.includeHomeBottom.btnConfigurationLayout.setOnClickListener {
            viewModel.openConfiguration()
        }

        binding.appBarHome.content.includeHomeBottom.btnSearchLayout.setOnClickListener {
            viewModel.openSearch()
        }



    }

    private fun updateTop(it: Configuration) {
        binding.appBarHome.content.includeHomeTop.btnCargoType.text = it.cargoType.value
        binding.appBarHome.content.includeHomeTop.btnOperationStep.text = it.operationStep.value
        binding.appBarHome.content.includeHomeTop.btnTerminal.text = it.terminal.value

        if(it.cargoType.value?.toLowerCase(Locale.US) == CargoType.VEHICLE){
            binding.appBarHome.content.includeHomeTop.btnCargoType.icon = ContextCompat.getDrawable(themedContext, R.drawable.ic_car)
        }else{
            binding.appBarHome.content.includeHomeTop.btnCargoType.icon = ContextCompat.getDrawable(themedContext, R.drawable.ic_container)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSavedConfig()
    }

    private fun showConfigurationErrorDialog() {
   /*     InfoConfirmDialog.showDialog(context = this,
                title = getString(R.string.config_error),
                message = getString(R.string.config_error_message),
                topIcon = R.drawable.ic_error, listener = object : InfoConfirmDialog.InfoListener {
            override fun onConfirm() {
//                    viewModel.openConfiguration()
            }
        })*/

        val dialogFragment =  InfoDialog.newInstance(
            title = getString(R.string.config_error),
            message = getString(R.string.config_error_message),
            buttonText = getString(android.R.string.ok),
            listener = object : InfoDialog.InfoListener {
                override fun onConfirm() {
//                    viewModel.openConfiguration()
                }
            })
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    private val navigationListener = object : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.nav_preferences -> {
                    //TODO handle nav
                }

                R.id.nav_about -> {
                    //TODO handle nav
                    showImeiDialog()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
    }

    private fun showImeiDialog() {
        val dialogFragment =  InfoDialog.newInstance(
            title = getString(R.string.device_IMEI),
            message = (application as ReleasingApplication).provideImei(),
            buttonText = getString(R.string.dismiss))
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    override fun getLayoutResourceId() = R.layout.activity_home

    override fun getBindingVariable() = BR._all

    override fun getViewModelClass() = HomeViewModel::class.java
}