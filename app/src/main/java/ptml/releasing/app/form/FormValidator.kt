package ptml.releasing.app.form

import ptml.releasing.configuration.models.ConfigureDeviceData
import timber.log.Timber

class FormValidator constructor(var formBuilder: FormBuilder?) {

    private var validForm = true
    var listener: ValidatorListener? = null


    fun validate(): Boolean {
        var isValid: Boolean
        Timber.d("validating form...")
        for (form in formBuilder?.data ?: mutableListOf()) {
            if (form.required) {
                Timber.d("Form is required: %s", form.type)
                isValid = validateBasedOnType(form) ?: false
                validationResult(isValid)
            }
        }

        if(!validForm){
            listener?.onError()
        }

        return validForm
    }

    private fun validationResult(isValid:Boolean): Boolean {
        if(!isValid){
            validForm = isValid
        }
        return validForm
    }



    private fun validateBasedOnType(data: ConfigureDeviceData?): Boolean? {
        try {
            val formType = FormType.fromType(data?.type)
            when (formType) {

                FormType.TEXTBOX -> {
                    Timber.d("Validating textbox single")
                    return formBuilder?.validateTextBox(data)
                }

                FormType.MULTI_LINE_TEXTBOX -> {
                    Timber.d("Validating multiple single")
                    return formBuilder?.validateTextBox(data)
                }
                FormType.IMAGES -> {
                    Timber.d("Validating image")
                    return formBuilder?.validateButton(data)
                }

                FormType.PRINTER -> {
                    Timber.d("Validating printer")
                    return true
                }

                FormType.DAMAGES -> {
                    Timber.d("Validating damages")
                    return formBuilder?.validateButton(data)
                }


                FormType.SINGLE_SELECT -> {
                    Timber.d("Validating single select")
                    return formBuilder?.validateSingleSelect(data)
                }


                FormType.MULTI_SELECT -> {
                    Timber.d("Validating multi select")
                    return formBuilder?.validateMultiSelect(data)
                }

                FormType.CHECK_BOX -> {
                    Timber.d("Validating checkbox")
                    return formBuilder?.validateCheckBox(data)
                }

                else -> {
                    Timber.e("Could not validate form type for %s", data?.type)
                    return null
                }

            }
        } catch (e: Exception) { //return an error view
            Timber.e(e, "Could not validate form for %s", data?.type)
            return null
        }
    }


    interface ValidatorListener{
        fun onError()
    }
}