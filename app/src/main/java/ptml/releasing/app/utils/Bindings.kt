package ptml.releasing.app.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.google.android.material.textfield.TextInputLayout
import ptml.releasing.R
import ptml.releasing.app.utils.extensions.beVisibleIf
import ptml.releasing.app.utils.extensions.hideKeyboard
import ptml.releasing.app.utils.extensions.setAllCapsInputFilter

/**
 * Created by kryptkode on 10/23/2019.
 */

@BindingAdapter("data")
fun AutoCompleteTextView.bindData(texts: List<String>?) {
    val adapter =
        ArrayAdapter<String>(context, R.layout.drop_down_menu_popup_item, texts ?: listOf())
    setAdapter(adapter)
}

abstract class EditTextWatcher : TextWatcher {
    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        onTextChange(p0)
    }

    abstract fun onTextChange(text: CharSequence?)
}

@BindingAdapter("textWatcher")
fun EditText.textWatcher(watcher: TextWatcher) {
    addTextChangedListener(watcher)
}

@BindingAdapter("errorText")
fun TextInputLayout.setErrorMessage(errorMessage: String?) {
    if (error != errorMessage) {
        error = errorMessage
    }
}

@BindingAdapter("hideKeyboardOnInputDone")
fun TextView.hideKeyboardOnInputDone(enabled: Boolean) {
    if (!enabled) return
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE)
            as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


@BindingAdapter("hideKeyboardOnInputDone")
fun EditText.hideKeyboardOnInputDone(enabled: Boolean) {
    if (!enabled) return
    val listener = TextView.OnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            clearFocus()
            hideKeyboard()
        }
        false
    }
    setOnEditorActionListener(listener)
}

@BindingAdapter("loadingVisibility")
fun View.loadingVisibility(dataState: LiveData<NetworkState>?) {
    beVisibleIf(dataState?.value?.status == Status.RUNNING)
}

@BindingAdapter("enableAllCapsFilter")
fun EditText.enableAllCapsFilter(enabled: Boolean) {
    if (!enabled) return
    setAllCapsInputFilter()
}




