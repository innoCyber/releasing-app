package ptml.releasing.form.models

import com.google.gson.annotations.SerializedName
import ptml.releasing.form.models.base.BaseModel

data class FormSelection(
    @SerializedName("selected_options") val selectedOptions: List<Int>
) : BaseModel()