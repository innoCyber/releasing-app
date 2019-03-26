package ptml.releasing.app.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import javax.inject.Inject

abstract class BaseActivity<T, D> : BaseFragmentActivity<D>() where T : BaseViewModel, D : ViewDataBinding {

    protected lateinit var viewModel: T
    @Inject
    protected lateinit var viewModeFactory: ViewModelProvider.Factory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModeFactory)
                .get(getViewModelClass())


    }


    protected abstract fun getViewModelClass(): Class<T>

   }
