package ptml.releasing.app.views.chips.validator

import ptml.releasing.app.views.chips.tokenizer.ChipTokenizer

/**
 * Interface used to ensure that a given CharSequence complies to a particular format.
 */
interface ChipValidator {

    /**
     * Validates the specified text.
     *
     * @return true If the text currently in the text editor is valid.
     * @see .fixText
     */
    fun isValid(chipTokenizer: ChipTokenizer, text: CharSequence): Boolean

    /**
     * Corrects the specified text to make it valid.
     *
     * @param invalidText A string that doesn't pass validation: isValid(invalidText)
     * returns false
     * @return A string based on invalidText such as invoking isValid() on it returns true.
     * @see .isValid
     */
    fun fixText(chipTokenizer: ChipTokenizer, invalidText: CharSequence): CharSequence
}
