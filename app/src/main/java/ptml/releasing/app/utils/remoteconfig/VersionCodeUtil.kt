package ptml.releasing.app.utils.remoteconfig

import android.content.Context
import android.os.Build
import javax.inject.Inject

/**
Created by kryptkode on 7/23/2019
 */
class VersionCodeUtil @Inject constructor(private val context: Context) {
    @Suppress("DEPRECATION")
    fun getCurrentVersionCode(): Long {
        val packageName = context.packageName
        val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
    }
}