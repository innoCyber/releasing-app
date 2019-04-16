package ptml.releasing.app.views.chips.tokenizer

import android.text.Editable
import android.text.Spanned
import android.util.Pair

import ptml.releasing.app.views.chips.ChipConfiguration
import ptml.releasing.app.views.chips.chip.Chip

import java.util.ArrayList

/**
 * Base implementation of the [ChipTokenizer] interface that performs no actions and returns default values.
 * This class allows for the easy creation of a ChipTokenizer that only implements some of the methods of the interface.
 */
abstract class BaseChipTokenizer : ChipTokenizer {

    override fun applyConfiguration(text: Editable, chipConfiguration: ChipConfiguration) {
        // Do nothing
    }

    override fun findTokenStart(charSequence: CharSequence, i: Int): Int {
        // Do nothing
        return 0
    }

    override fun findTokenEnd(charSequence: CharSequence, i: Int): Int {
        // Do nothing
        return 0
    }

    override fun findAllTokens(text: CharSequence): List<Pair<Int, Int>> {
        // Do nothing
        return ArrayList()
    }

    override fun terminateToken(charSequence: CharSequence, data: Any?): CharSequence {
        // Do nothing
        return charSequence
    }

    override fun terminateAllTokens(text: Editable) {
        // Do nothing
    }

    override fun findChipStart(chip: Chip, text: Spanned): Int {
        // Do nothing
        return 0
    }

    override fun findChipEnd(chip: Chip, text: Spanned): Int {
        // Do nothing
        return 0
    }

    override fun findAllChips(start: Int, end: Int, text: Spanned): Array<Chip> {
        return arrayOf()
    }

    override fun revertChipToToken(chip: Chip, text: Editable) {
        // Do nothing
    }

    override fun deleteChip(chip: Chip, text: Editable) {
        // Do nothing
    }

    override fun deleteChipAndPadding(chip: Chip, text: Editable) {
        // Do nothing
    }
}
