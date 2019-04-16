package ptml.releasing.app.views.chips.terminator

import android.text.Editable

class TextIterator(val text: Editable, private val mStart: Int, private var mEnd: Int) {

    var index: Int = 0
        private set

    init {

        index = mStart - 1 // Subtract 1 so that the first call to nextCharacter() will return the first character
    }

    fun totalLength(): Int {
        return text.length
    }

    fun windowLength(): Int {
        return mEnd - mStart
    }

    fun hasNextCharacter(): Boolean {
        return index + 1 < mEnd
    }

    fun nextCharacter(): Char {
        index++
        return text[index]
    }

    fun deleteCharacter(maintainIndex: Boolean) {
        text.replace(index, index + 1, "")
        if (!maintainIndex) {
            index--
        }
        mEnd--
    }

    fun replace(replaceStart: Int, replaceEnd: Int, chippedText: CharSequence) {
        text.replace(replaceStart, replaceEnd, chippedText)

        // Update indexes
        val newLength = chippedText.length
        val oldLength = replaceEnd - replaceStart
        index = replaceStart + newLength - 1
        mEnd += newLength - oldLength
    }
}
