package ptml.releasing.app.utils.extensions

import android.app.Activity
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.core.view.children
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout


/**
 * Created by kryptkode on 11/8/2019.
 */
fun Activity.hideKeyBoardOnTouchOfNonEditableViews() {
    val root = findViewById<ViewGroup>(android.R.id.content)
    for (view in root.children) {
        if (view !is TextInputLayout) {
            view.setOnTouchListener { _, _ ->
                view.hideKeyboard()
                false
            }
        }
    }
}

@MainThread
fun <VM : ViewModel> ComponentActivity.viewModels(
    viewModelClass: Class<VM>,
    factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }

    return ViewModelLazy(viewModelClass.kotlin, { viewModelStore }, factoryPromise)
}