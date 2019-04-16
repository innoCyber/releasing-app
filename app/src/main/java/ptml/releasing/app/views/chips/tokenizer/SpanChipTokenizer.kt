package ptml.releasing.app.views.chips.tokenizer

import android.content.Context
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.util.Pair

import ptml.releasing.app.views.chips.ChipConfiguration
import ptml.releasing.app.views.chips.chip.Chip
import ptml.releasing.app.views.chips.chip.ChipCreator
import ptml.releasing.app.views.chips.chip.ChipSpan

import java.lang.reflect.Array
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

/**
 * A default implementation of [ChipTokenizer].
 * This implementation does the following:
 *
 *  * Surrounds each token with a space and the Unit Separator ASCII control character (31) - See the diagram below
 *
 *  * The spaces are included so that android keyboards can distinguish the chips as different words and provide accurate
 * autocorrect suggestions
 *
 *
 *  * Replaces each token with a [ChipSpan] containing the same text, once the token terminates
 *  * Uses the values passed to [.applyConfiguration] to configure any ChipSpans that get created
 *
 * Each terminated token will therefore look like the following (this is what will be returned from [.terminateToken]):
 * <pre>
 * -----------------------------------------------------------
 * | SpannableString                                         |
 * |   ----------------------------------------------------  |
 * |   | ChipSpan                                         |  |
 * |   |                                                  |  |
 * |   |  space   separator    text    separator   space  |  |
 * |   |                                                  |  |
 * |   ----------------------------------------------------  |
 * -----------------------------------------------------------
</pre> *
 *
 * @see ChipSpan
 */
class SpanChipTokenizer<C : Chip>(private val mContext: Context, private val mChipCreator: ChipCreator<C>, private val mChipClass: Class<C>) : ChipTokenizer {

    private var mChipConfiguration: ChipConfiguration? = null

    private val mReverseTokenIndexesSorter = Comparator<Pair<Int, Int>> { lhs, rhs -> rhs.first - lhs.first }

    override fun applyConfiguration(text: Editable, chipConfiguration: ChipConfiguration) {
        mChipConfiguration = chipConfiguration

        for (chip in findAllChips(0, text.length, text)) {
            // Recreate the chips with the new configuration
            val chipStart = findChipStart(chip, text)
            deleteChip(chip, text)
            text.insert(chipStart, terminateToken(mChipCreator.createChip(mContext, chip as C)))
        }
    }

    override fun findTokenStart(text: CharSequence, cursor: Int): Int {
        var i = cursor

        // Work backwards until we find a CHIP_SPAN_SEPARATOR
        while (i > 0 && text[i - 1] != CHIP_SPAN_SEPARATOR) {
            i--
        }
        // Work forwards to skip over any extra whitespace at the beginning of the token
        while (i > 0 && i < text.length && Character.isWhitespace(text[i])) {
            i++
        }
        return i
    }

    override fun findTokenEnd(text: CharSequence, cursor: Int): Int {
        var i = cursor
        val len = text.length

        // Work forwards till we find a CHIP_SPAN_SEPARATOR
        while (i < len) {
            if (text[i] == CHIP_SPAN_SEPARATOR) {
                return i - 1 // subtract one because the CHIP_SPAN_SEPARATOR will be preceded by a space
            } else {
                i++
            }
        }
        return len
    }

    override fun findAllTokens(text: CharSequence): List<Pair<Int, Int>> {
        val unterminatedTokens = ArrayList<Pair<Int, Int>>()

        var insideChip = false
        // Iterate backwards through the text (to avoid messing up indexes)
        var index = text.length - 1
        while (index >= 0) {
            val theCharacter = text[index]

            // Every time we hit a CHIP_SPAN_SEPARATOR character we switch from being inside to outside
            // or outside to inside a chip
            // This check must happen before the whitespace check because CHIP_SPAN_SEPARATOR is considered a whitespace character
            if (theCharacter == CHIP_SPAN_SEPARATOR) {
                insideChip = !insideChip
                index--
                continue
            }

            // Completely skip over whitespace
            if (Character.isWhitespace(theCharacter)) {
                index--
                continue
            }

            // If we're ever outside a chip, see if the text we're in is a viable token for chipification
            if (!insideChip) {
                val tokenStart = findTokenStart(text, index)
                val tokenEnd = findTokenEnd(text, index)

                // Can only actually be chipified if there's at least one character between them
                if (tokenEnd - tokenStart >= 1) {
                    unterminatedTokens.add(Pair(tokenStart, tokenEnd))
                    index = tokenStart
                }
            }
            index--
        }
        return unterminatedTokens
    }

    override fun terminateToken(text: CharSequence, data: Any?): CharSequence {
        // Remove leading/trailing whitespace
        val trimmedText = text.toString().trim { it <= ' ' }
        return terminateToken(mChipCreator.createChip(mContext, trimmedText, data))
    }

    private fun terminateToken(chip: C): CharSequence {
        // Surround the text with CHIP_SPAN_SEPARATOR and spaces
        // The spaces allow autocorrect to correctly identify words
        val chipSeparator = Character.toString(CHIP_SPAN_SEPARATOR)
        val autoCorrectSeparator = Character.toString(AUTOCORRECT_SEPARATOR)
        val textWithSeparator = autoCorrectSeparator + chipSeparator + chip.text + chipSeparator + autoCorrectSeparator

        // Build the container object to house the ChipSpan and space
        val spannableString = SpannableString(textWithSeparator)

        // Attach the ChipSpan
        if (mChipConfiguration != null) {
            mChipCreator.configureChip(chip, mChipConfiguration!!)
        }
        spannableString.setSpan(chip, 0, textWithSeparator.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spannableString
    }

    override fun terminateAllTokens(text: Editable) {
        val unterminatedTokens = findAllTokens(text)
        // Sort in reverse order (so index changes don't affect anything)
        Collections.sort(unterminatedTokens, mReverseTokenIndexesSorter)
        for (indexes in unterminatedTokens) {
            val start = indexes.first
            val end = indexes.second
            val textToChip = text.subSequence(start, end)
            val chippedText = terminateToken(textToChip, null)
            text.replace(start, end, chippedText)
        }
    }

    override fun findChipStart(chip: Chip, text: Spanned): Int {
        return text.getSpanStart(chip)
    }

    override fun findChipEnd(chip: Chip, text: Spanned): Int {
        return text.getSpanEnd(chip)
    }


    @SuppressWarnings("unchecked")
    override fun findAllChips(start: Int, end: Int, text: Spanned): kotlin.Array<Chip> {
        val spansArray = text.getSpans(start, end, mChipClass)
        return spansArray as kotlin.Array<Chip>
    }

    override fun revertChipToToken(chip: Chip, text: Editable) {
        val chipStart = findChipStart(chip, text)
        val chipEnd = findChipEnd(chip, text)
        text.removeSpan(chip)
        text.replace(chipStart, chipEnd, chip.text)
    }

    override fun deleteChip(chip: Chip, text: Editable) {
        val chipStart = findChipStart(chip, text)
        val chipEnd = findChipEnd(chip, text)
        text.removeSpan(chip)
        // On the emulator for some reason the text automatically gets deleted and chipStart and chipEnd end up both being -1, so in that case we
        // don't need to call text.delete(...)
        if (chipStart != chipEnd) {
            text.delete(chipStart, chipEnd)
        }
    }

    override fun deleteChipAndPadding(chip: Chip, text: Editable) {
        // This implementation does not add any extra padding outside of the span so we can just delete the chip normally
        deleteChip(chip, text)
    }

    companion object {

        /**
         * The character used to separate chips internally is the US (Unit Separator) ASCII control character.
         * This character is used because it's untypable so we have complete control over when chips are created.
         */
        val CHIP_SPAN_SEPARATOR: Char = 31.toChar()
        val AUTOCORRECT_SEPARATOR = ' '
    }
}
