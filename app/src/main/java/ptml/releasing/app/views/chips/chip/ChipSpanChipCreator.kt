package ptml.releasing.app.views.chips.chip

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import ptml.releasing.app.views.chips.ChipConfiguration


class ChipSpanChipCreator : ChipCreator<ChipSpan> {

    override fun createChip(context: Context, text: CharSequence, data: Any?): ChipSpan {
        return ChipSpan(context, text, null, data)
    }

    override fun createChip(context: Context, existingChip: ChipSpan): ChipSpan {
        return ChipSpan(context, existingChip)
    }

    override fun configureChip(chip: ChipSpan, chipConfiguration: ChipConfiguration) {
        val chipHorizontalSpacing = chipConfiguration.chipHorizontalSpacing
        val chipBackground = chipConfiguration.chipBackground
        val chipCornerRadius = chipConfiguration.chipCornerRadius
        val chipTextColor = chipConfiguration.chipTextColor
        val chipTextSize = chipConfiguration.chipTextSize
        val chipHeight = chipConfiguration.chipHeight
        val chipVerticalSpacing = chipConfiguration.chipVerticalSpacing
        val maxAvailableWidth = chipConfiguration.maxAvailableWidth

        if (chipHorizontalSpacing != -1) {
            chip.setLeftMargin(chipHorizontalSpacing / 2)
            chip.setRightMargin(chipHorizontalSpacing / 2)
        }
        if (chipBackground != null) {
            chip.setBackgroundColor(chipBackground)
        }
        if (chipCornerRadius != -1) {
            chip.setCornerRadius(chipCornerRadius)
        }
        if (chipTextColor != Color.TRANSPARENT) {
            chip.setTextColor(chipTextColor)
        }
        if (chipTextSize != -1) {
            chip.setTextSize(chipTextSize)
        }
        if (chipHeight != -1) {
            chip.setChipHeight(chipHeight)
        }
        if (chipVerticalSpacing != -1) {
            chip.setChipVerticalSpacing(chipVerticalSpacing)
        }
        if (maxAvailableWidth != -1) {
            chip.setMaxAvailableWidth(maxAvailableWidth)
        }
    }
}
