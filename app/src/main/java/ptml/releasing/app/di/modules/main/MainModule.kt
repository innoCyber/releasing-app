package ptml.releasing.app.di.modules.main

import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import ptml.releasing.app.data.ReleasingRepository
import ptml.releasing.app.data.Repository
import ptml.releasing.app.data.domain.repository.VoyageRepository
import ptml.releasing.app.data.local.LocalDataManager
import ptml.releasing.app.data.remote.VoyageRestClient
import ptml.releasing.app.data.remote.mapper.VoyageMapper
import ptml.releasing.app.data.repo.VoyageRepositoryImpl
import ptml.releasing.app.di.scopes.ReleasingAppScope
import ptml.releasing.app.eventbus.EventBus
import ptml.releasing.app.local.Local
import ptml.releasing.app.local.ReleasingLocal
import ptml.releasing.app.prefs.Prefs
import ptml.releasing.app.remote.ReleasingRemote
import ptml.releasing.app.remote.Remote
import ptml.releasing.app.utils.AppCoroutineDispatchers
import ptml.releasing.app.utils.FileUtils
import ptml.releasing.app.utils.image.ImageLoader
import ptml.releasing.app.utils.image.ImageLoaderImpl
import retrofit2.Retrofit

@Module()
class MainModule {

    @Provides
    fun provideRepository(
        remote: Remote,
        local: Local,
        appCoroutineDispatchers: AppCoroutineDispatchers,
        fileUtils: FileUtils
    ): Repository {
        return ReleasingRepository(remote, local, appCoroutineDispatchers, fileUtils)
    }

    @Provides
    fun provideRemote(retrofit: Retrofit): Remote {
        return ReleasingRemote(retrofit)
    }

    @Provides
    @ReleasingAppScope
    fun provideLocal(prefs: Prefs): Local {
        return ReleasingLocal(prefs)
    }

    @Provides
    @ReleasingAppScope
    fun provideDispatchers(): AppCoroutineDispatchers {
        return AppCoroutineDispatchers(
            db = Dispatchers.IO,
            network = Dispatchers.IO,
            main = Dispatchers.Main
        )
    }

    @Provides
    @ReleasingAppScope
    fun provideEventBus(dispatchers: AppCoroutineDispatchers): EventBus {
        return EventBus.getInstance(dispatchers)
    }

    @Provides
    fun provideVoyageRepository(
        localDataManager: LocalDataManager,
        voyageRestClient: VoyageRestClient,
        voyageMapper: VoyageMapper
    ): VoyageRepository {
        return VoyageRepositoryImpl(localDataManager, voyageRestClient, voyageMapper)
    }


    @Provides
    @ReleasingAppScope
    fun provideImageLoader(context: Context) : ImageLoader{
        return ImageLoaderImpl(context)
    }


}