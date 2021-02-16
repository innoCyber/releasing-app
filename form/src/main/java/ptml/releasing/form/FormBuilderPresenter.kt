package ptml.releasing.form

import androidx.annotation.VisibleForTesting
import ptml.releasing.form.base.BuilderPresenter
import ptml.releasing.form.base.BuilderView
import ptml.releasing.form.models.*
import ptml.releasing.form.utils.Constants
import timber.log.Timber

class FormBuilderPresenter(private val formBuilder: BuilderView) :
    BuilderPresenter {

    @VisibleForTesting
    var values = mutableMapOf<Int?, Value>()

    @VisibleForTesting
    var options = mutableMapOf<Int?, Option>()

    @VisibleForTesting
    var quickRemarks: Map<Int, QuickRemark>? = null
    var voyagesMap: Map<Int, Voyage>? = null

    override fun initializeQuickRemarks(quickRemarks: Map<Int, QuickRemark>?) {
        this.quickRemarks = quickRemarks
    }

    override fun initializeVoyages(voyages: Map<Int, Voyage>?) {
        this.voyagesMap = voyages
    }

    override fun init(findCargoResponse: FormPreFillResponse?): BuilderView {
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


    override fun validateTextBox(data: FormConfiguration?): Boolean {
        val text = formBuilder.getTextBoxText(data)
        val empty = text.isEmpty()
        if (empty) {
            formBuilder.showTextBoxError(data)
        }
        return !empty
    }

    override fun getTextBoxValue(data: FormConfiguration?): Value? {
        val text = formBuilder.getTextBoxText(data)
        val value = if (text.isNotEmpty()) Value(text) else null
        value?.id = data?.id
        return value
    }

    override fun validateButton(data: FormConfiguration?): Boolean? {
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

    override fun validateSingleSelect(data: FormConfiguration?): Boolean? {
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

    override fun getSingleSelect(data: FormConfiguration?): FormSelection? {
        if (data?.options?.isNotEmpty() == true) {
            val formSelection = if (Constants.ITEM_TO_EXPAND < data.options.size) {
                val option = formBuilder.getSingleSelectSpinnerItem(data)
                val selectedValues = if (option != null) listOf(option.id ?: 0) else listOf()
                if (selectedValues.isNotEmpty())
                    FormSelection(selectedValues)
                else null

            } else {
                val option = formBuilder.getSingleSelectRVItem(data)
                val selectedValues = if (option != null) listOf(option.id ?: 0) else listOf()
                if (selectedValues.isNotEmpty())
                    FormSelection(selectedValues)
                else
                    null
            }
            formSelection?.id = data.id
            return formSelection
        }

        return null
    }

    override fun validateQuickRemarkSelect(data: FormConfiguration?): Boolean? {
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

    override fun getQuickRemarkSelect(data: FormConfiguration?): FormSelection? {
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

    override fun validateVoyagesSelect(data: FormConfiguration?): Boolean? {
        if (voyagesMap?.isNotEmpty() == true) {
            return if (Constants.ITEM_TO_EXPAND < voyagesMap?.size ?: 0) {
                true //since it is a spinner, an item is always selected
            } else {
                val item = formBuilder.getVoyageSelectRVItem(data)
                if (item == null) {
                    formBuilder.showVoyageError(data)
                }
                item != null;
            }
        }
        return true
    }

    override fun getVoyagesSelect(data: FormConfiguration?): Voyage? {
        if (voyagesMap?.isNotEmpty() == true) {
            return if (Constants.ITEM_TO_EXPAND < voyagesMap?.size ?: 0) {
                val voyage = formBuilder.getVoyageSelectSpinnerItem(data)
                Timber.d("Gotten voyage: $voyage")
                voyage

            } else {
                val voyage = formBuilder.getVoyageSelectRVItem(data)
                Timber.d("Gotten voyage: $voyage")
                voyage
            }
        }
        return null
    }

    override fun validateMultiSelect(data: FormConfiguration?): Boolean? {
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

    override fun getMultiSelect(data: FormConfiguration?): FormSelection? {
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

    override fun validateCheckBox(data: FormConfiguration?): Boolean? {
        val checked = formBuilder.getCheckBoxCheckedState(data)
        if (!checked) {
            formBuilder.showCheckBoxError(data)
            return false
        }
        return true
    }

    override fun getCheckBoxValue(data: FormConfiguration?): Value {
        val checked = formBuilder.getCheckBoxCheckedState(data)
        val value = Value(checked.toString())
        value.id = data?.id
        return value
    }


    override fun initializeDefaultValue(data: FormConfiguration?) {
        val value = Value("")
        value.id = data?.id
        values[data?.id] = value
    }

    override fun initializeDefaultOption(data: FormConfiguration?) {
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