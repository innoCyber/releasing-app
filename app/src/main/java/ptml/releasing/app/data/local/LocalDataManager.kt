package ptml.releasing.app.data.local

import ptml.releasing.app.data.domain.model.login.LoginEntity
import ptml.releasing.app.data.domain.model.voyage.ReleasingVoyage

/**
 * Created by kryptkode on 10/23/2019.
 */

interface LocalDataManager {

    fun setLoginData(entity: LoginEntity)
    fun getLoginData(): LoginEntity
    fun clearLoginData()

    fun loggedIn(): Boolean
    fun setLoggedIn(value: Boolean)

    fun resetPreferences(): Boolean
    fun removePreference(key: String)


    fun setIMEI(imei: String)
    fun getIMEI(): String

    fun getServerBaseUrl(): String
    fun setServerBaseUrl(url: String)

    fun getRecentVoyages(): List<ReleasingVoyage>
    fun setRecentVoyages(voyages: List<ReleasingVoyage>)
    fun setLastSelectedVoyage(voyage: ReleasingVoyage)
    fun getLastSelectedVoyage(): ReleasingVoyage
}