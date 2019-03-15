package ptml.releasing.di.modules.local


import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ptml.releasing.db.prefs.Prefs
import ptml.releasing.db.prefs.PrefsManager
import ptml.releasing.di.scopes.ReleasingAppScope

@Module
class DbModule {

    @Provides
    @ReleasingAppScope
    fun providePrefsHelper(context: Context, gson: Gson): Prefs {
        return PrefsManager(context, gson)
    }


}