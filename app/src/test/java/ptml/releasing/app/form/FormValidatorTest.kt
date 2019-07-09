package ptml.releasing.app.form

import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

import org.junit.Test
import ptml.releasing.app.form.base.BuilderView
import ptml.releasing.base.BaseTest
import ptml.releasing.configuration.models.ConfigureDeviceData
import ptml.releasing.data.*
import java.lang.Exception
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FormValidatorTest : BaseTest(){
    val formBuilder : BuilderView  = mockk()
    val listener: FormValidator.ValidatorListener = mockk()


    @Test
    fun `form with all fields non-required is valid on validation`(){
        val data: List<ConfigureDeviceData>  = configureDeviceDataNonRequired
        val formValidator = FormValidator(formBuilder, data)
        formValidator.listener = listener
        val result = formValidator.validate()
        assertThat( "Form should be valid since no fields are required", result, `is`(VALID))
    }


    @Test
    fun `form with filled required fields is valid on validation`(){
        val data: List<ConfigureDeviceData>  = configureDeviceData
        val formValidator = FormValidator(formBuilder, data)
        formValidator.listener = listener
        mockFormBuilderToFillAllFields()
        val result  = formValidator.validate()
        assertThat("Form validation should be true since  required fields are filled", result, `is`(VALID))
    }

    private fun  mockFormBuilderToFillAllFields(){
        every { formBuilder.validateButton(any()) }returns true
        every { formBuilder.validateCheckBox(any()) }returns true
        every { formBuilder.validateMultiSelect(any()) }returns true
        every { formBuilder.validateQuickRemarkSelect(any()) }returns true
        every { formBuilder.validateSingleSelect(any()) }returns true
        every { formBuilder.validateTextBox(any()) }returns true
    }

    @Test
    fun `form with unfilled required fields is invalid on validation`(){
        val data: List<ConfigureDeviceData>  = configureDeviceDataSomeRequired
        val formValidator = FormValidator(formBuilder, data)
        formValidator.listener = listener
        mockFormBuilderToFillSomeFields()
        every { listener.onError() }returns Unit

        val result = formValidator.validate()

        assertThat("Form validation should be false since not all required fields are filled", result, `is`(INVALID))
    }


    private fun  mockFormBuilderToFillSomeFields(){
        every { formBuilder.validateButton(any()) }returns false
        every { formBuilder.validateCheckBox(any()) }returns true
        every { formBuilder.validateMultiSelect(any()) }returns true
        every { formBuilder.validateQuickRemarkSelect(any()) }returns false
        every { formBuilder.validateSingleSelect(any()) }returns true
        every { formBuilder.validateTextBox(any()) }returns true
    }


    @Test
    fun `validation of invalid form type is ignored`(){
        val data: List<ConfigureDeviceData>  = configureDeviceDataWithInvalidFormType
        val formValidator = FormValidator(formBuilder, data)
        formValidator.listener = listener
        val result = formValidator.validate()
        assertThat("Invalid form types are ignored so validation should be true", result, `is`(VALID))
    }


    @Test
    fun `exception during validation is ignored`(){
        val data: List<ConfigureDeviceData>  = configureDeviceData
        val formValidator = FormValidator(formBuilder, data)
        mockFormBuilderToThrowException()
        val result = formValidator.validate()
        assertThat("exceptions ignored so validation should be true", result, `is`(VALID))
    }

    private fun  mockFormBuilderToThrowException(){
        every { formBuilder.validateButton(any()) }throws Exception("Error")
        every { formBuilder.validateCheckBox(any()) }throws Exception("Error")
        every { formBuilder.validateMultiSelect(any()) }throws Exception("Error")
        every { formBuilder.validateQuickRemarkSelect(any()) }throws Exception("Error")
        every { formBuilder.validateSingleSelect(any()) }throws Exception("Error")
        every { formBuilder.validateTextBox(any()) }throws Exception("Error")
    }



}