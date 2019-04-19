package ptml.releasing.app.form

import ptml.releasing.configuration.models.ConfigureDeviceData

@Suppress("UNUSED_PARAMETER")
abstract class FormListener {
    fun onFormAdded(data: ConfigureDeviceData?) {

    }

    abstract fun onClickFormButton(type: FormType)
}