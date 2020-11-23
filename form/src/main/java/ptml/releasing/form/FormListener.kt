package ptml.releasing.form

import android.view.View
import ptml.releasing.form.models.Damage
import ptml.releasing.form.models.FormConfiguration

@Suppress("UNUSED_PARAMETER")
abstract class FormListener {
    open fun onStartLoad() {

    }

    open fun onEndLoad() {

    }

    open fun onFormAdded(data: FormConfiguration?) {

    }


    open fun onFormAdded(damageData: Damage?) {

    }

    open fun onError(message: String) {

    }

    abstract fun onClickSave()
    abstract fun onClickReset()
    abstract fun onClickFormButton(type: FormType, view: View)
    abstract fun onDataChange(data: FormConfiguration?, change: Any?)

}