package ptml.releasing.app.form

internal enum class FormType constructor(val type: String) {
        LABEL("label"),
        TEXTBOX("textbox"),
        MULTI_LINE_TEXTBOX(" multi_line_textbox"),
        IMAGES("images"),
        PRINTER("printer"),
        DAMAGES("damages"),
        SINGLE_SELECT("single_select"),
        MULTI_SELECT("multi_select"),
        UNKNOWN("unknown")
    }