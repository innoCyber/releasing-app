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
import ptml.releasing.cargo_search.data.data_source.ChassisDatabase
import ptml.releasing.cargo_search.data.data_source.ChassisNumberDao
import ptml.releasing.cargo_search.data.repository.ChassisNumberRepositoryImpl
import ptml.releasing.cargo_search.domain.repository.ChassisNumberRepository
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
    fun provideRemote(retrofit: Retrofit, localDataManager: LocalDataManager): Remote {
        return ReleasingRemote(retrofit, localDataManager)
    }

    @Provides
    @ReleasingAppScope
    fun provideLocal(prefs: Prefs,chassisNumberRepository: ChassisNumberRepository): Local {
        return ReleasingLocal(prefs,chassisNumberRepository)
    }

    @Provides
    @ReleasingAppScope
    fun provideChassisNumberRepository(chassisNumberDao: ChassisNumberDao): ChassisNumberRepository {
        return ChassisNumberRepositoryImpl(chassisNumberDao)
    }

    @Provides
    @ReleasingAppScope
    fun provideChassisNumberDao(chassisDatabase: ChassisDatabase): ChassisNumberDao {
        return chassisDatabase.chassisNumberDao()
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