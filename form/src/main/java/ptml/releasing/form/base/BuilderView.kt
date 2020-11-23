package ptml.releasing.form.base

import android.view.View
import android.widget.LinearLayout
import ptml.releasing.form.models.*


interface BuilderView {
    fun init(findCargoResponse: FormPreFillResponse?): BuilderView
    fun reset()
    fun build(
        configDataList: List<FormConfiguration>?,
        remarks: Map<Int, QuickRemark>?,
        voyages: Map<Int, Voyage>?
    ): LinearLayout

    fun getBottomButtons(): View?
    fun createViewFromConfig(data: FormConfiguration?, i: Int): View?

//   fun createLabel(data: ConfigureDeviceData?, i: Int): TextView

    //    fun createTextBox(data: ConfigureDeviceData?, i: Int): TextInputLayout
    fun validateTextBox(data: FormConfiguration?): Boolean

    fun getTextBoxValue(data: FormConfiguration?): Value?
    fun getTextBoxText(data: FormConfiguration?): String
    fun showTextBoxError(data: FormConfiguration?)


    //    fun createMultilineTextBox(data: ConfigureDeviceData?, i: Int): TextInputLayout
//  fun createButton(data: ConfigureDeviceData?, i: Int): View
    fun validateButton(data: FormConfiguration?): Boolean?

    fun getButtonNumber(data: FormConfiguration?): String
    fun showButtonError(data: FormConfiguration?)

    //    fun createSingleSelect(data: ConfigureDeviceData?, i: Int): View?
    fun validateSingleSelect(data: FormConfiguration?): Boolean?

    fun getSingleSelect(data: FormConfiguration?): FormSelection?
    fun getSingleSelectRVItem(data: FormConfiguration?): Options?
    fun getSingleSelectSpinnerItem(data: FormConfiguration?): Options?
    fun showSingleSelectError(data: FormConfiguration?)

    //    fun createQuickRemarkSelect(data: ConfigureDeviceData?, i: Int): View?
    fun validateQuickRemarkSelect(data: FormConfiguration?): Boolean?

    fun getQuickRemarkSelect(data: FormConfiguration?): FormSelection?

    fun validateVoyagesSelect(data: FormConfiguration?): Boolean?

    fun getVoyageSelectRVItem(data: FormConfiguration?): Voyage?
    fun getVoyageSelectSpinnerItem(data: FormConfiguration?): Voyage?
    fun getVoyagesSelect(data: FormConfiguration?): Voyage?

    //    fun createMultiSelectSelect(data: ConfigureDeviceData?, i: Int): View?
    fun validateMultiSelect(data: FormConfiguration?): Boolean?

    fun getMultiSelect(data: FormConfiguration?): FormSelection?

    fun getMultiSelectSpinnerItems(data: FormConfiguration?): List<Int>
    fun getMultiSelectRVItems(data: FormConfiguration?): List<Int>
    fun showMultiSelectError(data: FormConfiguration?)


    //    fun createCheckBox(data: ConfigureDeviceData?, i: Int): View
    fun validateCheckBox(data: FormConfiguration?): Boolean?

    fun getCheckBoxValue(data: FormConfiguration?): Value
    fun getCheckBoxCheckedState(data: FormConfiguration?): Boolean
    fun showCheckBoxError(data: FormConfiguration?)

//    fun createErrorView(): View

    fun initializeData()

    fun bindValuesDataToView(value: Value)

    fun bindOptionsDataToView(option: Option)
    fun showVoyageError(data: FormConfiguration?)

}




