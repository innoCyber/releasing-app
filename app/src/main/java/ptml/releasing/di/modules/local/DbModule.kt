package ptml.releasing.di.modules.local


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import ptml.releasing.db.AppDatabase
import ptml.releasing.db.ReleasingLocal
import ptml.releasing.db.prefs.PrefsManager
import ptml.releasing.di.modules.rx.OBSERVER_ON
import ptml.releasing.di.modules.rx.SUBSCRIBER_ON
import ptml.releasing.di.scopes.ReleasingAppScope
import ptml.releasing.utils.Constants.DATABASE_NAME
import javax.inject.Named
import javax.inject.Singleton

@Module
class DbModule {


    @SuppressLint("MissingPermission", "HardwareIds")
    @Provides
    @Named("deviceId")
    @ReleasingAppScope
    fun provideDeviceId(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager.imei
        } else {
            telephonyManager.deviceId
        }
    }


    @Provides
    @ReleasingAppScope
    fun providePrefsHelper(context: Context): PrefsManager {
        return PrefsManager(context)
    }


    @Provides
    @ReleasingAppScope
    fun provideAppDatabase(context: Context): AppDatabase {
        val builder = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
        return builder.build()
    }

}