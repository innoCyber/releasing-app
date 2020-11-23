package ptml.releasing.form.base


import ptml.releasing.form.models.*

interface BuilderPresenter {

    fun initializeQuickRemarks(quickRemarks: Map<Int, QuickRemark>?)
    fun initializeVoyages(voyages: Map<Int, Voyage>?)
    fun init(findCargoResponse: FormPreFillResponse?): BuilderView

    fun validateTextBox(data: FormConfiguration?): Boolean
    fun getTextBoxValue(data: FormConfiguration?): Value?


    //    fun createMultilineTextBox(data: ConfigureDeviceData?, i: Int): TextInputLayout
//    fun createButton(data: ConfigureDeviceData?, i: Int): View
    fun validateButton(data: FormConfiguration?): Boolean?

    //    fun createSingleSelect(data: ConfigureDeviceData?, i: Int): View?
    fun validateSingleSelect(data: FormConfiguration?): Boolean?

    fun getSingleSelect(data: FormConfiguration?): FormSelection?

    //    fun createQuickRemarkSelect(data: ConfigureDeviceData?, i: Int): View?
    fun validateQuickRemarkSelect(data: FormConfiguration?): Boolean?
    
    fun getQuickRemarkSelect(data: FormConfiguration?): FormSelection?

    fun validateVoyagesSelect(data: FormConfiguration?): Boolean?

    fun getVoyagesSelect(data: FormConfiguration?): Voyage?
    
    //    fun createMultiSelectSelect(data: ConfigureDeviceData?, i: Int): View?
    fun validateMultiSelect(data: FormConfiguration?): Boolean?

    fun getMultiSelect(data: FormConfiguration?): FormSelection?


    //    fun createCheckBox(data: ConfigureDeviceData?, i: Int): View
    fun validateCheckBox(data: FormConfiguration?): Boolean?

    fun getCheckBoxValue(data: FormConfiguration?): Value

//    fun createErrorView(): View


    fun initializeDefaultValue(data: FormConfiguration?)

    fun initializeDefaultOption(data: FormConfiguration?)

    fun initializeValues()

    fun initializeOptions()

    fun validate() {

    }
}
