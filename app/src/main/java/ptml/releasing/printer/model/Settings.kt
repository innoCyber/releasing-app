package ptml.releasing.printer.model

import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel

class Settings(
    @SerializedName("currentPrinter") var currentPrinter: String?,
    @SerializedName("currentPrinterName") var currentPrinterName: String?,
    @SerializedName("labelCpclData") var labelCpclData: String?
) : BaseModel()
