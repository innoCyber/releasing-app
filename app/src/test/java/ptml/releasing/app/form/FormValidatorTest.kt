package ptml.releasing.app.form

import io.mockk.every
import io.mockk.mockk

import org.junit.Test
import ptml.releasing.app.form.base.BuilderView
import ptml.releasing.configuration.models.ConfigureDeviceData
import ptml.releasing.data.configureDeviceData
import ptml.releasing.data.configureDeviceDataNonRequired
import ptml.releasing.data.configureDeviceDataSomeRequired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FormValidatorTest{
    val formBuilder : BuilderView  = mockk()


    @Test
    fun `validate form with all fields non-required`(){
        val data: List<ConfigureDeviceData>  = configureDeviceDataNonRequired
        val formValidator = FormValidator(formBuilder, data)
        val result = formValidator.validate()
        assertTrue(result, "Form should be valid since no fields are required")
    }


    @Test
    fun `validate form with all fields required`(){
        val data: List<ConfigureDeviceData>  = configureDeviceData
        val formValidator = FormValidator(formBuilder, data)
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
        mockFormBuilderToFillSomeFields()

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





}