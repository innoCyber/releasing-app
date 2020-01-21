package ptml.releasing.app.data.remote.mapper

import ptml.releasing.app.data.domain.model.login.AdminOptionsEntity
import ptml.releasing.app.data.remote.result.AdminOptionsResult
import ptml.releasing.driver.app.data.remote.mapper.ModelMapper
import javax.inject.Inject


/**
 * Created by kryptkode on 10/27/2019.
 */
class AdminOptionResultMapper @Inject constructor() :
    ModelMapper<AdminOptionsResult, AdminOptionsEntity> {

    override fun mapFromModel(model: AdminOptionsResult): AdminOptionsEntity {
        return AdminOptionsEntity(
            model.success,
            model.message,
            model.cargoTypes,
            model.operationSteps,
            model.terminals
        )
    }
}