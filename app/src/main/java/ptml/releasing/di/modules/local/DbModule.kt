package ptml.releasing.di.modules.local


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import dagger.Module
import dagger.Provides
import ptml.releasing.di.scopes.ReleasingAppScope
import javax.inject.Named

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
        };
    }
}