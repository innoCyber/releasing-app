package ptml.releasing.app.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseFragment<V, D> : DaggerFragment() where V : BaseViewModel, D : ViewDataBinding {

    lateinit var binding: D
    lateinit var viewModel: V

    @Inject
    protected lateinit var viewModeFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModeFactory)
                .get(getViewModelClass())
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is BaseFragmentActivity<*>) { //check to ensure that the fragment's activity is a child of base activity
            throw RuntimeException("$context must be a subclass of ${BaseActivity::class.java.name}")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        initBinding(inflater, container)
        return binding.root
    }


    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, getLayoutResourceId(), container, false)
        binding.setVariable(getBindingVariable(), viewModel)
        binding.executePendingBindings()
    }

    protected fun showDialog(message: String?) {
        activity().showDialog(message)
    }

    protected fun initErrorDrawable(imageView: ImageView) {
        activity().initErrorDrawable(imageView)
    }

    protected fun animateViewUpWards(view: View, textView: TextView, @StringRes message: Int) {
        activity().showLoading(view, textView, message)
    }

    protected fun animateViewDownWards(view: View) {
        activity().hideLoading(view)
    }

    fun isOffline(): Boolean {
        return activity().isOffline()
    }

    fun notifyUser(message: String?) {
        activity().notifyUser(message)
    }

    fun notifyUser(view: View, message: String) {
        activity().notifyUser(view, message)
    }

    fun startNewActivity(name: Class<*>, finish: Boolean = false) {
        activity().startNewActivity(name, finish)
    }

    fun activity(): BaseActivity<*, *> {
        return activity as BaseActivity<*, *>
    }

    protected abstract fun getBindingVariable(): Int

    @LayoutRes
    protected abstract fun getLayoutResourceId(): Int

    protected abstract fun getViewModelClass(): Class<V>

}