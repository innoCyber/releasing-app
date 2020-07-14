package ptml.releasing.images.api

import com.google.gson.annotations.SerializedName

data class ImageUploadData(
    @SerializedName("cargo_type") val cargoTypeId: Int?,
    @SerializedName("operation_steps") val operationStep: Int?,
    @SerializedName("cargoId") val cargoId: Int?,
    @SerializedName("data") val fileNames: List<ImageFileName>
)

data class ImageFileName(@SerializedName("file_name") val fileName: String)