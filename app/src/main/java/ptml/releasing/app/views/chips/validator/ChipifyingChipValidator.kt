package ptml.releasing.app.views.chips.validator

import android.text.SpannableStringBuilder

import ptml.releasing.app.views.chips.tokenizer.ChipTokenizer

/**
 * A [ChipValidator] that deems text to be invalid if it contains
 * unterminated tokens and fixes the text by chipifying all the unterminated tokens.
 */
class ChipifyingChipValidator : ChipValidator {

    override fun isValid(chipTokenizer: ChipTokenizer, text: CharSequence): Boolean {

        // The text is considered valid if there are no unterminated tokens (everything is a chip)
        val unterminatedTokens = chipTokenizer.findAllTokens(text)
        return unterminatedTokens.isEmpty()
    }

    override fun fixText(chipTokenizer: ChipTokenizer, invalidText: CharSequence): CharSequence {
        val newText = SpannableStringBuilder(invalidText)
        chipTokenizer.terminateAllTokens(newText)
        return newText
    }
}
