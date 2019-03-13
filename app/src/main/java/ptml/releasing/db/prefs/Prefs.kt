package ptml.releasing.db.prefs

interface Prefs {
    fun isFirst(): Boolean
    fun setFirst(value:Boolean)
}