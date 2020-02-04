package ptml.releasing.app.data.domain.state

/**
 * Created by kryptkode on 10/23/2019.
 */

sealed class DataState<out T> {
    object Loading : DataState<Nothing>()
    class Success<out T>(val data: T?) : DataState<T>()
    class Error(val error: Any?) : DataState<Nothing>()
}