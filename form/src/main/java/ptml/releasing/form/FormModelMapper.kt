package ptml.releasing.form

/**
 * Created by kryptkode on 10/23/2019.
 */

interface FormModelMapper<M, F> {

    fun mapFromModel(model: M): F

    fun mapToModel(model: F): M
}