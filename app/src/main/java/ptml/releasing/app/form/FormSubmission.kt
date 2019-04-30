package ptml.releasing.app.form

import ptml.releasing.cargo_info.model.FormDamage
import ptml.releasing.cargo_info.model.FormSelection
import ptml.releasing.cargo_search.model.Value
import ptml.releasing.configuration.models.ConfigureDeviceData
import timber.log.Timber

class FormSubmission constructor(val formBuilder: FormBuilder?, private val formValidator: FormValidator?) {
    private val _valuesList = mutableListOf<Value>()
    val valuesList: List<Value> = _valuesList

    private val _damagesList = mutableListOf<FormDamage>()
    val damagesList: List<FormDamage> = _damagesList


    private val _selectionsList = mutableListOf<FormSelection>()
    val selectionList: List<FormSelection> = _selectionsList


    fun submit(){
        if (formValidator?.validate() == true) {
            for (form in formBuilder?.data ?: mutableListOf()) {
                submitBasedOnType(form)
            }
        }else{
            Timber.e("Form is invalid")
        }
    }

    private fun submitBasedOnType(data: ConfigureDeviceData?) {
        try {
            val formType = FormType.fromType(data?.type)
            when (formType) {

                FormType.TEXTBOX -> {
                    Timber.d("Getting TEXTBOX value")
                    _valuesList.add(formBuilder?.getTextBoxValue(data) ?: return)
                }

                FormType.MULTI_LINE_TEXTBOX -> {
                    Timber.d("Getting MULTI_LINE_TEXTBOX value")
                    _valuesList.add(formBuilder?.getTextBoxValue(data) ?: return)

                }
                FormType.IMAGES -> {
                    Timber.d("Getting IMAGES value")

                }

                FormType.PRINTER -> {
                    Timber.d("Getting PRINTER value")

                }

                FormType.DAMAGES -> {
                    Timber.d("Getting DAMAGES value")

                }


                FormType.SINGLE_SELECT -> {
                    Timber.d("Getting SINGLE_SELECT value")
                    _selectionsList.add(formBuilder?.getSingleSelect(data) ?: return)

                }


                FormType.MULTI_SELECT -> {
                    Timber.d("Getting MULTI_SELECT value")
                    _selectionsList.add(formBuilder?.getSingleSelect(data) ?: return)
                }

                FormType.CHECK_BOX -> {
                    Timber.d("Getting CHECK_BOX value")
                    //TODO
                }

                else -> {
                    Timber.e("Could not get the value for the form type =  %s", data?.type)
                }

            }
        } catch (e: Exception) { //
            Timber.e(e, "Could not get the value for the form type  = %s", data?.type)

        }
    }


}