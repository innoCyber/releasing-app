package ptml.releasing.app.di.modules.main

import dagger.Module
import dagger.Provides
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import ptml.releasing.BuildConfig
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.repository.ImeiRepository
import ptml.releasing.app.data.domain.repository.VoyageRepository
import ptml.releasing.app.di.scopes.ReleasingAppScope
import ptml.releasing.app.local.Local
import ptml.releasing.app.remote.Remote
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.Constants
import ptml.releasing.app.utils.image.ImageLoader
import ptml.releasing.internet_error_logs.model.ErrorCache
import javax.inject.Named

/**
 * Created by kryptkode on 8/7/2019
 */

@Module
class TestMainModule{

    @Provides
    fun provideRepository(): Repository {
        return mockk()
    }

    @Provides
    fun provideRemote(): Remote {
        return mockk()
    }

    @Provides
    @ReleasingAppScope
    fun provideLocal(): Local {
        return mockk()
    }

    @Provides
    @ReleasingAppScope
    fun provideDispatchers(): AppCoroutineDispatchers {
        return AppCoroutineDispatchers(
            db = Dispatchers.Main,
            network = Dispatchers.Main,
            main = Dispatchers.Main
        )
    }

    @Provides
    @ReleasingAppScope
    fun provideImageLoader() : ImageLoader {
        return mockk()
    }

    @Provides
    fun provideVoyageRepository(): VoyageRepository {
        return mockk()
    }

    @ReleasingAppScope
    @Provides
    fun provideErrorCache(): ErrorCache {
        return mockk()
    }

    @ReleasingAppScope
    @Provides
    @Named(Constants.DEBUG)
    fun provideDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    @Provides
    fun provideImeiRepo(): ImeiRepository {
        return mockk()
    }

}