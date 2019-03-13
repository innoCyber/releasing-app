package ptml.releasing.ui.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.StringRes
import dagger.android.support.DaggerFragment
import ptml.releasing.R
import ptml.releasing.ui.dialogs.InfoConfirmDialog

open class BaseFragment : DaggerFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isOffline()) {
            baseActivity().showSnackBarError()
        }
    }

    protected fun showLoading(view: View, textView: TextView, @StringRes message: Int) {
        textView.text = getString(message)

        val bottomUp = AnimationUtils.loadAnimation(context, R.anim.bottom_up)

        view.startAnimation(bottomUp)
        view.visibility = View.VISIBLE
    }

    protected fun hideLoading(view: View) {

        val slide = AnimationUtils.loadAnimation(
            context,
            R.anim.up_down
        )
        view.startAnimation(slide)
        view.visibility = View.GONE
    }


    protected fun hideKeyBoard(view: View) {
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun showDialog(message: String?) {
        InfoConfirmDialog.showDialog(context, getString(R.string.error), message, R.drawable.ic_error) {

        }
    }

    fun baseActivity(): BaseActivity {
        return (activity as BaseActivity)
    }


    fun notifyUser(message: String) = baseActivity().notifyUser(message)

    fun notifyUser(view: View, message: String) = baseActivity().notifyUser(view, message)


    fun isOffline() = baseActivity().isOffline()


}