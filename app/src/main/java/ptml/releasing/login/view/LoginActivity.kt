package ptml.releasing.login.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.admin_config.view.AdminConfigActivity
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.utils.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.Status
import ptml.releasing.app.utils.hideSoftInputFromWindow
import ptml.releasing.databinding.ActivityLoginBinding
import ptml.releasing.login.viewmodel.LoginViewModel

class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)
        initErrorDrawable(binding.includeError.imgError)

        viewModel = ViewModelProviders.of(this, viewModeFactory)
            .get(LoginViewModel::class.java)

        viewModel.networkState.observe(this, Observer {
            if (it == NetworkState.LOADING) {
                showLoading(
                    binding.includeProgress.root,
                    binding.includeProgress.tvMessage,
                    R.string.logining_in
                )
            } else {
                hideLoading(binding.includeProgress.root)
            }

            if (it?.status == Status.FAILED) {
                val error = ErrorHandler().getErrorMessage(it.throwable)
                showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
            } else {
                hideLoading(binding.includeError.root)
            }
        })

        viewModel.loadNext.observe(this, Observer {
            startNewActivity(AdminConfigActivity::class.java, true)
        })

        viewModel.errorMessage.observe(this, Observer {
            showErrorDialog(it)
        })


        viewModel.passwordValidation.observe(this, Observer {
            if (it != null) {
                binding.tilPassword.error = getString(it)
            } else {
                binding.tilPassword.error = null
            }
        })

        viewModel.usernameValidation.observe(this, Observer {
            if (it != null) {
                binding.tilAdminId.error = getString(it)
            } else {
                binding.tilAdminId.error = null
            }
        })

        binding.btnLoginLayout.setOnClickListener {
            login()
        }

        binding.editName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilAdminId.error = null
            }
        })

        binding.editPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilPassword.error = null
            }
        })

        binding.includeError.btnReloadLayout.setOnClickListener {
            login()
        }
    }

    private fun login() {
        viewModel.login(binding.editName.text.toString(), binding.editPassword.text.toString())
        binding.editPassword.clearFocus()
        binding.editName.clearFocus()
        binding.btnLogin.hideSoftInputFromWindow()
    }

    override fun getViewModelClass() = LoginViewModel::class.java

    override fun getBindingVariable() = BR._all

    override fun getLayoutResourceId() = R.layout.activity_login
}