package ptml.releasing.app.utils


object Constants {
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
    const val SINGLE_SELECT = "single_select"
    const val MULTI_SELECT = "multi_select"
    const val CHECK_BOX = "checkbox"
    const val QUICK_REMARKS = "quick_remarks"
    const val UNKNOWN = "unknown"
    const val ITEM_TO_EXPAND = 6


    const val DEFAULT_DAMAGES_VERSION = 1L
    const val DEFAULT_VOYAGE_VERSION = 1L
    const val DEFAULT_QUICK_REMARKS_VERSION = 1L
    const val DEFAULT_APP_VERSION = 1L

    const val PRINTER_TEXT_TO_REPLACE = "var_text"
    const val PRINTER_HEIGHT_TO_REPLACE = "var_height"
    const val MULTILINE_LINES_PER_PAGE= 20
    const val MULTILINE_LINES_PAGE_HEIGHT= 1050
    const val DEFAULT_BARCODE_PRINTER_SETTINGS = "! 0 200 200 400 1\r\n" +
            "PW 480\r\n" +
            "TONE 50\r\n" +
            "SPEED 4\r\n" +
            "ON-FEED IGNORE\r\n" +
            "NO-PACE\r\n" +
            "BAR-SENSE\r\n" +
            "T 4 0 179 20 PTML\r\n" +
            "BT 7 0 6\r\n" +
            "B 39 1 30 200 31 70 $PRINTER_TEXT_TO_REPLACE\r\n" +
            "FORM\r\n" +
            "PRINT\r\n"

    /**
     * Default printer settings for multiline text
     * the "var_texts" string should be replaced by the text to be printed
     * each line of text should  be terminated by \r\n (carriage return  and new line characters)
     * */
 /*   const val DEFAULT_MULTILINE_PRINTER_SETTINGS = "! 0 200 200 400 1\r\n" +
            "PW 480\r\n" +
            "TONE 50\r\n" +
            "SPEED 4\r\n" +
            "ON-FEED IGNORE\r\n" +
            "NO-PACE\r\n" +
            "BAR-SENSE\r\n" +
            "T 4 0 179 20 PTML\r\n" +
            "ML 47\r\n" +
            "T 4 0 10 20\r\n" +
            PRINTER_TEXT_TO_REPLACE +
            "ENDML\r\n" +
            "FORM\r\n" +
            "PRINT\r\n"
*/



    const  val DEFAULT_MULTILINE_PRINTER_SETTINGS = "! 0 200 200 $PRINTER_HEIGHT_TO_REPLACE 1\r\n" +
            "PW 480\r\n" +
            "TONE 50\r\n" +
            "SPEED 4\r\n" +
            "ON-FEED IGNORE\r\n" +
            "NO-PACE\r\n" +
            "BAR-SENSE\r\n" +
          "T 4 0 179 20 PTML\r\n" +
            "ML 47\r\n" +
            "T 7 0 45 70\r\n" +
            "$PRINTER_TEXT_TO_REPLACE\n" +
            "ENDML\r\n" +
            "FORM\r\n" +
            "PRINT\r\n"


    const val ALPHANUMERIC = "alphanumeric"
    const val NUMERIC = "numeric"
    const val SHIP_SIDE = "Ship Side"
    const val PLAY_STORE_URL_BASE = "https://play.google.com/store/apps/details?id="
    const val MARKET_URI_BASE = "market://details?id="
    const val PLAY_STORE_PACKAGE_NAME = "com.android.vending"
    const val INVALID_ID = -1
    const val DEBUG = "debug"
    const val PASSWORD_LENGTH = 6
}
