package ptml.releasing.app.views.chips.chip

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import ptml.releasing.R

import android.text.style.ImageSpan


/**
 * A Span that displays text and an optional icon inside of a material design chip. The chip's dimensions, colors etc. can be extensively customized
 * through the various setter methods available in this class.
 * The basic structure of the chip is the following:
 * For chips with the icon on right:
 * <pre>
 *
 * (chip vertical spacing / 2)
 * ----------------------------------------------------------
 * |                                                            |
 * (left margin)  |  (padding edge)   text   (padding between image)   icon    |   (right margin)
 * |                                                            |
 * ----------------------------------------------------------
 * (chip vertical spacing / 2)
 *
</pre> *
 * For chips with the icon on the left (see [.setShowIconOnLeft]):
 * <pre>
 *
 * (chip vertical spacing / 2)
 * ----------------------------------------------------------
 * |                                                            |
 * (left margin)  |   icon  (padding between image)   text   (padding edge)    |   (right margin)
 * |                                                            |
 * ----------------------------------------------------------
 * (chip vertical spacing / 2)
</pre> *
 */
class ChipSpan
/**
 * Constructs a new ChipSpan.
 *
 * @param context a [Context] that will be used to retrieve default configurations from resource files
 * @param text    the text for the ChipSpan to display
 * @param icon    an optional icon (can be `null`) for the ChipSpan to display
 */
(context: Context, override val text: CharSequence, private val mIcon: Drawable?, override val data: Any?) : ImageSpan(mIcon), Chip {

    private var mStateSet = intArrayOf()

    private val mEllipsis: String

    private var mDefaultBackgroundColor: ColorStateList? = null
    private var mBackgroundColor: ColorStateList? = null
    private var mTextColor: Int = 0
    private var mCornerRadius = -1
    private var mIconBackgroundColor: Int = 0

    private var mTextSize = -1
    private var mPaddingEdgePx: Int = 0
    private var mPaddingBetweenImagePx: Int = 0
    private var mLeftMarginPx: Int = 0
    private var mRightMarginPx: Int = 0
    private var mMaxAvailableWidth = -1
    private var mTextToDraw: String? = null
    private var mShowIconOnLeft = ICON_ON_LEFT_DEFAULT

    private var mChipVerticalSpacing = 0
    private var mChipHeight = -1
    private var mChipWidth = -1
    private var mIconWidth: Int = 0

    private var mCachedSize = -1

    override// If we haven't actually calculated a chip width yet just return -1, otherwise return the chip width + margins
    val width: Int
        get() = if (mChipWidth != -1) mLeftMarginPx + mChipWidth + mRightMarginPx else -1

    init {
        mTextToDraw = this.text.toString()

        mEllipsis = context.getString(R.string.chip_ellipsis)

        mDefaultBackgroundColor = ContextCompat.getColorStateList(context, R.color.chip_material_background)
        mBackgroundColor = mDefaultBackgroundColor

        mTextColor = ContextCompat.getColor(context, R.color.chip_default_text_color)
        mIconBackgroundColor = ContextCompat.getColor(context, R.color.chip_default_icon_background_color)

        val resources = context.resources
        mPaddingEdgePx = resources.getDimensionPixelSize(R.dimen.chip_default_padding_edge)
        mPaddingBetweenImagePx = resources.getDimensionPixelSize(R.dimen.chip_default_padding_between_image)
        mLeftMarginPx = resources.getDimensionPixelSize(R.dimen.chip_default_left_margin)
        mRightMarginPx = resources.getDimensionPixelSize(R.dimen.chip_default_right_margin)
    }

    /**
     * Copy constructor to recreate a ChipSpan from an existing one
     *
     * @param context  a [Context] that will be used to retrieve default configurations from resource files
     * @param chipSpan the ChipSpan to copy
     */
    constructor(context: Context, chipSpan: ChipSpan) : this(context, chipSpan.text, chipSpan.drawable, chipSpan.data) {

        mDefaultBackgroundColor = chipSpan.mDefaultBackgroundColor
        mTextColor = chipSpan.mTextColor
        mIconBackgroundColor = chipSpan.mIconBackgroundColor
        mCornerRadius = chipSpan.mCornerRadius

        mTextSize = chipSpan.mTextSize
        mPaddingEdgePx = chipSpan.mPaddingEdgePx
        mPaddingBetweenImagePx = chipSpan.mPaddingBetweenImagePx
        mLeftMarginPx = chipSpan.mLeftMarginPx
        mRightMarginPx = chipSpan.mRightMarginPx
        mMaxAvailableWidth = chipSpan.mMaxAvailableWidth

        mShowIconOnLeft = chipSpan.mShowIconOnLeft

        mChipVerticalSpacing = chipSpan.mChipVerticalSpacing
        mChipHeight = chipSpan.mChipHeight

        mStateSet = chipSpan.mStateSet
    }

    /**
     * Sets the height of the chip. This height should not include any extra spacing (for extra vertical spacing call [.setChipVerticalSpacing]).
     * The background of the chip will fill the full height provided here. If this method is never called, the chip will have the height of one full line
     * of text by default. If `-1` is passed here, the chip will revert to this default behavior.
     *
     * @param chipHeight the height to set in pixels
     */
    fun setChipHeight(chipHeight: Int) {
        mChipHeight = chipHeight
    }

    /**
     * Sets the vertical spacing to include in between chips. Half of the value set here will be placed as empty space above the chip and half the value
     * will be placed as empty space below the chip. Therefore chips on consecutive lines will have the full value as vertical space in between them.
     * This spacing is achieved by adjusting the font metrics used by the text view containing these chips; however it does not come into effect until
     * at least one chip is created. Note that vertical spacing is dependent on having a fixed chip height (set in [.setChipHeight]). If a
     * height is not specified in that method, the value set here will be ignored.
     *
     * @param chipVerticalSpacing the vertical spacing to set in pixels
     */
    fun setChipVerticalSpacing(chipVerticalSpacing: Int) {
        mChipVerticalSpacing = chipVerticalSpacing
    }

    /**
     * Sets the font size for the chip's text. If this method is never called, the chip text will have the same font size as the text in the TextView
     * containing this chip by default. If `-1` is passed here, the chip will revert to this default behavior.
     *
     * @param size the font size to set in pixels
     */
    fun setTextSize(size: Int) {
        mTextSize = size
        invalidateCachedSize()
    }

    /**
     * Sets the color for the chip's text.
     *
     * @param color the color to set (as a hexadecimal number in the form 0xAARRGGBB)
     */
    fun setTextColor(color: Int) {
        mTextColor = color
    }

    /**
     * Sets where the icon (if an icon was provided in the constructor) will appear.
     *
     * @param showIconOnLeft if true, the icon will appear on the left, otherwise the icon will appear on the right
     */
    fun setShowIconOnLeft(showIconOnLeft: Boolean) {
        this.mShowIconOnLeft = showIconOnLeft
        invalidateCachedSize()
    }

    /**
     * Sets the left margin. This margin will appear as empty space (it will not share the chip's background color) to the left of the chip.
     *
     * @param leftMarginPx the left margin to set in pixels
     */
    fun setLeftMargin(leftMarginPx: Int) {
        mLeftMarginPx = leftMarginPx
        invalidateCachedSize()
    }

    /**
     * Sets the right margin. This margin will appear as empty space (it will not share the chip's background color) to the right of the chip.
     *
     * @param rightMarginPx the right margin to set in pixels
     */
    fun setRightMargin(rightMarginPx: Int) {
        this.mRightMarginPx = rightMarginPx
        invalidateCachedSize()
    }

    /**
     * Sets the background color. To configure which color in the [ColorStateList] is shown you can call [.setState].
     * Passing `null` here will cause the chip to revert to it's default background.
     *
     * @param backgroundColor a [ColorStateList] containing backgrounds for different states.
     * @see .setState
     */
    fun setBackgroundColor(backgroundColor: ColorStateList?) {
        mBackgroundColor = backgroundColor ?: mDefaultBackgroundColor
    }

    /**
     * Sets the chip background corner radius.
     *
     * @param cornerRadius The corner radius value, in pixels.
     */
    fun setCornerRadius(@Dimension cornerRadius: Int) {
        mCornerRadius = cornerRadius
    }

    /**
     * Sets the icon background color. This is the color of the circle that gets drawn behind the icon passed to the
     * [.ChipSpan]  constructor}
     *
     * @param iconBackgroundColor the icon background color to set (as a hexadecimal number in the form 0xAARRGGBB)
     */
    fun setIconBackgroundColor(iconBackgroundColor: Int) {
        mIconBackgroundColor = iconBackgroundColor
    }

    fun setMaxAvailableWidth(maxAvailableWidth: Int) {
        mMaxAvailableWidth = maxAvailableWidth
        invalidateCachedSize()
    }

    /**
     * Sets the UI state. This state will be reflected in the background color drawn for the chip.
     *
     * @param stateSet one of the state constants in [android.view.View]
     * @see .setBackgroundColor
     */
    override fun setState(stateSet: IntArray) {
        this.mStateSet = stateSet ?: intArrayOf()
    }

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val usingFontMetrics = fm != null

        // Adjust the font metrics regardless of whether or not there is a cached size so that the text view can maintain its height
        if (usingFontMetrics) {
            adjustFontMetrics(paint, fm!!)
        }

        if (mCachedSize == -1 && usingFontMetrics) {
            mIconWidth = if (mIcon != null) calculateChipHeight(fm!!.top, fm.bottom) else 0

            val actualWidth = calculateActualWidth(paint)
            mCachedSize = actualWidth

            if (mMaxAvailableWidth != -1) {
                val maxAvailableWidthMinusMargins = mMaxAvailableWidth - mLeftMarginPx - mRightMarginPx
                if (actualWidth > maxAvailableWidthMinusMargins) {
                    mTextToDraw = this.text.toString() + mEllipsis

                    while (calculateActualWidth(paint) > maxAvailableWidthMinusMargins && mTextToDraw!!.length > 0) {
                        val lastCharacterIndex = mTextToDraw!!.length - mEllipsis.length - 1
                        if (lastCharacterIndex < 0) {
                            break
                        }
                        mTextToDraw = mTextToDraw!!.substring(0, lastCharacterIndex) + mEllipsis
                    }

                    // Avoid a negative width
                    mChipWidth = Math.max(0, maxAvailableWidthMinusMargins)
                    mCachedSize = mMaxAvailableWidth
                }
            }
        }

        return mCachedSize
    }

    private fun calculateActualWidth(paint: Paint): Int {
        // Only change the text size if a text size was set
        if (mTextSize != -1) {
            paint.textSize = mTextSize.toFloat()
        }

        var totalPadding = mPaddingEdgePx

        // Find text width
        val bounds = Rect()
        paint.getTextBounds(mTextToDraw, 0, mTextToDraw!!.length, bounds)
        val textWidth = bounds.width()

        if (mIcon != null) {
            totalPadding += mPaddingBetweenImagePx
        } else {
            totalPadding += mPaddingEdgePx
        }

        mChipWidth = totalPadding + textWidth + mIconWidth
        return width
    }

    fun invalidateCachedSize() {
        mCachedSize = -1
    }

    /**
     * Adjusts the provided font metrics to make it seem like the font takes up `mChipHeight + mChipVerticalSpacing` pixels in height.
     * This effectively ensures that the TextView will have a height equal to `mChipHeight + mChipVerticalSpacing` + whatever padding it has set.
     * In [.draw] the chip itself is drawn to that it is vertically centered with
     * `mChipVerticalSpacing / 2` pixels of space above and below it
     *
     * @param paint the paint whose font metrics should be adjusted
     * @param fm    the font metrics object to populate through [Paint.getFontMetricsInt]
     */
    private fun adjustFontMetrics(paint: Paint, fm: Paint.FontMetricsInt) {
        // Only actually adjust font metrics if we have a chip height set
        if (mChipHeight != -1) {
            paint.getFontMetricsInt(fm)
            val textHeight = fm.descent - fm.ascent
            // Break up the vertical spacing in half because half will go above the chip, half will go below the chip
            val halfSpacing = mChipVerticalSpacing / 2

            // Given that the text is centered vertically within the chip, the amount of space above or below the text (inbetween the text and chip)
            // is half their difference in height:
            val spaceBetweenChipAndText = (mChipHeight - textHeight) / 2

            val textTop = fm.top
            val chipTop = fm.top - spaceBetweenChipAndText

            val textBottom = fm.bottom
            val chipBottom = fm.bottom + spaceBetweenChipAndText

            // The text may have been taller to begin with so we take the most negative coordinate (highest up) to be the top of the content
            val topOfContent = Math.min(textTop, chipTop)
            // Same as above but we want the largest positive coordinate (lowest down) to be the bottom of the content
            val bottomOfContent = Math.max(textBottom, chipBottom)

            // Shift the top up by halfSpacing and the bottom down by halfSpacing
            val topOfContentWithSpacing = topOfContent - halfSpacing
            val bottomOfContentWithSpacing = bottomOfContent + halfSpacing

            // Change the font metrics so that the TextView thinks the font takes up the vertical space of a chip + spacing
            fm.ascent = topOfContentWithSpacing
            fm.descent = bottomOfContentWithSpacing
            fm.top = topOfContentWithSpacing
            fm.bottom = bottomOfContentWithSpacing
        }
    }

    private fun calculateChipHeight(top: Int, bottom: Int): Int {
        // If a chip height was set we can return that, otherwise calculate it from top and bottom
        return if (mChipHeight != -1) mChipHeight else bottom - top
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        var x = x
        var top = top
        var bottom = bottom
        // Shift everything mLeftMarginPx to the left to create an empty space on the left (creating the margin)
        x += mLeftMarginPx.toFloat()
        if (mChipHeight != -1) {
            // If we set a chip height, adjust to vertically center chip in the line
            // Adding (bottom - top) / 2 shifts the chip down so the top of it will be centered vertically
            // Subtracting (mChipHeight / 2) shifts the chip back up so that the center of it will be centered vertically (as desired)
            top += (bottom - top) / 2 - mChipHeight / 2
            bottom = top + mChipHeight
        }

        // Perform actual drawing
        drawBackground(canvas, x, top, bottom, paint)
        drawText(canvas, x, top, bottom, paint, mTextToDraw)
        if (mIcon != null) {
            drawIcon(canvas, x, top, bottom, paint)
        }
    }

    private fun drawBackground(canvas: Canvas, x: Float, top: Int, bottom: Int, paint: Paint) {
        val backgroundColor = mBackgroundColor!!.getColorForState(mStateSet, mBackgroundColor!!.defaultColor)
        paint.color = backgroundColor
        val height = calculateChipHeight(top, bottom)
        val rect = RectF(x, top.toFloat(), x + mChipWidth, bottom.toFloat())
        val cornerRadius = if (mCornerRadius != -1) mCornerRadius else height / 2
        canvas.drawRoundRect(rect, cornerRadius.toFloat(), cornerRadius.toFloat(), paint)
        paint.color = mTextColor
    }

    private fun drawText(canvas: Canvas, x: Float, top: Int, bottom: Int, paint: Paint, text: CharSequence?) {
        if (mTextSize != -1) {
            paint.textSize = mTextSize.toFloat()
        }
        val height = calculateChipHeight(top, bottom)
        val fm = paint.fontMetrics

        // The top value provided here is the y coordinate for the very top of the chip
        // The y coordinate we are calculating is where the baseline of the text will be drawn
        // Our objective is to have the midpoint between the top and baseline of the text be in line with the vertical center of the chip
        // First we add height / 2 which will put the baseline at the vertical center of the chip
        // Then we add half the height of the text which will lower baseline so that the midpoint is at the vertical center of the chip as desired
        val adjustedY = top + (height / 2 + (-fm.top - fm.bottom) / 2)

        // The x coordinate provided here is the left-most edge of the chip
        // If there is no icon or the icon is on the right, then the text will start at the left-most edge, but indented with the edge padding, so we
        // add mPaddingEdgePx
        // If there is an icon and it's on the left, the text will start at the left-most edge, but indented by the combined width of the icon and
        // the padding between the icon and text, so we add (mIconWidth + mPaddingBetweenImagePx)
        val adjustedX = x + if (mIcon == null || !mShowIconOnLeft) mPaddingEdgePx else mIconWidth + mPaddingBetweenImagePx

        canvas.drawText(text!!, 0, text.length, adjustedX, adjustedY, paint)
    }

    private fun drawIcon(canvas: Canvas, x: Float, top: Int, bottom: Int, paint: Paint) {
        drawIconBackground(canvas, x, top, bottom, paint)
        drawIconBitmap(canvas, x, top, bottom, paint)
    }

    private fun drawIconBackground(canvas: Canvas, x: Float, top: Int, bottom: Int, paint: Paint) {
        val height = calculateChipHeight(top, bottom)

        paint.color = mIconBackgroundColor

        // Since it's a circle the diameter is equal to the height, so the radius == diameter / 2 == height / 2
        val radius = height / 2
        // The coordinates that get passed to drawCircle are for the center of the circle
        // x is the left edge of the chip, (x + mChipWidth) is the right edge of the chip
        // So the center of the circle is one radius distance from either the left or right edge (depending on which side the icon is being drawn on)
        val circleX = if (mShowIconOnLeft) x + radius else x + mChipWidth - radius
        // The y coordinate is always just one radius distance from the top
        canvas.drawCircle(circleX, (top + radius).toFloat(), radius.toFloat(), paint)

        paint.color = mTextColor
    }

    private fun drawIconBitmap(canvas: Canvas, x: Float, top: Int, bottom: Int, paint: Paint) {
        val height = calculateChipHeight(top, bottom)

        // Create a scaled down version of the bitmap to fit within the circle (whose diameter == height)
        val iconBitmap = Bitmap.createBitmap(mIcon!!.intrinsicWidth, mIcon.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val scaledIconBitMap = scaleDown(iconBitmap, height.toFloat() * SCALE_PERCENT_OF_CHIP_HEIGHT, true)
        iconBitmap.recycle()
        val bitmapCanvas = Canvas(scaledIconBitMap)
        mIcon.setBounds(0, 0, bitmapCanvas.width, bitmapCanvas.height)
        mIcon.draw(bitmapCanvas)

        // We are drawing a square icon inside of a circle
        // The coordinates we pass to canvas.drawBitmap have to be for the top-left corner of the bitmap
        // The bitmap should be inset by half of (circle width - bitmap width)
        // Since it's a circle, the circle's width is equal to it's height which is equal to the chip height
        val xInsetWithinCircle = ((height - bitmapCanvas.width) / 2).toFloat()

        // The icon x coordinate is going to be insetWithinCircle pixels away from the left edge of the circle
        // If the icon is on the left, the left edge of the circle is just x
        // If the icon is on the right, the left edge of the circle is x + mChipWidth - height
        val iconX = if (mShowIconOnLeft) x + xInsetWithinCircle else x + mChipWidth - height + xInsetWithinCircle

        // The y coordinate works the same way (only it's always from the top edge)
        val yInsetWithinCircle = ((height - bitmapCanvas.height) / 2).toFloat()
        val iconY = top + yInsetWithinCircle

        canvas.drawBitmap(scaledIconBitMap, iconX, iconY, paint)
    }

    private fun scaleDown(realImage: Bitmap, maxImageSize: Float, filter: Boolean): Bitmap {
        val ratio = Math.min(maxImageSize / realImage.width, maxImageSize / realImage.height)
        val width = Math.round(ratio * realImage.width)
        val height = Math.round(ratio * realImage.height)
        return Bitmap.createScaledBitmap(realImage, width, height, filter)
    }

    override fun toString(): String {
        return text.toString()
    }

    companion object {

        private val SCALE_PERCENT_OF_CHIP_HEIGHT = 0.70f
        private val ICON_ON_LEFT_DEFAULT = true
    }
}
