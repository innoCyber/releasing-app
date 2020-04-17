package ptml.releasing.cargo_info.model

import ptml.releasing.form.models.FormConfigureDeviceResponse
import ptml.releasing.form.models.QuickRemark
import ptml.releasing.form.models.Voyage

data class FormDataWrapper(
    val remarks: Map<Int, QuickRemark>,
    val formConfigureDeviceResponse: FormConfigureDeviceResponse,
    val voyages: Map<Int, Voyage>? = null
)