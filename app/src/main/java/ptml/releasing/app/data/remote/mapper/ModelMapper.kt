package ptml.releasing.driver.app.data.remote.mapper

/**
 * Created by kryptkode on 10/23/2019.
 */

interface ModelMapper<in M, out E> {

    fun mapFromModel(model: M): E

}