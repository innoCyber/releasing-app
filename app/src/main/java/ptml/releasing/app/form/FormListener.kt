package ptml.releasing.app.form

import ptml.releasing.configuration.models.ConfigureDeviceData

abstract class FormListener {
    fun onFormAdded(data: ConfigureDeviceData?) {

    }

    abstract fun onClickFormButton(type: FormType)
}