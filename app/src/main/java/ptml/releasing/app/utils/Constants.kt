package ptml.releasing.app.utils

object Constants{

    const val BAR_CODE: String = "barcode"
    const val EXTRAS = "extras"
    const val OK_HTTP_CACHE: String = "ok_http_cache"
    const val CONNECT_TIME_OUT = 60L
    const val WRITE_TIME_OUT = 60L
    const val DATABASE_NAME = "releasing.db"
    const val PREFS = "prefs"

    //Connection
    const val IS_NETWORK_AVAILABLE: String = "Is Network Available"
    const val NETWORK_STATE_INTENT: String = "Network State"

    const val LABEL = "label"
    const val TEXT_BOX = "textbox"
    const val MULTI_LINE_TEXTBOX = "multi_line_textbox"
    const val IMAGES = "images"
    const val PRINTER = "printer"
    const val DAMAGES = "damages"
    const val SINGLE_SELECT ="single_select"
    const val MULTI_SELECT = "multi_select"
    const val CHECK_BOX = "checkbox"
    const val UNKNOWN = "unknown"
    const val ITEM_TO_EXPAND = 6


    const val DEFAULT_PRINTER_CODE = "! 0 200 200 406 1\r\n" +
            "PW 480\r\n" +
            "TONE 0\r\n" +
            "SPEED 4\r\n" +
            "ON-FEED IGNORE\r\n" +
            "NO-PACE\r\n" +
            "BAR-SENSE\r\n" +
            "T 4 0 179 47 PTML\r\n" +
            "BT 7 0 6\r\n" +
            "B 39 1 30 216 31 134 var_barcode\r\n" +
            "FORM\r\n" +
            "PRINT\r\n"

}
