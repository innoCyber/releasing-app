package ptml.releasing.app.views.chips

import android.content.res.ColorStateList

class ChipConfiguration
/**
 * Creates a new ChipConfiguration. You can pass in `-1` or `null` for any of the parameters to indicate that parameter should be
 * ignored.
 *
 * @param chipHorizontalSpacing         the amount of horizontal space (in pixels) to put between consecutive chips
 * @param chipBackground      the [ColorStateList] to set as the background of the chips
 * @param chipCornerRadius    the corner radius of the chip background, in pixels
 * @param chipTextColor       the color to set as the text color of the chips
 * @param chipTextSize        the font size (in pixels) to use for the text of the chips
 * @param chipHeight          the height (in pixels) of each chip
 * @param chipVerticalSpacing the amount of vertical space (in pixels) to put between chips on consecutive lines
 * @param maxAvailableWidth   the maximum available with for a chip (the width of a full line of text in the text view)
 */
internal constructor(val chipHorizontalSpacing: Int,
                     val chipBackground: ColorStateList,
                     val chipCornerRadius: Int,
                     val chipTextColor: Int,
                     val chipTextSize: Int,
                     val chipHeight: Int,
                     val chipVerticalSpacing: Int,
                     val maxAvailableWidth: Int)
