package ptml.releasing.app.remote

enum class Endpoint(
    val url:String,
    val description: String
) {
    VERIFY_DEVICE(Urls.VERIFY_DEVICE, "Verification of the device IMEI"),
    LOGIN(Urls.LOGIN, "Admin login"),
    SET_ADMIN_CONFIGURATION(Urls.SET_ADMIN_CONFIGURATION, "Get the admin configuration for operation step, terminal and cargo type"),
    SET_CONFIGURATION_DEVICE(Urls.SET_CONFIGURATION_DEVICE, "Get the form configuration for an operation step"),
    DOWNLOAD_DAMAGES(Urls.DOWNLOAD_DAMAGES, "Download damages"),
    FIND_CARGO(Urls.FIND_CARGO, "Get a cargo's information based on it's code"),
    UPLOAD_DATA (Urls.UPLOAD_DATA, "Upload form information for a cargo"),
    QUICK_REMARK(Urls.QUICK_REMARK,  "Download quick remarks"),
    UNKNOWN("",  "An unknown path");

    companion object{
        fun fromPath(path:String?):Endpoint{
            return when(path){
                Urls.VERIFY_DEVICE -> VERIFY_DEVICE
                Urls.LOGIN -> LOGIN
                Urls.SET_ADMIN_CONFIGURATION -> SET_ADMIN_CONFIGURATION
                Urls.SET_CONFIGURATION_DEVICE -> SET_CONFIGURATION_DEVICE
                Urls.DOWNLOAD_DAMAGES -> DOWNLOAD_DAMAGES
                Urls.FIND_CARGO -> FIND_CARGO
                Urls.UPLOAD_DATA -> UPLOAD_DATA
                Urls.QUICK_REMARK -> QUICK_REMARK
                else -> UNKNOWN
            }
        }
    }

}

object Urls{
    const val VERIFY_DEVICE = "verifyDeviceId"
    const val LOGIN = "login"
    const val SET_ADMIN_CONFIGURATION = "setAdminConfiguration"
    const val SET_CONFIGURATION_DEVICE = "setConfigurationDevice"
    const val DOWNLOAD_DAMAGES = "downloadDamages"
    const val FIND_CARGO = "findCargo"
    const val UPLOAD_DATA  = "uploadData"
    const val QUICK_REMARK = "downloadQuickRemarksCar"

}
