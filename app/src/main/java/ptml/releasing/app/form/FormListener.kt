package ptml.releasing.app.form

import android.view.View
import ptml.releasing.configuration.models.ConfigureDeviceData

@Suppress("UNUSED_PARAMETER")
abstract class FormListener {
    open fun onStartLoad() {

    }

    open fun onEndLoad() {

    }
    open fun onFormAdded(data: ConfigureDeviceData?) {

    }

    open fun onError(message:String) {

    }

    abstract fun onClickSave()
    abstract fun onClickReset()
    abstract fun onClickFormButton(type: FormType, view: View)
    open fun onImageButtonLoaded(view: View){

    }
}