package ptml.releasing.app.form

import io.mockk.every
import io.mockk.mockk

import org.junit.Test
import ptml.releasing.app.form.base.BuilderView
import ptml.releasing.app.utils.Constants
import ptml.releasing.base.BaseTest
import ptml.releasing.cargo_info.model.FormSelection
import ptml.releasing.cargo_search.model.Value
import ptml.releasing.data.configureDeviceData
import ptml.releasing.data.configureDeviceDataWithInvalidFormType
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormSubmissionTest : BaseTest() {
    private val formBuilder: BuilderView = mockk()
    private val formValidator: FormValidator = mockk()


    @Test
    fun `attempt to submit an invalid form`() {
        val submission = FormSubmission(
            formBuilder,
            mockk(),
            formValidator
        )
        every {
            formValidator.validate()
        } returns false

        assertTrue(submission.valuesList.isEmpty(), "No values are added")
        assertTrue(submission.damagesList.isEmpty(), "No damages are added")
        assertTrue(submission.selectionList.isEmpty(), "No selections are added")
    }

    @Test
    fun `submit form successfully`() {
        val submission = FormSubmission(
            formBuilder,
            configureDeviceData,
            formValidator
        )


        mockSubmitFormForAllFields()

        every {
            formValidator.validate()
        } returns true

        submission.submit()

        val valuesSize = configureDeviceData.filter {
            it.type == Constants.CHECK_BOX || it.type == Constants.MULTI_LINE_TEXTBOX || it.type == Constants.TEXT_BOX
        }.size

        val optionsSize = configureDeviceData.filter {
            it.type == Constants.SINGLE_SELECT || it.type == Constants.MULTI_SELECT || it.type == Constants.QUICK_REMARKS
        }.size

        assertEquals(valuesSize, submission.valuesList.size, "Values list should have the same size")
        assertEquals(optionsSize, submission.selectionList.size, "Selection list should have the same size")
        assertTrue(submission.damagesList.isEmpty(), "Damages list should  be empty")

    }

    private fun mockSubmitFormForAllFields(){
        every {formBuilder.getCheckBoxValue(any()) }returns Value("")
        every {formBuilder.getTextBoxValue(any()) }returns Value("")
        every {formBuilder.getSingleSelect(any()) }returns FormSelection(listOf())
        every {formBuilder.getMultiSelect(any()) }returns FormSelection(listOf())
        every {formBuilder.getQuickRemarkSelect(any()) }returns FormSelection(listOf())
    }


    @Test
    fun `attempt to submit form with invalid type`() {
        val submission = FormSubmission(
            formBuilder,
            configureDeviceDataWithInvalidFormType,
            formValidator
        )
        every {
            formValidator.validate()
        } returns true

        mockSubmitFormForAllFields()

        submission.submit()

        val valuesSize = configureDeviceData.filter {
            it.type == Constants.CHECK_BOX || it.type == Constants.MULTI_LINE_TEXTBOX || it.type == Constants.TEXT_BOX
        }.size

        val optionsSize = configureDeviceData.filter {
            it.type == Constants.SINGLE_SELECT || it.type == Constants.MULTI_SELECT || it.type == Constants.QUICK_REMARKS
        }.size

        assertEquals(valuesSize, submission.valuesList.size, "Values list should have the same size")
        assertEquals(optionsSize, submission.selectionList.size, "Selection list should have the same size")
        assertTrue(submission.damagesList.isEmpty(), "No damages are added")

    }

    @Test
    fun `submit form  fail with exception`() {
        val submission = FormSubmission(
            formBuilder,
            configureDeviceData,
            formValidator
        )
        mockSubmitFormThrowException()
        every {
            formValidator.validate()
        } returns true


        submission.submit()

        val valuesSize = configureDeviceData.filter {
             it.type == Constants.MULTI_LINE_TEXTBOX || it.type == Constants.TEXT_BOX
        }.size

        val optionsSize = configureDeviceData.filter {
            it.type == Constants.MULTI_SELECT
        }.size

        assertEquals(valuesSize, submission.valuesList.size, "Values list should have the same size")
        assertEquals(optionsSize, submission.selectionList.size, "Selection list should have the same size")
        assertTrue(submission.damagesList.isEmpty(), "No damages are added")
    }


    private fun mockSubmitFormThrowException(){
        every {formBuilder.getCheckBoxValue(any()) }throws Exception("Error")
        every {formBuilder.getTextBoxValue(any()) }returns Value("")
        every {formBuilder.getSingleSelect(any()) }throws Exception("Error")
        every {formBuilder.getMultiSelect(any()) }returns FormSelection(listOf())
        every {formBuilder.getQuickRemarkSelect(any()) }throws Exception("Error")
    }

}