package ptml.releasing.app.form

import ptml.releasing.configuration.models.ConfigureDeviceData

interface FormListener {
    fun onFormAdded(data: ConfigureDeviceData)
}