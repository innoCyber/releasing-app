package ptml.releasing.adminlogin.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.admin_login_basic_auth_layout.view.*
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.admin_config.view.AdminConfigActivity
import ptml.releasing.adminlogin.viewmodel.LoginViewModel
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.exception.ErrorHandler
import ptml.releasing.app.utils.NetworkState
import ptml.releasing.app.utils.StaticBasicAuth.saveAdminLoginDetails
import ptml.releasing.app.utils.StaticBasicAuth.getAdminPassword
import ptml.releasing.app.utils.StaticBasicAuth.getAdminUsername
import ptml.releasing.app.utils.Status
import ptml.releasing.app.utils.hideSoftInputFromWindow
import ptml.releasing.app.utils.livedata.Event
import ptml.releasing.databinding.ActivityAdminLoginBinding
import ptml.releasing.BuildConfig


class LoginActivity : BaseActivity<LoginViewModel, ActivityAdminLoginBinding>() {


    lateinit var mDialogViewc: View
    lateinit var mBuilder: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showUpEnabled(true)
        setupInputBasicAuthDialog()
        initErrorDrawable(binding.includeError.imgError)

        viewModel = ViewModelProviders.of(this, viewModeFactory)
            .get(LoginViewModel::class.java)

        viewModel.getNetworkState().observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                if (it == NetworkState.LOADING) {
                    showLoading(
                        binding.includeProgress.root,
                        binding.includeProgress.tvMessage,
                        R.string.logining_in
                    )
                } else {
                    hideLoading(binding.includeProgress.root)
                }
                if (it.status == Status.FAILED) {
                    val error = ErrorHandler(this).getErrorMessage(it.throwable)
                    showLoading(binding.includeError.root, binding.includeError.tvMessage, error)
                } else {
                    hideLoading(binding.includeError.root)
                }
            }

        })

        viewModel.getLoadNext().observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                startNewActivity(AdminConfigActivity::class.java, true)
            }
        })

        viewModel.getErrorMessage().observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let {
                showErrorDialog(it)
            }
        })


        viewModel.getPasswordValidation().observe(this, Observer { event ->
            event.getContentIfNotHandled().let {
                if (it != null) {
                    binding.tilPassword.error = getString(it)
                } else {
                    binding.tilPassword.error = null
                }

            }
        })

        viewModel.getUsernameValidation().observe(this, Observer { event ->
            event.getContentIfNotHandled().let {
                if (it != null) {
                    binding.tilAdminId.error = getString(it)
                } else {
                    binding.tilAdminId.error = null
                }

            }
        })

        binding.btnLoginLayout.setOnClickListener {
            //login()
            verifyLoginCredentialsAndShowBasicDialog(binding.editName.text.toString(), binding.editPassword.text.toString())
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
            //login()
            verifyLoginCredentialsAndShowBasicDialog(binding.editName.text.toString(), binding.editPassword.text.toString())
        }
    }

    private fun login() {
        viewModel.login(binding.editName.text.toString(), binding.editPassword.text.toString(),"","")
        binding.editPassword.clearFocus()
        binding.editName.clearFocus()
        binding.btnLogin.hideSoftInputFromWindow()

    }

    private fun verifyLoginCredentialsAndShowBasicDialog(username: String, password: String){

        if (username.isEmpty() || password.isEmpty()) {
            if (username.isEmpty()) {
                binding.editName.error = "Enter ID to proceed"
            }

            if (password.isEmpty()) {
                binding.editPassword.error = "Enter password to proceed"
            }

            return
        }

        saveAdminLoginDetails(username,password)
        mBuilder = AlertDialog.Builder(this).create()
        if (mDialogViewc.parent != null) {
            (mDialogViewc.parent as ViewGroup).removeView(mDialogViewc)
        }
        mBuilder.setView(mDialogViewc)
        mBuilder.setTitle("Basic Auth Credentials Dialog")
        mBuilder.show()

        populateBasicAuthDialogEntries()
    }

    @SuppressLint("SetTextI18n")
    private fun populateBasicAuthDialogEntries(){

        if(BuildConfig.FLAVOR == "production"){
            mDialogViewc.url_edittext.setText("https://billing.grimaldi-nigeria.com:1448/api/AndroidAppReleasing/")
        }
        if(BuildConfig.FLAVOR == "staging"){
            mDialogViewc.url_edittext.setText("https://billing.grimaldi-nigeria.com:8085/api/AndroidAppReleasing/")
        }
        if(BuildConfig.FLAVOR == "dev"){
            mDialogViewc.url_edittext.setText("https://billing.grimaldi-nigeria.com:1449/api/AndroidAppReleasing/")
        }

        mDialogViewc.username_edittext.setText(if(BuildConfig.FLAVOR == "production" || BuildConfig.FLAVOR == "staging") "Ptml01R1" else "admin")
        mDialogViewc.edit_password.setText(if(BuildConfig.FLAVOR == "production" || BuildConfig.FLAVOR == "staging" ) "SPtml0309!!" else "Passw2021")
    }

    private fun setupInputBasicAuthDialog() {
        mDialogViewc = LayoutInflater.from(this).inflate(
            R.layout.admin_login_basic_auth_layout,
            null
        )

        mBuilder = AlertDialog.Builder(this).create()
    }

    override fun getViewModelClass() = LoginViewModel::class.java

    override fun getBindingVariable() = BR._all

    override fun getLayoutResourceId() = R.layout.activity_admin_login

    override fun onBackPressed() {
        navigator.goToSearch(this)
        super.onBackPressed()
    }
}