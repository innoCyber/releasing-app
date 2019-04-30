package ptml.releasing.cargo_info.model

import com.google.gson.annotations.SerializedName
import ptml.releasing.app.base.BaseModel

data class FormSelection(
    @SerializedName("selected_options")val selectedOptions: List<Int>
):BaseModel()