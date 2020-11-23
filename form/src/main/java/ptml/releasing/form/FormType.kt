package ptml.releasing.form

import ptml.releasing.form.utils.Constants

enum class FormType constructor(val type: String) {
    LABEL(Constants.LABEL),
    TEXTBOX(Constants.TEXT_BOX),
    MULTI_LINE_TEXTBOX(Constants.MULTI_LINE_TEXTBOX),
    IMAGES(Constants.IMAGES),
    PRINTER(Constants.PRINTER),
    PRINTER_DAMAGES(Constants.PRINTER_DAMAGES),
    UPLOAD_IMAGES(Constants.UPLOAD_IMAGES),
    DAMAGES(Constants.DAMAGES),
    SINGLE_SELECT(Constants.SINGLE_SELECT),
    MULTI_SELECT(Constants.MULTI_SELECT),
    CHECK_BOX(Constants.CHECK_BOX),
    QUICK_REMARK(Constants.QUICK_REMARKS),
    SIMPLE_TEXT(Constants.SIMPLE_TEXT),
    VOYAGE(Constants.VOYAGE),
    UNKNOWN(Constants.UNKNOWN);


    companion object {
        fun fromType(type: String?): FormType {
            return when (type) {
                Constants.LABEL -> LABEL
                Constants.TEXT_BOX -> TEXTBOX
                Constants.MULTI_LINE_TEXTBOX -> MULTI_LINE_TEXTBOX
                Constants.IMAGES -> IMAGES
                Constants.PRINTER -> PRINTER
                Constants.PRINTER_DAMAGES -> PRINTER_DAMAGES
                Constants.DAMAGES -> DAMAGES
                Constants.SINGLE_SELECT -> SINGLE_SELECT
                Constants.MULTI_SELECT -> MULTI_SELECT
                Constants.CHECK_BOX -> CHECK_BOX
                Constants.QUICK_REMARKS -> QUICK_REMARK
                Constants.SIMPLE_TEXT -> SIMPLE_TEXT
                Constants.VOYAGE -> VOYAGE
                else -> UNKNOWN
            }
        }
    }

}