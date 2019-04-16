package ptml.releasing.app.views.chips.chip

import android.content.Context
import ptml.releasing.app.views.chips.ChipConfiguration


/**
 * Interface to allow the creation and configuration of chips
 *
 * @param <C> The type of [Chip] that the implementation will create/configure
</C> */
interface ChipCreator<C : Chip> {

    /**
     * Creates a chip from the given context and text. Use this method when creating a brand new chip from a piece of text.
     *
     * @param context the [Context] to use to initialize the chip
     * @param text    the text the Chip should represent
     * @param data    the data to associate with the Chip, or null to associate no data
     * @return the created chip
     */
    fun createChip(context: Context, text: CharSequence, data: Any?): C

    /**
     * Creates a chip from the given context and existing chip. Use this method when recreating a chip from an existing one.
     *
     * @param context      the [Context] to use to initialize the chip
     * @param existingChip the chip that the created chip should be based on
     * @return the created chip
     */
    fun createChip(context: Context, existingChip: C): C

    /**
     * Applies the given [ChipConfiguration] to the given [Chip]. Use this method to customize the appearance/behavior of a chip before
     * adding it to the text.
     *
     * @param chip              the chip to configure
     * @param chipConfiguration the configuration to apply to the chip
     */
    fun configureChip(chip: C, chipConfiguration: ChipConfiguration)
}
