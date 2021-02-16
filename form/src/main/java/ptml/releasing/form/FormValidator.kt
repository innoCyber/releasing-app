package ptml.releasing.form

import ptml.releasing.form.base.BuilderView
import ptml.releasing.form.models.FormConfiguration
import ptml.releasing.form.utils.Constants
import timber.log.Timber

class FormValidator constructor(
    val builderView: BuilderView?,
    val data: List<FormConfiguration>?
) {

    private var validForm = true
    var listener: ValidatorListener? = null


    fun validate(): Boolean {
        var isValid: Boolean
        Timber.d("validating form...")
        for (form in data ?: mutableListOf()) {
            if (form.required && (form.type != Constants.LABEL)) {
                Timber.d("Form is required: %s", form.type)
                isValid = validateBasedOnType(form) ?: true
                validationResult(isValid)
            }
        }

        if (!validForm) {
            listener?.onError()
        }

        return validForm
    }

    private fun validationResult(isValid: Boolean): Boolean {
        if (!isValid) {
            validForm = isValid
        }
        return validForm
    }


    private fun validateBasedOnType(data: FormConfiguration?): Boolean? {
        try {
            val formType = FormType.fromType(data?.type)
            when (formType) {

                FormType.TEXTBOX -> {
                    Timber.d("Validating textbox single")
                    return builderView?.validateTextBox(data)
                }

                FormType.MULTI_LINE_TEXTBOX -> {
                    Timber.d("Validating multiple single")
                    return builderView?.validateTextBox(data)
                }
                FormType.IMAGES -> {
                    Timber.d("Validating image")
                    return builderView?.validateButton(data)
                }

                FormType.PRINTER -> {
                    Timber.d("Validating printer")
                    return true
                }

                FormType.PRINTER_DAMAGES -> {
                    Timber.d("Validating printer damages")
                    return true
                }

                FormType.DAMAGES -> {
                    Timber.d("Validating damages")
                    return builderView?.validateButton(data)
                }


                FormType.SINGLE_SELECT -> {
                    Timber.d("Validating single select")
                    return builderView?.validateSingleSelect(data)
                }


                FormType.MULTI_SELECT -> {
                    Timber.d("Validating multi select")
                    return builderView?.validateMultiSelect(data)
                }

                FormType.QUICK_REMARK -> {
                    Timber.d("Validating quick remark")
                    return builderView?.validateQuickRemarkSelect(data)
                }

                FormType.CHECK_BOX -> {
                    Timber.d("Validating checkbox")
                    return builderView?.validateCheckBox(data)
                }
                
                FormType.VOYAGE -> {
                    Timber.d("Validating voyage")
                    return builderView?.validateVoyagesSelect(data)
                }

                else -> {
                    Timber.e("Could not validate form type for %s", data?.type)
                    return null
                }

            }
        } catch (e: Exception) { //return an url view
            Timber.e(e, "Could not validate form for %s", data?.type)
            return null
        }
    }


    interface ValidatorListener {
        fun onError()
    }
}