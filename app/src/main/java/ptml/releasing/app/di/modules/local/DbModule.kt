package ptml.releasing.app.di.modules.local


import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ptml.releasing.app.prefs.Prefs
import ptml.releasing.app.prefs.PrefsManager
import ptml.releasing.app.di.scopes.ReleasingAppScope

@Module
class DbModule {

    @Provides
    @ReleasingAppScope
    fun providePrefsHelper(context: Context, gson: Gson): Prefs {
        return PrefsManager(context, gson)
    }


}