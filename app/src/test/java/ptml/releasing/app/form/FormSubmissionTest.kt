package ptml.releasing.app.form

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat

import org.junit.Test
import ptml.releasing.app.form.base.BuilderView
import ptml.releasing.app.utils.Constants
import ptml.releasing.base.BaseTest
import ptml.releasing.cargo_info.model.FormSelection
import ptml.releasing.cargo_search.model.Value
import ptml.releasing.data.configureDeviceData
import ptml.releasing.data.configureDeviceDataWithInvalidFormType
import ptml.releasing.download_damages.model.Damage
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FormSubmissionTest : BaseTest() {
    private val formBuilder: BuilderView = mockk()
    private val formValidator: FormValidator = mockk()


    @Test
    fun `submitting an invalid form does not create the submission list`() {
        val submission = FormSubmission(
            formBuilder,
            mockk(),
            formValidator
        )
        every {
            formValidator.validate()
        } returns false

        assertThat(submission.valuesList, `is`(emptyList()))
        assertThat(submission.damagesList, `is`(emptyList()))
        assertThat(submission.selectionList, `is`(emptyList()))
    }

    @Test
    fun `submit valid form creates the submission list`() {
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

        val expectedValueList = configureDeviceData.filter {
            it.type == Constants.CHECK_BOX || it.type == Constants.MULTI_LINE_TEXTBOX || it.type == Constants.TEXT_BOX
        }

        val expectedOptionList = configureDeviceData.filter {
            it.type == Constants.SINGLE_SELECT || it.type == Constants.MULTI_SELECT || it.type == Constants.QUICK_REMARKS
        }

        assertThat(submission.valuesList.size, `is`(expectedValueList.size))
        assertThat(submission.selectionList.size, `is`(expectedOptionList.size))
        assertThat(submission.damagesList.size, `is`(emptyList<Damage>().size))
    }

    private fun mockSubmitFormForAllFields() {
        every { formBuilder.getCheckBoxValue(any()) } returns Value("")
        every { formBuilder.getTextBoxValue(any()) } returns Value("")
        every { formBuilder.getSingleSelect(any()) } returns FormSelection(listOf())
        every { formBuilder.getMultiSelect(any()) } returns FormSelection(listOf())
        every { formBuilder.getQuickRemarkSelect(any()) } returns FormSelection(listOf())
    }


    @Test
    fun `submitting form with invalid type ignores the invalid form type and creates submission list`() {
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

        val expectedValueList = configureDeviceData.filter {
            it.type == Constants.CHECK_BOX || it.type == Constants.MULTI_LINE_TEXTBOX || it.type == Constants.TEXT_BOX
        }

        val expectedOptionList = configureDeviceData.filter {
            it.type == Constants.SINGLE_SELECT || it.type == Constants.MULTI_SELECT || it.type == Constants.QUICK_REMARKS
        }

        assertThat(submission.valuesList.size, `is`(expectedValueList.size))
        assertThat(submission.selectionList.size, `is`(expectedOptionList.size))
        assertThat(submission.damagesList.size, `is`(emptyList<Damage>().size))

    }

    @Test
    fun `submitting when exception occurs catches the exception and creates submission list`() {
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

        val expectedValueList = configureDeviceData.filter {
            it.type == Constants.MULTI_LINE_TEXTBOX || it.type == Constants.TEXT_BOX
        }

        val expectedOptionList = configureDeviceData.filter {
            it.type == Constants.MULTI_SELECT
        }

        assertThat(submission.valuesList.size, `is`(expectedValueList.size))
        assertThat(submission.selectionList.size, `is`(expectedOptionList.size))
        assertThat(submission.damagesList.size, `is`(emptyList<Damage>().size))
    }


    private fun mockSubmitFormThrowException() {
        every { formBuilder.getCheckBoxValue(any()) } throws Exception("Error")
        every { formBuilder.getTextBoxValue(any()) } returns Value("")
        every { formBuilder.getSingleSelect(any()) } throws Exception("Error")
        every { formBuilder.getMultiSelect(any()) } returns FormSelection(listOf())
        every { formBuilder.getQuickRemarkSelect(any()) } throws Exception("Error")
    }

}