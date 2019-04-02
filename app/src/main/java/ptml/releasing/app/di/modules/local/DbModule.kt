package ptml.releasing.app.di.modules.local


import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.securepreferences.SecurePreferences
import dagger.Module
import dagger.Provides
import ptml.releasing.BuildConfig
import ptml.releasing.app.prefs.Prefs
import ptml.releasing.app.prefs.PrefsManager
import ptml.releasing.app.di.scopes.ReleasingAppScope
import ptml.releasing.app.utils.Constants.PREFS

@Module
class DbModule {

    @Provides
    @ReleasingAppScope
    fun providePrefsHelper(sharedPreferences: SharedPreferences, gson: Gson): Prefs {
        return PrefsManager(sharedPreferences, gson)
    }

    @Provides
    @ReleasingAppScope
    fun provideSharedPreferences(context: Context):SharedPreferences{
        if(BuildConfig.DEBUG){
            return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        }
        return SecurePreferences(context, PREFS)
    }


}