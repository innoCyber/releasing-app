package ptml.releasing.app.views.chips.tokenizer

import android.text.Editable
import android.text.Spanned
import android.util.Pair

import ptml.releasing.app.views.chips.ChipConfiguration
import ptml.releasing.app.views.chips.chip.Chip

/**
 * An extension of [Tokenizer][android.widget.MultiAutoCompleteTextView.Tokenizer] that provides extra support
 * for chipification.
 *
 *
 * In the context of this interface, a token is considered to be plain (non-chipped) text. Once a token is terminated it becomes or contains a chip.
 *
 *
 *
 * The CharSequences passed to the ChipTokenizer methods may contain both chipped text
 * and plain text so the tokenizer must have some method of distinguishing between the two (e.g. using a delimeter character.
 * The [.terminateToken] method is where a chip can be formed and returned to replace the plain text.
 * Whatever class the implementation deems to represent a chip, must implement the [Chip] interface.
 *
 *
 * @see SpanChipTokenizer
 */
interface ChipTokenizer {

    /**
     * Configures this ChipTokenizer to produce chips with the provided attributes. For each of these attributes, `-1` or `null` may be
     * passed to indicate that the attribute may be ignored.
     *
     *
     * This will also apply the provided [ChipConfiguration] to any existing chips in the provided text.
     *
     *
     * @param text              the text in which to search for existing chips to apply the configuration to
     * @param chipConfiguration a [ChipConfiguration] containing customizations for the chips produced by this class
     */
    fun applyConfiguration(text: Editable, chipConfiguration: ChipConfiguration)

    /**
     * Returns the start of the token that ends at offset
     * `cursor` within `text`.
     */
    fun findTokenStart(text: CharSequence, cursor: Int): Int

    /**
     * Returns the end of the token (minus trailing punctuation)
     * that begins at offset `cursor` within `text`.
     */
    fun findTokenEnd(text: CharSequence, cursor: Int): Int

    /**
     * Searches through `text` for any tokens.
     *
     * @param text the text in which to search for un-terminated tokens
     * @return a list of [Pair]s of the form (startIndex, endIndex) containing the locations of all
     * unterminated tokens
     */
    fun findAllTokens(text: CharSequence): List<Pair<Int, Int>>

    /**
     * Returns `text`, modified, if necessary, to ensure that
     * it ends with a token terminator (for example a space or comma).
     */
    fun terminateToken(text: CharSequence, data: Any?): CharSequence

    /**
     * Terminates (converts from token into chip) all unterminated tokens in the provided text.
     * This method CAN alter the provided text.
     *
     * @param text the text in which to terminate all tokens
     */
    fun terminateAllTokens(text: Editable)

    /**
     * Finds the index of the first character in `text` that is a part of `chip`
     *
     * @param chip the chip whose start should be found
     * @param text the text in which to search for the start of `chip`
     * @return the start index of the chip
     */
    fun findChipStart(chip: Chip, text: Spanned): Int

    /**
     * Finds the index of the character after the last character in `text` that is a part of `chip`
     *
     * @param chip the chip whose end should be found
     * @param text the text in which to search for the end of `chip`
     * @return the end index of the chip
     */
    fun findChipEnd(chip: Chip, text: Spanned): Int

    /**
     * Searches through `text` for any chips
     *
     * @param start index to start looking for terminated tokens (inclusive)
     * @param end   index to end looking for terminated tokens (exclusive)
     * @param text  the text in which to search for terminated tokens
     * @return a list of objects implementing the [Chip] interface to represent the terminated tokens
     */
    fun findAllChips(start: Int, end: Int, text: Spanned): Array<Chip>

    /**
     * Effectively does the opposite of [.terminateToken] by reverting the provided chip back into a token.
     * This method CAN alter the provided text.
     *
     * @param chip the chip to revert into a token
     * @param text the text in which the chip resides
     */
    fun revertChipToToken(chip: Chip, text: Editable)

    /**
     * Removes a chip and any text it encompasses from `text`. This method CAN alter the provided text.
     *
     * @param chip the chip to remove
     * @param text the text to remove the chip from
     */
    fun deleteChip(chip: Chip, text: Editable)

    /**
     * Removes a chip, any text it encompasses AND any padding text (such as spaces) that may have been inserted when the chip was created in
     * [.terminateToken] or after. This method CAN alter the provided text.
     *
     * @param chip the chip to remove
     * @param text the text to remove the chip and padding from
     */
    fun deleteChipAndPadding(chip: Chip, text: Editable)
}
