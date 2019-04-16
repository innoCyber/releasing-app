package ptml.releasing.app.views.chips.terminator

import ptml.releasing.app.views.chips.tokenizer.ChipTokenizer

import android.text.Editable

/**
 * This interface is used to handle the management of characters that should trigger the creation of chips in a text view.
 *
 * @see ChipTokenizer
 */
interface ChipTerminatorHandler {

    /**
     * Sets all the characters that will be marked as chip terminators. This will replace any previously set chip terminators.
     *
     * @param chipTerminators a map of characters to be marked as chip terminators to behaviors that describe how to respond to the characters, or null
     * to remove all chip terminators
     */
    fun setChipTerminators(chipTerminators: Map<Char, Int>?)

    /**
     * Adds a character as a chip terminator. When the provided character is encountered in entered text, the nearby text will be chipified according
     * to the behavior provided here.
     * `behavior` Must be one of:
     *
     *  * [.BEHAVIOR_CHIPIFY_ALL]
     *  * [.BEHAVIOR_CHIPIFY_CURRENT_TOKEN]
     *  * [.BEHAVIOR_CHIPIFY_TO_TERMINATOR]
     *
     *
     * @param character the character to mark as a chip terminator
     * @param behavior  the behavior describing how to respond to the chip terminator
     */
    fun addChipTerminator(character: Char, behavior: Int)

    /**
     * Customizes the way paste events are handled.
     * If one of:
     *
     *  * [.BEHAVIOR_CHIPIFY_ALL]
     *  * [.BEHAVIOR_CHIPIFY_CURRENT_TOKEN]
     *  * [.BEHAVIOR_CHIPIFY_TO_TERMINATOR]
     *
     * is passed, all chip terminators will be handled with that behavior when a paste event occurs.
     * If [.PASTE_BEHAVIOR_USE_DEFAULT] is passed, whatever behavior is configured for a particular chip terminator
     * (through [.setChipTerminators] or [.addChipTerminator] will be used for that chip terminator
     *
     * @param pasteBehavior the behavior to use on a paste event
     */
    fun setPasteBehavior(pasteBehavior: Int)

    /**
     * Parses the provided text looking for characters marked as chip terminators through [.addChipTerminator] and [.setChipTerminators].
     * The provided [Editable] will be modified if chip terminators are encountered.
     *
     * @param tokenizer    the [ChipTokenizer] to use to identify and chipify tokens in the text
     * @param text         the text in which to search for chip terminators tokens to be chipped
     * @param start        the index at which to begin looking for chip terminators (inclusive)
     * @param end          the index at which to end looking for chip terminators (exclusive)
     * @param isPasteEvent true if this handling is for a paste event in which case the behavior set in [.setPasteBehavior] will be used,
     * otherwise false
     * @return an non-negative integer indicating the index where the cursor (selection) should be placed once the handling is complete,
     * or a negative integer indicating that the cursor should not be moved.
     */
    fun findAndHandleChipTerminators(tokenizer: ChipTokenizer, text: Editable, start: Int, end: Int, isPasteEvent: Boolean): Int

    companion object {

        /**
         * When a chip terminator character is encountered in newly inserted text, all tokens in the whole text view will be chipified
         */

        val BEHAVIOR_CHIPIFY_ALL = 0

        /**
         * When a chip terminator character is encountered in newly inserted text, only the current token (that in which the chip terminator character
         * was found) will be chipified. This token may extend beyond where the chip terminator character was located.
         */
        val BEHAVIOR_CHIPIFY_CURRENT_TOKEN = 1

        /**
         * When a chip terminator character is encountered in newly inserted text, only the text from the previous chip up until the chip terminator
         * character will be chipified. This may not be an entire token.
         */
        val BEHAVIOR_CHIPIFY_TO_TERMINATOR = 2

        /**
         * Constant for use with [.setPasteBehavior]. Use this if a paste should behave the same as a standard text input (the chip temrinators
         * will all behave according to their pre-determined behavior set through [.addChipTerminator] or [.setChipTerminators]).
         */
        val PASTE_BEHAVIOR_USE_DEFAULT = -1
    }
}
