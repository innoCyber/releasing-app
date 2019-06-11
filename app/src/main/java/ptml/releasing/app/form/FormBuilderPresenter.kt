package ptml.releasing.app.form

import ptml.releasing.app.form.base.BuilderPresenter
import ptml.releasing.app.form.base.BuilderView
import ptml.releasing.app.utils.Constants
import ptml.releasing.cargo_info.model.FormSelection
import ptml.releasing.cargo_search.model.FindCargoResponse
import ptml.releasing.cargo_search.model.Option
import ptml.releasing.cargo_search.model.Value
import ptml.releasing.configuration.models.ConfigureDeviceData
import ptml.releasing.quick_remarks.model.QuickRemark
import timber.log.Timber

class FormBuilderPresenter(private val formBuilder: BuilderView) :
    BuilderPresenter {
    private var values = mutableMapOf<Int?, Value>()
    private var options = mutableMapOf<Int?, Option>()
    var quickRemarks: Map<Int, QuickRemark>? = null
    internal var data: List<ConfigureDeviceData>? = null


    override fun init(findCargoResponse: FindCargoResponse?): BuilderView {
        for (value in findCargoResponse?.values ?: mutableListOf()) {
            Timber.w("Value: %s", value)
            this.values[value.id] = value
        }
        for (option in findCargoResponse?.options ?: mutableListOf()) {
            Timber.w("Option: %s", option)
            this.options[option.id] = option
        }

        Timber.e("Options Map: %s", options)
        Timber.e("Values Map: %s", values)

        return formBuilder
    }


    override fun validateTextBox(data: ConfigureDeviceData?): Boolean {
        val text = formBuilder.getTextBoxText(data)
        val empty = text.isEmpty()
        if (empty) {
            formBuilder.showTextBoxError(data)
        }
        return !empty
    }

    override fun getTextBoxValue(data: ConfigureDeviceData?): Value? {
        val text = formBuilder.getTextBoxText(data)
        val value = if (text.isNotEmpty()) Value(text) else null
        value?.id = data?.id
        return value
    }

    override fun validateButton(data: ConfigureDeviceData?): Boolean? {
        val number = try {
            formBuilder.getButtonNumber(data).toInt()
        } catch (e: Throwable) {
            0
        }
        if (number <= 0) { //if it is required, when no items are selected, selected item will be zero or less
            formBuilder.showButtonError(data)
            return false
        }

        return true
    }

    override fun validateSingleSelect(data: ConfigureDeviceData?): Boolean? {
        if (data?.options?.size ?: 0 > 0) {
            if (Constants.ITEM_TO_EXPAND < data?.options?.size ?: 0) {
                return true //since it is a spinner, an item is always selected
            } else {
                val item = formBuilder.getSingleSelectRVItem(data)
                if (item == null) {
                    formBuilder.showSingleSelectError(data)
                }
                return item != null;
            }
        }

        return true
    }

    override fun getSingleSelect(data: ConfigureDeviceData?): FormSelection? {
        val formSelection = if (Constants.ITEM_TO_EXPAND < data?.options?.size ?: 0) {
            val option = formBuilder.getSingleSelectSpinnerItem(data)
            val selectedValues = listOf(option?.id ?: 0)
            if (selectedValues.isNotEmpty())
                FormSelection(selectedValues)
            else null

        } else {
            val option = formBuilder.getSingleSelectRVItem(data)
            val selectedValues = listOf(option?.id ?: 0)
            if (selectedValues.isNotEmpty())
                FormSelection(selectedValues)
            else
                null
        }
        formSelection?.id = data?.id
        return formSelection
    }

    override fun validateQuickRemarkSelect(data: ConfigureDeviceData?): Boolean? {
        if (quickRemarks?.isNotEmpty() == true) {
            val items = if (Constants.ITEM_TO_EXPAND < quickRemarks?.size ?: 0) {
                formBuilder.getMultiSelectSpinnerItems(data)
            } else {
                formBuilder.getMultiSelectRVItems(data)
            }
            if (items.isEmpty()) {
                formBuilder.showMultiSelectError(data)
                return false
            }
        }
        return true
    }

    override fun getQuickRemarkSelect(data: ConfigureDeviceData?): FormSelection? {
        if (quickRemarks?.isNotEmpty() == true) {
            val items = if (Constants.ITEM_TO_EXPAND < quickRemarks?.size ?: 0) {
                formBuilder.getMultiSelectSpinnerItems(data)
            } else {
                formBuilder.getMultiSelectRVItems(data)
            }

            val formSelection = if (items.isNotEmpty())
                FormSelection(items)
            else
                null
            formSelection?.id = data?.id
            return formSelection
        }

        return null
    }

    override fun validateMultiSelect(data: ConfigureDeviceData?): Boolean? {
        if (data?.options?.size ?: 0 > 0) {
            val items = if (Constants.ITEM_TO_EXPAND < data?.options?.size ?: 0) {
                formBuilder.getMultiSelectSpinnerItems(data)
            } else {
                formBuilder.getMultiSelectRVItems(data)
            }

            if (items.isEmpty()) {
                formBuilder.showMultiSelectError(data)
                return false
            }

        }
        return true
    }

    override fun getMultiSelect(data: ConfigureDeviceData?): FormSelection? {
        if (data?.options?.isNotEmpty() == true) {
            val items = if (Constants.ITEM_TO_EXPAND < data.options.size) {
                formBuilder.getMultiSelectSpinnerItems(data)
            } else {
                formBuilder.getMultiSelectRVItems(data)
            }

            val formSelection = if (items.isNotEmpty())
                FormSelection(items)
            else
                null
            formSelection?.id = data.id
            return formSelection
        }

        return null
    }

    override fun validateCheckBox(data: ConfigureDeviceData?): Boolean? {
        val checked = formBuilder.getCheckBoxCheckedState(data)
        if (!checked) {
            formBuilder.showCheckBoxError(data)
            return false
        }
        return true
    }

    override fun getCheckBoxValue(data: ConfigureDeviceData?): Value {
        val checked = formBuilder.getCheckBoxCheckedState(data)
        val value = Value(checked.toString())
        value.id = data?.id
        return value
    }


    override fun initializeDefaultValue(data: ConfigureDeviceData?) {
        val value = Value("")
        value.id = data?.id
        values[data?.id] = value
    }

    override fun initializeDefaultOption(data: ConfigureDeviceData?) {
        val option = Option(null)
        option.id = data?.id
        options[data?.id] = option
    }

    override fun initializeValues() {
        for (value in values.values) {
            formBuilder.bindValuesDataToView(value)
        }
    }

    override fun initializeOptions() {
        for (option in options.values) {
            formBuilder.bindOptionsDataToView(option)
        }
    }


}