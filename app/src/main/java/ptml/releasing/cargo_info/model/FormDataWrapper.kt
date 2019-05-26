package ptml.releasing.cargo_info.model

import ptml.releasing.configuration.models.ConfigureDeviceData
import ptml.releasing.configuration.models.ConfigureDeviceResponse
import ptml.releasing.quick_remarks.model.QuickRemark

data class FormDataWrapper (val remarks:Map<Int, QuickRemark>, val configureDeviceData: ConfigureDeviceResponse)