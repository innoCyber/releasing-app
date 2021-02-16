package ptml.releasing.form

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import ptml.releasing.form.base.BaseTest
import ptml.releasing.form.base.BuilderView
import ptml.releasing.form.models.FormConfiguration
import ptml.releasing.form.utils.*


class FormValidatorTest : BaseTest() {
    private val formBuilder: BuilderView = mockk()
    private val listener: FormValidator.ValidatorListener = mockk()

    @Test
    fun `form with all fields non-required is valid on validation`() {
        val data: List<FormConfiguration> = configureDeviceDataNonRequired
        val formValidator = FormValidator(formBuilder, data)
        formValidator.listener = listener
        val result = formValidator.validate()
        assertThat(result).isEqualTo(VALID)
    }


    @Test
    fun `form with filled required fields is valid on validation`() {
        val data: List<FormConfiguration> = configureDeviceData
        val formValidator = FormValidator(formBuilder, data)
        formValidator.listener = listener
        mockFormBuilderToFillAllFields()
        val result = formValidator.validate()
        assertThat(result).isEqualTo(VALID)
    }

    private fun mockFormBuilderToFillAllFields() {
        every { formBuilder.validateButton(any()) } returns true
        every { formBuilder.validateCheckBox(any()) } returns true
        every { formBuilder.validateMultiSelect(any()) } returns true
        every { formBuilder.validateQuickRemarkSelect(any()) } returns true
        every { formBuilder.validateSingleSelect(any()) } returns true
        every { formBuilder.validateTextBox(any()) } returns true
    }

    @Test
    fun `form with unfilled required fields is invalid on validation`() {
        val data: List<FormConfiguration> = configureDeviceDataSomeRequired
        val formValidator = FormValidator(formBuilder, data)
        formValidator.listener = listener
        mockFormBuilderToFillSomeFields()
        every { listener.onError() } returns Unit

        val result = formValidator.validate()

        assertThat(result).isEqualTo(INVALID)
    }


    private fun mockFormBuilderToFillSomeFields() {
        every { formBuilder.validateButton(any()) } returns false
        every { formBuilder.validateCheckBox(any()) } returns true
        every { formBuilder.validateMultiSelect(any()) } returns true
        every { formBuilder.validateQuickRemarkSelect(any()) } returns false
        every { formBuilder.validateSingleSelect(any()) } returns true
        every { formBuilder.validateTextBox(any()) } returns true
    }


    @Test
    fun `validation of invalid form type is ignored`() {
        val data: List<FormConfiguration> = configureDeviceDataWithInvalidFormType
        val formValidator = FormValidator(formBuilder, data)
        formValidator.listener = listener
        val result = formValidator.validate()
        assertThat(result).isEqualTo(VALID)
    }


    @Test
    fun `exception during validation is ignored`() {
        val data: List<FormConfiguration> = configureDeviceData
        val formValidator = FormValidator(formBuilder, data)
        mockFormBuilderToThrowException()
        val result = formValidator.validate()
        assertThat(result).isEqualTo(VALID)
    }

    private fun mockFormBuilderToThrowException() {
        every { formBuilder.validateButton(any()) } throws Exception("Error")
        every { formBuilder.validateCheckBox(any()) } throws Exception("Error")
        every { formBuilder.validateMultiSelect(any()) } throws Exception("Error")
        every { formBuilder.validateQuickRemarkSelect(any()) } throws Exception("Error")
        every { formBuilder.validateSingleSelect(any()) } throws Exception("Error")
        every { formBuilder.validateTextBox(any()) } throws Exception("Error")
    }


}