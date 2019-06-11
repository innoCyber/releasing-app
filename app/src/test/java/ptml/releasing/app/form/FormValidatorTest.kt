package ptml.releasing.app.form

import io.mockk.every
import io.mockk.mockk

import org.junit.Test
import ptml.releasing.app.form.base.BuilderView
import ptml.releasing.base.BaseTest
import ptml.releasing.configuration.models.ConfigureDeviceData
import ptml.releasing.data.configureDeviceData
import ptml.releasing.data.configureDeviceDataNonRequired
import ptml.releasing.data.configureDeviceDataSomeRequired
import ptml.releasing.data.configureDeviceDataWithInvalidFormType
import java.lang.Exception
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FormValidatorTest : BaseTest(){
    val formBuilder : BuilderView  = mockk()
    val listener: FormValidator.ValidatorListener = mockk()


    @Test
    fun `validate form with all fields non-required`(){
        val data: List<ConfigureDeviceData>  = configureDeviceDataNonRequired
        val formValidator = FormValidator(formBuilder, data)
        formValidator.listener = listener
        val result = formValidator.validate()
        assertTrue(result, "Form should be valid since no fields are required")
    }


    @Test
    fun `validate form with all fields required`(){
        val data: List<ConfigureDeviceData>  = configureDeviceData
        val formValidator = FormValidator(formBuilder, data)
        formValidator.listener = listener
        mockFormBuilderToFillAllFields()
        val result  = formValidator.validate()
        assertEquals(true, result, "Form validation should be true since  required fields are filled")
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
    fun `validate form with required fields and some fields are not filled`(){
        val data: List<ConfigureDeviceData>  = configureDeviceDataSomeRequired
        val formValidator = FormValidator(formBuilder, data)
        formValidator.listener = listener
        mockFormBuilderToFillSomeFields()
        every { listener.onError() }returns Unit

        val result = formValidator.validate()

        assertFalse(result, "Form validation should be false since not all required fields are filled")
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
    fun `attempt to validate invalid form type`(){
        val data: List<ConfigureDeviceData>  = configureDeviceDataWithInvalidFormType
        val formValidator = FormValidator(formBuilder, data)
        formValidator.listener = listener
        val result = formValidator.validate()
        assertTrue(result, "Invalid form types are ignored so validation should be true")
    }


    @Test
    fun `attempt to validate but exception occurs`(){
        val data: List<ConfigureDeviceData>  = configureDeviceData
        val formValidator = FormValidator(formBuilder, data)
        mockFormBuilderToThrowException()
        val result = formValidator.validate()
        assertTrue(result, "exceptions ignored so validation should be true")
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