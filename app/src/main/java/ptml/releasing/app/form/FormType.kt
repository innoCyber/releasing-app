package ptml.releasing.app.form

import ptml.releasing.app.utils.Constants

enum class FormType constructor(val type: String) {
    LABEL(Constants.LABEL),
    TEXTBOX(Constants.TEXT_BOX),
    MULTI_LINE_TEXTBOX(Constants.MULTI_LINE_TEXTBOX),
    IMAGES(Constants.IMAGES),
    PRINTER(Constants.PRINTER),
    DAMAGES(Constants.DAMAGES),
    SINGLE_SELECT(Constants.SINGLE_SELECT),
    MULTI_SELECT(Constants.MULTI_SELECT),
    UNKNOWN(Constants.UNKNOWN);


    companion object{
        fun fromType(type: String?): FormType {
            return when (type) {
                Constants.LABEL -> LABEL
                Constants.TEXT_BOX -> TEXTBOX
                Constants.MULTI_LINE_TEXTBOX -> MULTI_LINE_TEXTBOX
                Constants.IMAGES -> IMAGES
                Constants.PRINTER -> PRINTER
                Constants.DAMAGES -> DAMAGES
                Constants.SINGLE_SELECT -> SINGLE_SELECT
                Constants.MULTI_SELECT -> MULTI_SELECT
                else -> UNKNOWN
            }
        }
    }

}