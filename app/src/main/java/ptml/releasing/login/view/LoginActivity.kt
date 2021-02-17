package ptml.releasing.login.view

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.data.domain.repository.LoginRepository
import ptml.releasing.app.data.domain.state.DataState
import ptml.releasing.app.utils.extensions.beVisibleIf
import ptml.releasing.app.utils.livedata.observe
import ptml.releasing.databinding.ActivityLoginBinding
import ptml.releasing.login.viewmodel.LoginViewModel
import javax.inject.Inject

/**
 * Created by kryptkode on 1/21/2020.
 */
class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    @Inject
    lateinit var loginRepository: LoginRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIfLoggedIn()
        binding.viewModel = viewModel
        setupObservers()
    }

    private fun checkIfLoggedIn() {
        lifecycleScope.launchWhenCreated {
            val loggedIn = loginRepository.getLoggedIn()
            if (loggedIn) {
                navigator.goToSearch(this@LoginActivity)
            }
        }
    }


    private fun setupObservers() {
        viewModel.getLoginDataState().observe(this) {
            binding.progressBar.root.beVisibleIf(it is DataState.Loading)
            when (it) {
                is DataState.Loading -> {
                    binding.progressBar.tvMessage.text = getString(R.string.login_loading_message)
                }

                is DataState.Error -> {
                    showErrorDialog(it.error as String?)
                }

                is DataState.Success -> {
                    //unused
                }
            }
        }

        viewModel.getGoToSearchEvent().observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                navigator.goToSearch(this)
            }
        }


        viewModel.getGoToReset().observe(this) {
            it?.getContentIfNotHandled()?.let {
                navigator.goToReset(this@LoginActivity)
            }
        }
    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_login
    }

    override fun getViewModelClass(): Class<LoginViewModel> {
        return LoginViewModel::class.java
    }


}