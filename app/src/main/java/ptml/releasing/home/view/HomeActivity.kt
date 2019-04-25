package ptml.releasing.home.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.ReleasingApplication
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoDialog
import ptml.releasing.login.view.LoginActivity
import ptml.releasing.cargo_search.view.SearchActivity
import ptml.releasing.configuration.models.CargoType
import ptml.releasing.configuration.models.Configuration
import ptml.releasing.databinding.ActivityHomeBinding
import ptml.releasing.home.viewmodel.HomeViewModel
import java.util.*


class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getSavedConfig()
        viewModel.isConfigured.observe(this, Observer {
            binding.appBarHome.content.tvConfigMessageContainer.visibility =
                if (it) View.GONE else View.VISIBLE //hide or show the not configured message
//            binding.appBarHome.content.includeHome.root.visibility = if (it) View.VISIBLE else View.GONE //hide or show the home buttons
        })



        viewModel.openSearch.observe(this, Observer {
            if (it) {
                startNewActivity(SearchActivity::class.java)
            } else {
                showConfigurationErrorDialog()
            }
        })

    /*    viewModel.savedConfiguration.observe(this, Observer {
            updateTop(it)
        })*/




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
        binding.appBarHome.content.includeHomeBottom.btnConfiguration.setOnClickListener {
            viewModel.openConfiguration()
        }

        binding.appBarHome.content.includeHomeBottom.btnSearch.setOnClickListener {
            viewModel.openSearch()
        }


    }

    override fun initBeforeView() {
        super.initBeforeView()
        if (viewModel.isConfigured()) {
            startNewActivity(SearchActivity::class.java)
        } else {
            startNewActivity(LoginActivity::class.java)
        }
    }

  /*  private fun updateTop(it: Configuration) {
        binding.appBarHome.content.includeHome.tvCargoFooter.text = it.cargoType.value
        binding.appBarHome.content.includeHome.tvOperationStepHeader.text = it.operationStep.value
        binding.appBarHome.content.includeHome.tvTerminalHeader.text = it.terminal.value

        if (it.cargoType.value?.toLowerCase(Locale.US) == CargoType.VEHICLE) {
            binding.appBarHome.content.includeHome.imgCargoType.setImageDrawable(
                ContextCompat.getDrawable(themedContext, R.drawable.ic_car)
            )
        } else {
            binding.appBarHome.content.includeHome.imgCargoType.setImageDrawable(
                ContextCompat.getDrawable(themedContext, R.drawable.ic_container)
            )
        }
    }
*/
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

        val dialogFragment = InfoDialog.newInstance(
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

    private val navigationListener =
        NavigationView.OnNavigationItemSelectedListener { item ->
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
            true
        }

    private fun showImeiDialog() {
        val dialogFragment = InfoDialog.newInstance(
            title = getString(R.string.device_IMEI),
            message = (application as ReleasingApplication).provideImei(),
            buttonText = getString(R.string.dismiss)
        )
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.name)
    }

    override fun getLayoutResourceId() = R.layout.activity_home

    override fun getBindingVariable() = BR._all

    override fun getViewModelClass() = HomeViewModel::class.java
}