package ptml.releasing.resetpassword.view

import android.os.Bundle
import androidx.core.content.ContextCompat
import ptml.releasing.BR
import ptml.releasing.R
import ptml.releasing.app.base.BaseActivity
import ptml.releasing.app.data.domain.state.DataState
import ptml.releasing.app.utils.extensions.beVisibleIf
import ptml.releasing.app.utils.extensions.observe
import ptml.releasing.databinding.ActivityResetPasswordBinding
import ptml.releasing.resetpassword.viewmodel.ResetPasswordViewModel

/**
 * Created by kryptkode on 10/23/2019.
 */
class ResetPasswordActivity : BaseActivity<ResetPasswordViewModel, ActivityResetPasswordBinding>() {

    override fun getLayoutResourceId() = R.layout.activity_reset_password

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModelClass(): Class<ResetPasswordViewModel> {
        return ResetPasswordViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        binding.progressBar.progressRoot.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.progress_overlay
            )
        )
        binding.progressBar.cardView.setCardBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.progress_overlay
            )
        )
        viewModel.getExitState().observe(this) { event ->
            event?.getContentIfNotHandled()?.let {
                finish()
            }
        }

        viewModel.getResetPasswordDataState().observe(this) {
            binding.progressBar.root.beVisibleIf(it is DataState.Loading)
            when (it) {
                is DataState.Loading -> {
                    binding.progressBar.tvMessage.text = getString(R.string.login_loading_message)
                }

                is DataState.Error -> {
                    showErrorDialog(it.error as String?)
                }

                is DataState.Success -> {
                    notifyUser(getString(R.string.password_reset_success))
                    finish()

                }
            }
        }
    }

}