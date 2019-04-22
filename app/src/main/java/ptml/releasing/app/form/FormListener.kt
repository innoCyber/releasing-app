package ptml.releasing.app.form

import android.view.View
import ptml.releasing.configuration.models.ConfigureDeviceData

@Suppress("UNUSED_PARAMETER")
abstract class FormListener {
    fun onFormAdded(data: ConfigureDeviceData?) {

    }

    abstract fun onClickFormButton(type: FormType, view: View)
}