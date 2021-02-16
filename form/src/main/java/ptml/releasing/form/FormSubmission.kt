package ptml.releasing.form

import ptml.releasing.form.base.BuilderView
import ptml.releasing.form.models.*

import timber.log.Timber

class FormSubmission constructor(
    val formBuilder: BuilderView?,
    val data: List<FormConfiguration>?,
    private val formValidator: FormValidator?
) {
    private val _valuesList = mutableListOf<Value>()
    val valuesList: List<Value> = _valuesList

    private val _damagesList = mutableListOf<FormDamage>()
    val damagesList: List<FormDamage> = _damagesList

    private val _selectionsList = mutableListOf<FormSelection>()
    val selectionList: List<FormSelection> = _selectionsList

    var selectedVoyage : Voyage? = null

    fun submit() {
        if (formValidator?.validate() == true) {
            for (form in data ?: mutableListOf()) {
                submitBasedOnType(form)
            }
        } else {
            Timber.e("Form is invalid")
        }
    }

    private fun submitBasedOnType(data: FormConfiguration?) {
        try {
            val formType = FormType.fromType(data?.type)
            when (formType) {

                FormType.TEXTBOX -> {
                    Timber.d("Getting TEXTBOX value")
                    val value = formBuilder?.getTextBoxValue(data)
                    if (value != null) {
                        Timber.d("Gotten TEXTBOX value : $value")
                        _valuesList.add(value)
                    }
                }

                FormType.MULTI_LINE_TEXTBOX -> {
                    Timber.d("Getting MULTI_LINE_TEXTBOX value")
                    val value = formBuilder?.getTextBoxValue(data)
                    if (value != null) {
                        Timber.d("Gotten MULTI_LINE_TEXTBOX value : $value")
                        _valuesList.add(value)
                    }

                }
                FormType.IMAGES -> {
                    Timber.d("Getting IMAGES value")

                }

                FormType.PRINTER -> {
                    Timber.d("Getting PRINTER value")

                }

                FormType.PRINTER_DAMAGES->{
                    Timber.d("Getting Printer Damages Value")
                    }

                FormType.DAMAGES -> {
                    Timber.d("Getting DAMAGES value")

                }


                FormType.SINGLE_SELECT -> {
                    Timber.d("Getting SINGLE_SELECT value")
                    val formSelection = formBuilder?.getSingleSelect(data)
                    if (formSelection != null) {
                        _selectionsList.add(formSelection)
                        Timber.d("Gotten SINGLE_SELECT value $formSelection")
                    }

                }

                FormType.MULTI_SELECT -> {
                    Timber.d("Getting MULTI_SELECT value")
                    val formSelection = formBuilder?.getMultiSelect(data)
                    if (formSelection != null) {
                        _selectionsList.add(formSelection)
                        Timber.d("Gotten MULTI_SELECT value $formSelection")
                    }
                }


                FormType.QUICK_REMARK -> {
                    Timber.d("Getting QUICK_REMARK value")
                    val formSelection = formBuilder?.getQuickRemarkSelect(data)
                    if (formSelection != null) {
                        _selectionsList.add(formSelection)
                        Timber.d("Gotten QUICK_REMARK value $formSelection")
                    }
                }

                FormType.CHECK_BOX -> {
                    Timber.d("Getting CHECK_BOX value")
                    val value = formBuilder?.getCheckBoxValue(data)
                    if (value != null) {
                        _valuesList.add(value)
                        Timber.d("Gotten CHECK_BOX value $value")
                    }
                }

                FormType.VOYAGE -> {
                    Timber.d("Getting VOYAGE value")
                    val value = formBuilder?.getVoyagesSelect(data)
                    if (value != null) {
                        selectedVoyage = value
                        Timber.d("Gotten VOYAGE value $value")
                    }
                }

                else -> {
                    Timber.e("Could not get the value for the form type =  %s", data?.type)
                }
            }
        } catch (e: Exception) { //
            Timber.e(
                e,
                "Could not get the value for the form type with exception  = %s",
                data?.type
            )

        }
    }


}