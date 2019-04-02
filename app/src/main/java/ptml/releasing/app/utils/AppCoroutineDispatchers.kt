package ptml.releasing.app.utils

import kotlinx.coroutines.CoroutineDispatcher

 data class AppCoroutineDispatchers(
    val db: CoroutineDispatcher,
    val network: CoroutineDispatcher,
    val main: CoroutineDispatcher
)