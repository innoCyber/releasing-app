package ptml.releasing.app.di.modules.local


import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.securepreferences.SecurePreferences
import dagger.Module
import dagger.Provides
import ptml.releasing.BuildConfig
import ptml.releasing.app.db.ErrorCacheImpl
import ptml.releasing.app.db.ReleasingDb
import ptml.releasing.app.db.mapper.InternetErrorLogMapper
import ptml.releasing.app.di.scopes.ReleasingAppScope
import ptml.releasing.app.prefs.Prefs
import ptml.releasing.app.prefs.PrefsManager
import ptml.releasing.app.utils.Constants.PREFS
import ptml.releasing.internet_error_logs.model.ErrorCache

@Module
class LocalModule {

    @Provides
    @ReleasingAppScope
    fun providePrefsHelper(sharedPreferences: SharedPreferences, gson: Gson, context: Context): Prefs {
        return PrefsManager(sharedPreferences, gson, context)
    }

    @Provides
    @ReleasingAppScope
    fun provideSharedPreferences(context: Context):SharedPreferences{
        if(BuildConfig.DEBUG){
            return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
        return SecurePreferences(context, PREFS)
    }

    @Provides
    @ReleasingAppScope
    fun provideDb(context: Context):ReleasingDb{
        return ReleasingDb.getInstance(context)
    }


    @ReleasingAppScope
    @Provides
    fun provideErrorCache(mapper: InternetErrorLogMapper, db: ReleasingDb): ErrorCache{
        return ErrorCacheImpl(mapper, db)
    }

}