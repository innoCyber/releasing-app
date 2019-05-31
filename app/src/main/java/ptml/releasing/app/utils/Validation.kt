package ptml.releasing.app.utils

import java.util.*
import java.util.regex.Pattern

object Validation {

    @Suppress("NAME_SHADOWING")
    fun isURL(url: String?): Boolean {
        var url: String? = url ?: return false

        url = url?.toLowerCase(Locale.getDefault())
        val regex = ("^((ftp|http|https|intent)?://)"                      // support scheme

                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp user@

                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}"                            // IP URL -> 199.194.52.184

                + "|"                                                        // IP DOMAIN

                + "([0-9a-z_!~*'()-]+\\.)*"                                  //  www.

                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\."

                + "[a-z]{2,6})"                                              // first level domain -> .com or .museum

                + "(:[0-9]{1,4})?"                                           // :80

                + "((/?)|"                                                   // a slash isn't required if there is no file name

                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$")
        val pattern = Pattern.compile(regex)
        return pattern.matcher(url).matches()
    }
}