package ptml.releasing.cargo_info.model

import ptml.releasing.form.models.FormConfigureDeviceResponse
import ptml.releasing.form.models.QuickRemark

data class FormDataWrapper(
    val remarks: Map<Int, QuickRemark>,
    val formConfigureDeviceResponse: FormConfigureDeviceResponse
)