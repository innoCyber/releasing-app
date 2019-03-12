package ptml.releasing.ui.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ptml.releasing.R
import ptml.releasing.databinding.FragmentLoginBinding
import ptml.releasing.ui.MainActivity
import ptml.releasing.ui.base.BaseFragment
import ptml.releasing.ui.dialogs.InfoConfirmDialog
import ptml.releasing.utils.ErrorHandler
import ptml.releasing.utils.NetworkState
import ptml.releasing.utils.NotificationUtils
import ptml.releasing.utils.Status
import javax.inject.Inject


class LoginFragment @Inject constructor() : BaseFragment() {

    lateinit var binding: FragmentLoginBinding

    lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var viewModeFactory: ViewModelProvider.Factory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = ViewModelProviders.of(this, viewModeFactory)
            .get(LoginViewModel::class.java)


        loginViewModel.networkState.observe(this, Observer {
            if (it == NetworkState.LOADING) {
                showLoading()
            } else {
                hideLoading()
            }

            if (it?.status == Status.FAILED) {
                val error = ErrorHandler(context!!).getErrorMessage(it.throwable)
                NotificationUtils.notifyUser(binding.root, error)
            }
        })

        loginViewModel.response.observe(this, Observer {
            when (it.isSuccess) {
                true -> {
                    (activity as LoginActivity).startNewActivity(MainActivity::class.java, true)
                }

                else -> {
                    showDialog(it.message)
                }
            }
        })

        loginViewModel.passwordValidation.observe(this, Observer {
            if (it != null) {
                binding.tilPassword.error = getString(it)
            } else {
                binding.tilPassword.error = null
            }
        })

        loginViewModel.usernameValidation.observe(this, Observer {
            if (it != null) {
                binding.tilAdminId.error = getString(it)
            } else {
                binding.tilAdminId.error = null
            }
        })

        binding.btnLoginLayout.setOnClickListener {
            loginViewModel.login(binding.editName.text.toString(), binding.editPassword.text.toString())
            binding.editPassword.clearFocus()
            binding.editName.clearFocus()
            hideKeyBoard(binding.btnLogin)
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

    }


    private fun showLoading() {
        binding.includeProgress.tvMessage.text = getString(R.string.logining_in)
        binding.includeProgress.root.visibility = View.VISIBLE
        binding.includeProgress.root.alpha = 0.0f


        val slide = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, binding.includeProgress.root.height.toFloat(),
            Animation.RELATIVE_TO_SELF, 0.0f
        )

        slide.duration = 300
        slide.fillAfter = true
        slide.isFillEnabled = true

        binding.includeProgress.root.startAnimation(slide)

        slide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.includeProgress.root.alpha = 1.0f
            }

            override fun onAnimationStart(animation: Animation?) {

            }
        })
    }

    private fun hideLoading() {


        val slide = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, binding.includeProgress.root.height.toFloat()
        )

        slide.duration = 300
        slide.fillAfter = true
        slide.isFillEnabled = true

        binding.includeProgress.root.startAnimation(slide)

        slide.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.includeProgress.root.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {

            }
        })
    }

    private fun hideKeyBoard(view: View){
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showDialog(message: String) {
        InfoConfirmDialog.showDialog(context, getString(R.string.error), message, R.drawable.ic_error) {

        }
    }

}