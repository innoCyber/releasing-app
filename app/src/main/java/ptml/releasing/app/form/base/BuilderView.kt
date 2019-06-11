package ptml.releasing.app.form.base

import android.view.View
import android.widget.LinearLayout
import ptml.releasing.app.form.adapter.SelectModel
import ptml.releasing.cargo_info.model.FormSelection
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.cargo_search.model.Option
import ptml.releasing.cargo_search.model.Value
import ptml.releasing.configuration.models.ConfigureDeviceData
import ptml.releasing.configuration.models.Options
import ptml.releasing.quick_remarks.model.QuickRemark
import timber.log.Timber

interface BuilderView{
    fun init(findCargoResponse: FindCargoResponse?): BuilderView
    fun reset()
    fun build(configDataList: List<ConfigureDeviceData>?,
              remarks: Map<Int, QuickRemark>?): LinearLayout

    fun getBottomButtons():View?
    fun createViewFromConfig(data: ConfigureDeviceData?, i: Int): View?

//    fun createLabel(data: ConfigureDeviceData?, i: Int): TextView

//    fun createTextBox(data: ConfigureDeviceData?, i: Int): TextInputLayout
    fun validateTextBox(data: ConfigureDeviceData?): Boolean
    fun getTextBoxValue(data: ConfigureDeviceData?): Value?
    fun getTextBoxText(data: ConfigureDeviceData?):String
    fun showTextBoxError(data: ConfigureDeviceData?)


//    fun createMultilineTextBox(data: ConfigureDeviceData?, i: Int): TextInputLayout
//    fun createButton(data: ConfigureDeviceData?, i: Int): View
    fun validateButton(data: ConfigureDeviceData?): Boolean?
    fun getButtonNumber(data: ConfigureDeviceData?):String
    fun showButtonError(data: ConfigureDeviceData?)

//    fun createSingleSelect(data: ConfigureDeviceData?, i: Int): View?
    fun validateSingleSelect(data: ConfigureDeviceData?): Boolean?
    fun getSingleSelect(data: ConfigureDeviceData?): FormSelection?
    fun getSingleSelectRVItem(data: ConfigureDeviceData?):Options?
    fun getSingleSelectSpinnerItem(data: ConfigureDeviceData?):Options?
    fun showSingleSelectError(data: ConfigureDeviceData?)

//    fun createQuickRemarkSelect(data: ConfigureDeviceData?, i: Int): View?
    fun validateQuickRemarkSelect(data: ConfigureDeviceData?): Boolean?
    fun getQuickRemarkSelect(data: ConfigureDeviceData?): FormSelection?

//    fun createMultiSelectSelect(data: ConfigureDeviceData?, i: Int): View?
    fun validateMultiSelect(data: ConfigureDeviceData?): Boolean?
    fun getMultiSelect(data: ConfigureDeviceData?): FormSelection?

    fun getMultiSelectSpinnerItems(data: ConfigureDeviceData?):List<Int>
    fun getMultiSelectRVItems(data: ConfigureDeviceData?):List<Int>
    fun showMultiSelectError(data: ConfigureDeviceData?)


//    fun createCheckBox(data: ConfigureDeviceData?, i: Int): View
    fun validateCheckBox(data: ConfigureDeviceData?): Boolean?
    fun getCheckBoxValue(data: ConfigureDeviceData?): Value
    fun getCheckBoxCheckedState(data: ConfigureDeviceData?): Boolean
    fun showCheckBoxError(data: ConfigureDeviceData?)

//    fun createErrorView(): View

    fun initializeData()

    fun bindValuesDataToView(value: Value)

    fun bindOptionsDataToView(option:Option)

}




