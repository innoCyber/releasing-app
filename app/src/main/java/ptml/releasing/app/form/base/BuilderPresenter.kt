package ptml.releasing.app.form.base

import ptml.releasing.cargo_info.model.FormSelection
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.cargo_search.model.Value
import ptml.releasing.configuration.models.ConfigureDeviceData
import ptml.releasing.quick_remarks.model.QuickRemark

interface BuilderPresenter{

    fun provideQuickRemarks(quickRemarks: Map<Int, QuickRemark>?)
    fun init(findCargoResponse: FindCargoResponse?): BuilderView

    fun validateTextBox(data: ConfigureDeviceData?): Boolean
    fun getTextBoxValue(data: ConfigureDeviceData?): Value?


    //    fun createMultilineTextBox(data: ConfigureDeviceData?, i: Int): TextInputLayout
//    fun createButton(data: ConfigureDeviceData?, i: Int): View
    fun validateButton(data: ConfigureDeviceData?): Boolean?

    //    fun createSingleSelect(data: ConfigureDeviceData?, i: Int): View?
    fun validateSingleSelect(data: ConfigureDeviceData?): Boolean?
    fun getSingleSelect(data: ConfigureDeviceData?): FormSelection?

    //    fun createQuickRemarkSelect(data: ConfigureDeviceData?, i: Int): View?
    fun validateQuickRemarkSelect(data: ConfigureDeviceData?): Boolean?
    fun getQuickRemarkSelect(data: ConfigureDeviceData?): FormSelection?

    //    fun createMultiSelectSelect(data: ConfigureDeviceData?, i: Int): View?
    fun validateMultiSelect(data: ConfigureDeviceData?): Boolean?
    fun getMultiSelect(data: ConfigureDeviceData?): FormSelection?


    //    fun createCheckBox(data: ConfigureDeviceData?, i: Int): View
    fun validateCheckBox(data: ConfigureDeviceData?): Boolean?
    fun getCheckBoxValue(data: ConfigureDeviceData?): Value

//    fun createErrorView(): View



    fun initializeDefaultValue(data: ConfigureDeviceData?)

    fun initializeDefaultOption(data: ConfigureDeviceData?)

    fun initializeValues()

    fun initializeOptions()

    fun validate(){

    }
}
