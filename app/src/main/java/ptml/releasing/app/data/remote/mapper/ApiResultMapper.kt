package ptml.releasing.app.data.remote.mapper

import ptml.releasing.app.data.domain.model.ApiResult
import ptml.releasing.app.data.remote.result._Result
import ptml.releasing.driver.app.data.remote.mapper.ModelMapper
import javax.inject.Inject

/**
 * Created by kryptkode on 10/23/2019.
 */

class ApiResultMapper @Inject constructor() : ModelMapper<_Result<*>, ApiResult> {
    override fun mapFromModel(model: _Result<*>): ApiResult {
        return ApiResult(model.success, model.message)
    }
}