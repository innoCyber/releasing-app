package ptml.releasing.app.form

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import ptml.releasing.app.form.base.BuilderView
import ptml.releasing.base.BaseTest
import ptml.releasing.cargo_info.model.FormSelection
import ptml.releasing.cargo_search.model.Option
import ptml.releasing.cargo_search.model.Value
import ptml.releasing.data.*
import kotlin.test.assertNull


class FormBuilderPresenterTest : BaseTest() {

    private val formBuilder: BuilderView = mockk()

    private val presenter by lazy {
        FormBuilderPresenter(formBuilder)
    }

    @Test
    fun `should initialize quick remarks when provided with non-null value`() {
        assertNull(
            presenter.quickRemarks,
            "Quick remarks should be null before initialization"
        )

        presenter.initializeQuickRemarks(quickRemarks)

        assertThat(presenter.quickRemarks, `is`(quickRemarks))
    }

    @Test
    fun `should not initialize quick remarks when provided with null value`() {
        assertNull(
            presenter.quickRemarks,
            "Quick remarks should be null before initialization"
        )

        presenter.initializeQuickRemarks(null)

        assertNull(
            presenter.quickRemarks,
            "Quick remarks should be null after initialization with null value"
        )
    }


    @Test
    fun `should populate values when provided with non-null value`() {

        assertThat(presenter.values, `is`(emptyValues))

        presenter.init(findCargoResponse)

        val expectedValues = findCargoResponse.values?.associateBy ({it.id}, {it})
        assertThat(presenter.values, `is`(expectedValues))
    }

    @Test
    fun `should not populate values when provided with null value`() {

        assertThat(presenter.values, `is`(emptyValues))

        presenter.init(null)

        assertThat(presenter.values, `is`(emptyValues))
    }

    @Test
    fun `should not populate options when provided with null value`() {
        assertThat(presenter.options, `is`(emptyOptions))

        presenter.init(null)

        assertThat(presenter.options, `is`(emptyOptions))
    }


    @Test
    fun `should populate options when provided with non-null value`() {

        assertThat(presenter.options, `is`(emptyOptions))

        presenter.init(findCargoResponse)

        val expectedOptions = findCargoResponse.options?.associateBy ({it.id}, {it})
        assertThat(presenter.options, `is`(expectedOptions))
    }


    @Test
    fun `textbox input is valid when there is text`() {
        every {
            formBuilder.getTextBoxText(any())
        } returns TEXT_BOX_TITLE

        every {
            formBuilder.showTextBoxError(any())
        } returns Unit

        val data = getTextBoxData()

        val result = presenter.validateTextBox(data)
        assertThat(result, `is`(VALID))
    }


    @Test
    fun `textbox input is invalid when there is no text)`() {
        every {
            formBuilder.getTextBoxText(any())
        } returns ""

        every {
            formBuilder.showTextBoxError(any())
        } returns Unit

        val data = getTextBoxData()

        val result = presenter.validateTextBox(data)
        assertThat(result, `is`(INVALID))
    }


    @Test
    fun `textbox should return value with its text context`() {
        every {
            formBuilder.getTextBoxText(any())
        } returns TEXT_BOX_TITLE

        val data = getTextBoxData()
        val expectedValue = Value(TEXT_BOX_TITLE)
        expectedValue.id = data.id

        val result = presenter.getTextBoxValue(data)
        assertThat(result, `is`(expectedValue))
    }

    @Test
    fun `button data is valid when the button text is greater than 0`() {
        every {
            formBuilder.getButtonNumber(any())
        } returns BUTTON_NUMBER.toString()

        every {
            formBuilder.showButtonError(any())
        } returns Unit

        val data = buttonData()
        val result = presenter.validateButton(data)

        assertThat(result, `is`(VALID))
    }


    @Test
    fun `button data is invalid when the button text is 0`() {
        every {
            formBuilder.getButtonNumber(any())
        } returns 0.toString()

        every {
            formBuilder.showButtonError(any())
        } returns Unit

        val data = buttonData()
        val result = presenter.validateButton(data)

        assertThat(result, `is`(INVALID))
    }

    @Test
    fun `button data is invalid when the button text is not a number`() {
        every {
            formBuilder.getButtonNumber(any())
        } returns Double.NaN.toString()

        every {
            formBuilder.showButtonError(any())
        } returns Unit

        val data = buttonData()

        val result = presenter.validateButton(data)
        assertThat(result, `is`(INVALID))
    }


    @Test
    fun `single select data is ignored when there are no options on validation`() {
        every {
            formBuilder.getSingleSelectRVItem(any())
        } returns singleSelectOptions[SELECTED_POSITION]

        every {
            formBuilder.showSingleSelectError(any())
        } returns Unit

        val data = getSingleDataNoOptions()
        val result = presenter.validateSingleSelect(data)

        assertThat(result, `is`(VALID))
    }

    @Test
    fun `single select data is valid when the options are more than 6`() {
        every {
            formBuilder.getSingleSelectRVItem(any())
        } returns singleSelectOptions[SELECTED_POSITION]

        every {
            formBuilder.showSingleSelectError(any())
        } returns Unit

        val data = getSingleDataWithOptions()
        val result = presenter.validateSingleSelect(data)

        assertThat(result, `is`(VALID))
    }


    @Test
    fun `single select data is valid when the options are less than 6 and one item is selected`() {
        every {
            formBuilder.getSingleSelectRVItem(any())
        } returns singleSelectOptions[SELECTED_POSITION] //one selected

        every {
            formBuilder.showSingleSelectError(any())
        } returns Unit

        val data = getSingleDataOneOptions()
        val result = presenter.validateSingleSelect(data)

        assertThat(result, `is`(VALID))
    }


    @Test
    fun `single select data is invalid when the options are less than 6 and no items are selected`() {
        every {
            formBuilder.getSingleSelectRVItem(any())
        } returns null //none selected

        every {
            formBuilder.showSingleSelectError(any())
        } returns Unit

        val data = getSingleDataOneOptions()
        val result = presenter.validateSingleSelect(data)

        assertThat(result, `is`(INVALID))
    }


    @Test
    fun `single select selection is null when it has no items`() {
        val data = getSingleDataNoOptions()
        val result = presenter.getSingleSelect(data)

        assertNull(result, "Form selection is null when the data has no options")
    }


    @Test
    fun `single select selection is non-null with items less than 6 and one item selected`() {
        val options = singleSelectOptions[SELECTED_POSITION]
        every {
            formBuilder.getSingleSelectRVItem(any())
        } returns options

        val selectedOptions = listOf(options.id ?: 0)
        val expectedSelection = FormSelection(selectedOptions)
        val data = getSingleDataOneOptions()
        val result = presenter.getSingleSelect(data)

        assertThat(result, `is`(expectedSelection))
    }

    @Test
    fun `single select selection is null with items less than 6 and no selected item`() {
        every {
            formBuilder.getSingleSelectRVItem(any())
        } returns null //none selected

        val data = getSingleDataOneOptions()
        val result = presenter.getSingleSelect(data)

        assertNull(result)
    }

    @Test
    fun `single select selection is non-null with items more than 6 and one item selected`() {
        val options = singleSelectOptions[SELECTED_POSITION]
        every {
            formBuilder.getSingleSelectSpinnerItem(any())
        } returns options //one selected


        val selectedOptions = listOf(options.id ?: 0)
        val expectedSelection = FormSelection(selectedOptions)
        val data = getSingleDataWithOptions()
        val result = presenter.getSingleSelect(data)

        assertThat(result, `is`(expectedSelection))
    }

    @Test
    fun `single select selection is null with items more than 6 and no selected items`() {
        every {
            formBuilder.getSingleSelectSpinnerItem(any())
        } returns null //none selected

        val data = getSingleDataWithOptions()
        val result = presenter.getSingleSelect(data)

        assertNull(result)
    }

    @Test
    fun `quick remarks are ignored when they are empty on validation`() {
        val data = quickRemarkData
        val result = presenter.validateQuickRemarkSelect(data)

        assertThat(result, `is`(VALID))
    }


    @Test
    fun `quick remarks with at most 6 items are invalid when no items are selected on validation`() {

        every {
            formBuilder.getMultiSelectRVItems(any())
        } returns listOf()

        every {
            formBuilder.showMultiSelectError(any())
        } returns Unit


        val data = quickRemarkData
        presenter.quickRemarks = quickRemarks // set quick remarks
        val result = presenter.validateQuickRemarkSelect(data)

        assertThat( result, `is`(INVALID))
    }

    @Test
    fun `quick remarks with options more than 6 are invalid when no items are selected on validation`() {

        every {
            formBuilder.getMultiSelectSpinnerItems(any())
        } returns listOf()

        every {
            formBuilder.showMultiSelectError(any())
        } returns Unit


        val data = quickRemarkData
        presenter.quickRemarks = quickRemarks6 // set quick remarks
        val result = presenter.validateQuickRemarkSelect(data)

        assertThat(result, `is`(INVALID))
    }

    @Test
    fun `quick remarks with items less than 6 is valid when some items are selected`() {

        every {
            formBuilder.getMultiSelectRVItems(any())
        } returns multiSelectItemsList


        val data = quickRemarkData
        presenter.quickRemarks = quickRemarks // set quick remarks  to less than 6 items
        val result = presenter.validateQuickRemarkSelect(data)

        assertThat(result, `is`(VALID))
    }

    @Test
    fun `quick remarks with items more than 6 is valid when some items are selected`() {
        every {
            formBuilder.getMultiSelectSpinnerItems(any())
        } returns multiSelectItemsListAtLeast6

        val data = quickRemarkData
        presenter.quickRemarks = quickRemarks6  // set quick remarks to more than 6 items
        val result = presenter.validateQuickRemarkSelect(data)

        assertThat(result, `is`(VALID))
    }


    @Test
    fun `getting quick remarks data return null when the quick remark data is null`() {
        val data = quickRemarkData
        val result = presenter.getQuickRemarkSelect(data)
        assertNull(result, "Since no quick remark, null data should be returned")
    }


    @Test
    fun `getting quick remarks selection with items more than 6  and some items selected, returns the selection`() {
        every {
            formBuilder.getMultiSelectSpinnerItems(any())
        } returns multiSelectItemsListAtLeast6


        val data = quickRemarkData
        presenter.quickRemarks = quickRemarks6  // set quick remarks to more than 6 items
        val expected = FormSelection(multiSelectItemsListAtLeast6)
        val result = presenter.getQuickRemarkSelect(data)

        assertThat(result, `is`(expected))
    }


    @Test
    fun `getting quick remarks selection with items less than 6 and some items selected, returns the selection`() {
        every {
            formBuilder.getMultiSelectRVItems(any())
        } returns multiSelectItemsList


        val data = quickRemarkData
        presenter.quickRemarks = quickRemarks  // set quick remarks to more than 6 items
        val expected = FormSelection(multiSelectItemsList)
        val result = presenter.getQuickRemarkSelect(data)

        assertThat(result, `is`(expected))
    }


    @Test
    fun `multiselect is ignored when they are empty on validation`() {
        val data = provideMultiSelectDataNoOption()
        val result = presenter.validateMultiSelect(data)

        assertThat(result, `is`(VALID))
    }


    @Test
    fun `multiselect is invalid when no items are selected on validation`() {
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
        val result = presenter.validateMultiSelect(data)

        assertThat(result, `is`(INVALID))
    }

    @Test
    fun `multiselect with items less than 6 is valid when some items are selected`() {
        every {
            formBuilder.getMultiSelectSpinnerItems(any())
        } returns multiSelectItemsListAtLeast6

        val data = provideMultiSelectData()
        val result = presenter.validateMultiSelect(data)

        assertThat(result, `is`(VALID))
    }


    @Test
    fun `multiselect with items more than 6 is valid when some items are selected`() {
        every {
            formBuilder.getMultiSelectRVItems(any())
        } returns multiSelectItemsList


        val data = provideMultiSelectDataWithOneOption()
        val result = presenter.validateMultiSelect(data)

        assertThat(result, `is`(VALID))
    }


    @Test
    fun `getting multiselect data return null when the multiselect data is null`() {
        val data = provideMultiSelectDataNoOption()
        val result = presenter.getMultiSelect(data)

        assertNull(result, "Since no quick remark, null is returned")
    }


    @Test
    fun `getting multiselect selection with items more than 6  and some items selected, returns the selection`() {
        every {
            formBuilder.getMultiSelectSpinnerItems(any())
        } returns multiSelectItemsListAtLeast6

        val data = provideMultiSelectData()
        val expected = FormSelection(multiSelectItemsListAtLeast6)
        val result = presenter.getMultiSelect(data)

        assertThat(result, `is`(expected))
    }


    @Test
    fun `getting multiselect with items less than 6 and some items selected, returns the selection`() {
        every {
            formBuilder.getMultiSelectRVItems(any())
        } returns multiSelectItemsList


        val data = provideMultiSelectDataWithOneOption()
        val expected = FormSelection(multiSelectItemsList)
        val result = presenter.getMultiSelect(data)

        assertThat(result, `is`(expected))
    }


    @Test
    fun `a required check box is valid when checked on validation`() {
        every {
            formBuilder.getCheckBoxCheckedState(any())
        } returns true

        val result = presenter.validateCheckBox(provideCheckBoxData())

        assertThat(result, `is`(VALID))
    }

    @Test
    fun `a required checkbox is invalid when unchecked on validation`() {
        every {
            formBuilder.getCheckBoxCheckedState(any())
        } returns false

        every {
            formBuilder.showCheckBoxError(any())
        } returns Unit


        val result = presenter.validateCheckBox(provideCheckBoxData())

        assertThat(result, `is`(INVALID))
    }


    @Test
    fun `getting check box when checked return value with the state of the checkbox`() {
        val checked = true
        every {
            formBuilder.getCheckBoxCheckedState(any())
        } returns checked

        val data = provideCheckBoxData()
        val expected = Value(checked.toString())
        expected.id = data.id
        val result = presenter.getCheckBoxValue(data)

        assertThat(result, `is`(expected))
    }


    @Test
    fun `getting checkbox when unchecked return value with the state of the checkbox`() {
        val checked = false
        every {
            formBuilder.getCheckBoxCheckedState(any())
        } returns checked

        val data = provideCheckBoxData()
        val expected = Value(checked.toString())
        expected.id = data.id
        val result = presenter.getCheckBoxValue(data)

        assertThat(result, `is`(expected))
    }


    @Test
    fun `default value are initialized`() {
        val data = getTextBoxData()
        val expected = Value("")
        expected.id = data.id
        presenter.initializeDefaultValue(data)

        assertThat(presenter.values[data.id], `is`(expected))
    }

    @Test
    fun `default option are initialized`() {
        val data = getSingleDataWithOptions()
        val expected = Option(null)
        expected.id = data.id
        presenter.initializeDefaultOption(data)

        assertThat(presenter.options[data.id], `is`(expected))
    }


}