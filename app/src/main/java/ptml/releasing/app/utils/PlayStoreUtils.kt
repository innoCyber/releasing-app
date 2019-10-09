package ptml.releasing.app.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri

import ptml.releasing.R
import ptml.releasing.app.utils.Constants.MARKET_URI_BASE
import ptml.releasing.app.utils.Constants.PLAY_STORE_URL_BASE
import ptml.releasing.app.utils.Constants.PLAY_STORE_PACKAGE_NAME


class PlayStoreUtils(private val context: Context) {

    fun openPlayStore() {
        val marketIntent = createPlayStoreMarketIntent()
        val playStoreInfo = resolvePlayStore(marketIntent)
        if (playStoreInfo != null) {
            val component = getComponent(playStoreInfo)
            addComponentAndFlagsToIntent(marketIntent, component)
            openPlayStoreDirectly(marketIntent)
        } else {
            openPlayStoreInBrowser()
        }
    }

    private fun openPlayStoreDirectly(intent: Intent) {
        context.startActivity(intent)
    }

    private fun openPlayStoreInBrowser() {
        val intent = createPlayStoreBrowserIntent()
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.open_playstore_with)))
    }


    private fun createPlayStoreMarketIntent(): Intent {
        val marketUrl = getMarketUrl()
        val marketUri = convertToUri(marketUrl)
        return Intent(Intent.ACTION_VIEW, marketUri)
    }

    private fun addComponentAndFlagsToIntent(intent: Intent, componentName: ComponentName) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.component = componentName
    }

    private fun createPlayStoreBrowserIntent(): Intent {
        val playStoreUrl = getPlayStoreUrl()
        val playStoreBrowserUri = convertToUri(playStoreUrl)
        return Intent(Intent.ACTION_VIEW, playStoreBrowserUri)
    }

    private fun resolvePlayStore(intent: Intent): ActivityInfo? {
        val otherAppsResolveInfo = context.packageManager
            .queryIntentActivities(intent, 0)
        for (resolveInfo in otherAppsResolveInfo) {
            if (resolveInfo.activityInfo.applicationInfo.packageName == PLAY_STORE_PACKAGE_NAME) {
                return resolveInfo.activityInfo
            }
        }
        return null
    }

    private fun getComponent(activityInfo: ActivityInfo): ComponentName {
        return ComponentName(
            activityInfo.applicationInfo.packageName,
            activityInfo.name
        )
    }

    private fun convertToUri(uri: String): Uri? {
        return Uri.parse(uri)
    }

    private fun getAppId(): String {
        return context.packageName
    }

    private fun getMarketUrl(): String {
        return "$MARKET_URI_BASE${getAppId()}"
    }

    private fun getPlayStoreUrl(): String {
        return "$PLAY_STORE_URL_BASE${getAppId()}"
    }
}