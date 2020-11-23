package ptml.releasing.form.models

import com.google.gson.annotations.SerializedName
import ptml.releasing.form.models.base.BaseModel

class Settings(
    @SerializedName("currentPrinter") var currentPrinter: String?,
    @SerializedName("currentPrinterName") var currentPrinterName: String?,
    @SerializedName("labelCpclData") var labelCpclData: String?
) : BaseModel()
