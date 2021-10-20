package ptml.releasing.cargo_search.model

import ptml.releasing.cargo_search.model.PODOperationStep

data class PODResponse(val success: Boolean, val message: String, val operation_step: List<PODOperationStep>){
}