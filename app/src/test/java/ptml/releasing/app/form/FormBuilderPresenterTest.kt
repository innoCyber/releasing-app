package ptml.releasing.app.form

import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import ptml.releasing.app.form.base.BuilderView
import ptml.releasing.base.BaseTest
import ptml.releasing.cargo_info.model.FormSelection
import ptml.releasing.cargo_search.model.Option
import ptml.releasing.cargo_search.model.Value
import ptml.releasing.data.*
import kotlin.test.assertEquals

class FormBuilderPresenterTest : BaseTest() {

    private val formBuilder: BuilderView = mockk()

    private val presenter by lazy {
        FormBuilderPresenter(formBuilder)
    }

    @Test
    fun setQuickRemarks() {

        assertEquals(
            null, presenter.quickRemarks,
            "Quick remarks are null before calling provideQuickRemarks"
        )

        presenter.provideQuickRemarks(quickRemarks)

        assertEquals(
            quickRemarks.size, presenter.quickRemarks?.size,
            "Quick remarks have been set"
        )

    }

    @Test
    fun initializeValuesAndOptions() {
        assertEquals(
            0, presenter.values.size,
            "No values  before calling init"
        )

        assertEquals(
            0, presenter.options.size,
            "No options  before calling init"
        )

        presenter.init(findCargoResponse)


        assertEquals(
            findCargoResponse.values.size, presenter.values.size,
            "Value size is the same "
        )

        assertEquals(
            findCargoResponse.options.size, presenter.options.size,
            "options size is the same"
        )

    }


    @Test
    fun `validate textbox (there is text in the textbox)`() {

        every {
            formBuilder.getTextBoxText(any())
        } returns TEXT_BOX_TITLE

        every {
            formBuilder.showTextBoxError(any())
        } returns Unit

        val data = getTextBoxData()

        val valid = presenter.validateTextBox(data)

        assertEquals(true, valid, "Textbox has some text")
    }


    @Test
    fun `validate textbox (there is no text in the textbox)`() {

        every {
            formBuilder.getTextBoxText(any())
        } returns ""

        every {
            formBuilder.showTextBoxError(any())
        } returns Unit

        val data = getTextBoxData()

        val valid = presenter.validateTextBox(data)

        assertEquals(false, valid, "Textbox has some text")
    }


    @Test
    fun `get textbox value`() {
        every {
            formBuilder.getTextBoxText(any())
        } returns TEXT_BOX_TITLE

        val data = getTextBoxData()
        val expectedValue = Value(TEXT_BOX_TITLE)
        expectedValue.id = data.id

        val value = presenter.getTextBoxValue(data)

        assertEquals(
            expectedValue,
            value,
            "Textbox value matches"
        )
    }

    @Test
    fun `validate button (with at least one number)`() {
        every {
            formBuilder.getButtonNumber(any())
        } returns BUTTON_NUMBER.toString()

        every {
            formBuilder.showButtonError(any())
        } returns Unit

        val data = buttonData()
        val valid = presenter.validateButton(data)

        assertEquals(true, valid)
    }


    @Test
    fun `validate button (with at 0 number)`() {
        every {
            formBuilder.getButtonNumber(any())
        } returns 0.toString()

        every {
            formBuilder.showButtonError(any())
        } returns Unit

        val data = buttonData()
        val valid = presenter.validateButton(data)

        assertEquals(false, valid)
    }

    @Test
    fun `validate button (with at a non-number)`() {
        every {
            formBuilder.getButtonNumber(any())
        } returns "hello"

        every {
            formBuilder.showButtonError(any())
        } returns Unit

        val data = buttonData()
        val valid = presenter.validateButton(data)

        assertEquals(false, valid)
    }


    @Test
    fun `validate single select (with no options)`() {
        val options =
            if (singleSelectOptions.isNotEmpty()) singleSelectOptions[0] else optionsSample
        every {
            formBuilder.getSingleSelectRVItem(any())
        } returns options

        every {
            formBuilder.showSingleSelectError(any())
        } returns Unit

        val data = getSingleDataNoOptions()
        val valid = presenter.validateSingleSelect(data)
        assertEquals(true, valid, "With no options, single select validation is true")
    }

    @Test
    fun `validate single select (with options more than 6)`() {
        val options =
            if (singleSelectOptions.isNotEmpty()) singleSelectOptions[0] else optionsSample
        every {
            formBuilder.getSingleSelectRVItem(any())
        } returns options

        every {
            formBuilder.showSingleSelectError(any())
        } returns Unit

        val data = getSingleDataWithOptions()
        val valid = presenter.validateSingleSelect(data)
        assertEquals(
            true,
            valid,
            "With options more than 6, a spinner is shown so, single select validation is true"
        )

    }


    @Test
    fun `validate single select (with options less than 6 and one selected)`() {
        val options =
            if (singleSelectOptions.isNotEmpty()) singleSelectOptions[0] else optionsSample
        every {
            formBuilder.getSingleSelectRVItem(any())
        } returns options //one selected

        every {
            formBuilder.showSingleSelectError(any())
        } returns Unit

        val data = getSingleDataOneOptions()
        val valid = presenter.validateSingleSelect(data)
        assertEquals(
            true,
            valid,
            "With options less than 6, a recycler view is shown, with one selected, single select validation is true"
        )

    }


    @Test
    fun `validate single select (with options less than 6 and none selected)`() {
        every {
            formBuilder.getSingleSelectRVItem(any())
        } returns null //none selected

        every {
            formBuilder.showSingleSelectError(any())
        } returns Unit

        val data = getSingleDataOneOptions()
        val valid = presenter.validateSingleSelect(data)
        assertEquals(
            false,
            valid,
            "With options less than 6, a recycler view is shown, with none selected, single select validation is false"
        )
    }


    @Test
    fun `get single select (with no options)`() {
        val data = getSingleDataNoOptions()
        val result = presenter.getSingleSelect(data)
        assertEquals(null, result, "Form selection is null since the data has no options")
    }


    @Test
    fun `get single select (with options less than 6)`() {
        val options =
            if (singleSelectOptions.isNotEmpty()) singleSelectOptions[0] else optionsSample
        every {
            formBuilder.getSingleSelectRVItem(any())
        } returns options

        val selectedOptions = listOf(options.id ?: 0)
        val expectedSelection = FormSelection(selectedOptions)
        val data = getSingleDataOneOptions()
        val result = presenter.getSingleSelect(data)
        assertEquals(expectedSelection, result, "Form selection matches")
    }

    @Test
    fun `get single select (with options less than 6 and no selected option)`() {
        every {
            formBuilder.getSingleSelectRVItem(any())
        } returns null

        val data = getSingleDataOneOptions()
        val result = presenter.getSingleSelect(data)
        assertEquals(null, result, "Form selection matches")
    }

    @Test
    fun `get single select (with options more than 6)`() {

        val options =
            if (singleSelectOptions.isNotEmpty()) singleSelectOptions[0] else optionsSample
        every {
            formBuilder.getSingleSelectSpinnerItem(any())
        } returns options //one selected


        val selectedOptions = listOf(options.id ?: 0)
        val expectedSelection = FormSelection(selectedOptions)
        val data = getSingleDataWithOptions()
        val result = presenter.getSingleSelect(data)
        assertEquals(expectedSelection, result, "Form selection matches")
    }

    @Test
    fun `get single select (with options more than 6 and no selected option)`() {

        every {
            formBuilder.getSingleSelectSpinnerItem(any())
        } returns null //one selected


        val data = getSingleDataWithOptions()
        val result = presenter.getSingleSelect(data)
        assertEquals(null, result, "Form selection matches")
    }

    @Test
    fun `validate quick remarks when items are empty`() {
        val data = quickRemarkData
        val valid = presenter.validateQuickRemarkSelect(data)
        assertEquals(true, valid)
    }


    @Test
    fun `validate quick remarks with no items`() {
        every {
            formBuilder.getMultiSelectSpinnerItems(any())
        } returns listOf()

        every {
            formBuilder.getMultiSelectRVItems(any())
        } returns listOf()

        every {
            formBuilder.showMultiSelectError(any())
        } returns Unit


        val data = quickRemarkData
        presenter.quickRemarks = quickRemarks6 // set quick remarks
        val valid = presenter.validateQuickRemarkSelect(data)

        assertEquals(false, valid, "Validation is false when no items are selected")
    }

    @Test
    fun `validate quick remarks with items more than 6`() {
        every {
            formBuilder.getMultiSelectSpinnerItems(any())
        } returns multiSelectItemsList6


        val data = quickRemarkData
        presenter.quickRemarks = quickRemarks6  // set quick remarks to more than 6 items
        val valid = presenter.validateQuickRemarkSelect(data)

        assertEquals(true, valid, "Validation is true since items are selected")
    }


    @Test
    fun `validate quick remarks with items less than 6`() {

        every {
            formBuilder.getMultiSelectRVItems(any())
        } returns multiSelectItemsList


        val data = quickRemarkData
        presenter.quickRemarks = quickRemarks // set quick remarks  to less than 6 items
        val valid = presenter.validateQuickRemarkSelect(data)
        assertEquals(true, valid, "Validation is true since items are selected")
    }


    @Test
    fun `get quick remarks with no items`() {
        val data = quickRemarkData
        val valid = presenter.getQuickRemarkSelect(data)
        assertEquals(null, valid, "Since no quick remark, null is returned")
    }


    @Test
    fun `get quick remarks with items more than 6`() {
        every {
            formBuilder.getMultiSelectSpinnerItems(any())
        } returns multiSelectItemsList6


        val data = quickRemarkData
        presenter.quickRemarks = quickRemarks6  // set quick remarks to more than 6 items
        val expected = FormSelection(multiSelectItemsList6)
        val actual = presenter.getQuickRemarkSelect(data)

        assertEquals(expected, actual, "Form selection matches")
    }


    @Test
    fun `get quick remarks with items less than 6`() {
        every {
            formBuilder.getMultiSelectRVItems(any())
        } returns multiSelectItemsList


        val data = quickRemarkData
        presenter.quickRemarks = quickRemarks  // set quick remarks to more than 6 items
        val expected = FormSelection(multiSelectItemsList)
        val actual = presenter.getQuickRemarkSelect(data)

        assertEquals(expected, actual, "Form selection matches")
    }


    @Test
    fun `validate multiselect when options empty`() {
        val data = provideMultiSelectDataNoOption()
        val valid = presenter.validateMultiSelect(data)
        assertEquals(true, valid)
    }


    @Test
    fun `validate multiselect with no items`() {
        every {
            formBuilder.getMultiSelectSpinnerItems(any())
        } returns listOf()

        every {
            formBuilder.getMultiSelectRVItems(any())
        } returns listOf()

        every {
            formBuilder.showMultiSelectError(any())
        } returns Unit

        val data = provideMultiSelectData()
        val valid = presenter.validateMultiSelect(data)

        assertEquals(false, valid, "Validation is false when no items are selected")
    }

    @Test
    fun `validate multiselect with items more than 6`() {
        every {
            formBuilder.getMultiSelectSpinnerItems(any())
        } returns multiSelectItemsList6

        val data = provideMultiSelectData()
        val valid = presenter.validateMultiSelect(data)

        assertEquals(true, valid, "Validation is true since items are selected")
    }


    @Test
    fun `validate multiselect with items less than 6`() {

        every {
            formBuilder.getMultiSelectRVItems(any())
        } returns multiSelectItemsList


        val data = provideMultiSelectDataWithOneOption()
        val valid = presenter.validateMultiSelect(data)
        assertEquals(true, valid, "Validation is true since items are selected")
    }


    @Test
    fun `get multiselect with no items`() {
        val data = provideMultiSelectDataNoOption()
        val valid = presenter.getMultiSelect(data)
        assertEquals(null, valid, "Since no quick remark, null is returned")
    }


    @Test
    fun `get multiselect with items more than 6`() {
        every {
            formBuilder.getMultiSelectSpinnerItems(any())
        } returns multiSelectItemsList6


        val data = provideMultiSelectData()
        val expected = FormSelection(multiSelectItemsList6)
        val actual = presenter.getMultiSelect(data)

        assertEquals(expected, actual, "Form selection matches")
    }


    @Test
    fun `get multiselect with items less than 6`() {
        every {
            formBuilder.getMultiSelectRVItems(any())
        } returns multiSelectItemsList


        val data = provideMultiSelectDataWithOneOption()
        val expected = FormSelection(multiSelectItemsList)
        val actual = presenter.getMultiSelect(data)

        assertEquals(expected, actual, "Form selection matches")
    }


    @Test
    fun `validate check box when checked`() {
        every {
            formBuilder.getCheckBoxCheckedState(any())
        } returns true

        val result = presenter.validateCheckBox(provideCheckBoxData())

        assertEquals(true, result, "Result should be true, checkbox is checked")
    }

    @Test
    fun `validate checkbox when unchecked`() {
        every {
            formBuilder.getCheckBoxCheckedState(any())
        } returns false

        every {
            formBuilder.showCheckBoxError(any())
        } returns Unit


        val result = presenter.validateCheckBox(provideCheckBoxData())

        assertEquals(false, result, "Result should be true, checkbox is checked")
    }


    @Test
    fun `get check box when checked`() {
        val checked = true
        every {
            formBuilder.getCheckBoxCheckedState(any())
        } returns checked

        val data = provideCheckBoxData()
        val expected = Value(checked.toString())
        expected.id = data.id
        val result = presenter.getCheckBoxValue(data)

        assertEquals(expected, result, "Result should match $expected")
    }


    @Test
    fun `get checkbox when unchecked`() {
        val checked = false
        every {
            formBuilder.getCheckBoxCheckedState(any())
        } returns checked

        val data = provideCheckBoxData()
        val expected = Value(checked.toString())
        expected.id = data.id
        val result = presenter.getCheckBoxValue(data)

        assertEquals(expected, result, "Result should match $expected")
    }


    @Test
    fun `initialize default value`() {
        val data = getTextBoxData()
        val expected = Value("")
        expected.id = data.id
        presenter.initializeDefaultValue(data)
        assertEquals(expected, presenter.values[data.id], "The values should be equal")
    }

    @Test
    fun `initialize default option`() {
        val data = getSingleDataWithOptions()
        val expected = Option(null)
        expected.id = data.id
        presenter.initializeDefaultOption(data)
        assertEquals(expected, presenter.options[data.id], "The options should be equal")
    }

    @Test
    fun `initialize values`() {

    }


    @Test
    fun `initialize options`() {

    }


}