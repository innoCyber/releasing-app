
package ptml.releasing.app.di.modules.worker

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@dagger.Module(includes = [AssistedInject_SampleAssistedInjectModule::class])
@AssistedModule
interface SampleAssistedInjectModule