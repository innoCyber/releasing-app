package ptml.releasing.home.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.dialogs.InfoConfirmDialog
import ptml.releasing.auth.view.LoginActivity
import ptml.releasing.damages.view.DamageActivity
import ptml.releasing.databinding.ActivityHomeBinding
import ptml.releasing.home.viewmodel.HomeViewModel
import ptml.releasing.search.view.SearchActivity


class HomeActivity : BaseActivity<HomeViewModel, ActivityHomeBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        binding.appBarHome.content.includeHomeBottom.btnDownloadLayout.setOnClickListener {
            viewModel.openDownloadDamages()
        }

        binding.appBarHome.content.includeHomeBottom.btnSearchLayout.setOnClickListener {
            viewModel.openSearch()
        }

        viewModel.openConfiguration.observe(this, Observer {
            startNewActivity(LoginActivity::class.java)
        })
        viewModel.openSearch.observe(this, Observer {
            if (it) {
                startNewActivity(SearchActivity::class.java)
            } else {
                showConfigurationErrorDialog()
            }
        })

        viewModel.openDownloadDamages.observe(this, Observer {
            if (it) {
                startNewActivity(DamageActivity::class.java)
            } else {
                showConfigurationErrorDialog()
            }

        })


    }

    private fun showConfigurationErrorDialog() {
        InfoConfirmDialog.showDialog(context = this,
            title = getString(R.string.config_error),
            message = getString(R.string.config_error_message),
            topIcon = R.drawable.ic_error, listener = object : InfoConfirmDialog.InfoListener {
                override fun onConfirm() {
                    viewModel.openConfiguration()
                }
            })
    }

    private val navigationListener = object : NavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.nav_preferences -> {
                    //TODO handle nav
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }
    }

    override fun getLayoutResourceId() = R.layout.activity_home

    override fun getBindingVariable() = BR._all

    override fun getViewModelClass() = HomeViewModel::class.java
}