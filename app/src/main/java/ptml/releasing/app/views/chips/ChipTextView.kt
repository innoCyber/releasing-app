package ptml.releasing.app.views.chips

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension

import android.text.Editable
import android.text.Layout
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Pair
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ListAdapter


import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView
import androidx.core.content.ContextCompat
import ptml.releasing.R
import ptml.releasing.app.views.chips.chip.Chip
import ptml.releasing.app.views.chips.chip.ChipInfo
import ptml.releasing.app.views.chips.chip.ChipSpan
import ptml.releasing.app.views.chips.chip.ChipSpanChipCreator
import ptml.releasing.app.views.chips.terminator.ChipTerminatorHandler
import ptml.releasing.app.views.chips.terminator.DefaultChipTerminatorHandler
import ptml.releasing.app.views.chips.tokenizer.ChipTokenizer
import ptml.releasing.app.views.chips.tokenizer.SpanChipTokenizer
import ptml.releasing.app.views.chips.validator.ChipifyingChipValidator
import ptml.releasing.app.views.chips.validator.IllegalCharacterIdentifier
import ptml.releasing.app.views.chips.validator.ChipValidator
import timber.log.Timber

import java.util.ArrayList
import java.util.Arrays
import java.util.Collections

/**
 * An editable TextView extending [AppCompatMultiAutoCompleteTextView] that supports "chipifying" pieces of text and displaying suggestions for segments of the text.
 * <h1>The ChipTokenizer</h1>
 * To customize chipifying with this class you can provide a custom [ChipTokenizer] by calling [.setChipTokenizer].
 * By default the [SpanChipTokenizer] is used.
 * <h1>Chip Terminators</h1>
 * To set which characters trigger the creation of a chip, call [.addChipTerminator] or [.setChipTerminators].
 * For example if tapping enter should cause all unchipped text to become chipped, call
 * `chipSuggestionTextView.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);`
 * To completely customize how chips are created when text is entered in this text view you can provide a custom [ChipTerminatorHandler]
 * through [.setChipTerminatorHandler]
 * <h1>Illegal Characters</h1>
 * To prevent a character from being typed you can call [.setIllegalCharacterIdentifier]} to identify characters
 * that should be considered illegal.
 * <h1>Suggestions</h1>
 * To provide suggestions you must provide an [Adapter] by calling [.setAdapter]
 * <h1>UI Customization</h1>
 * This view defines six custom attributes (all of which are optional):
 *
 *  * chipHorizontalSpacing - the horizontal space between chips
 *  * chipBackground - the background color of the chip
 *  * chipCornerRadius - the corner radius of the chip background
 *  * chipTextColor - the color of the chip text
 *  * chipTextSize - the font size of the chip text
 *  * chipHeight - the height of a single chip
 *  * chipVerticalSpacing - the vertical space between chips on consecutive lines
 *
 *  * Note: chipVerticalSpacing is only used if a chipHeight is also set
 *
 *
 *
 * The values of these attributes will be passed to the ChipTokenizer through [ChipTokenizer.applyConfiguration]
 * <h1>Validation</h1>
 * This class can perform validation when certain events occur (such as losing focus). When the validation occurs is decided by
 * [AutoCompleteTextView]. To perform validation, set a [ChipValidator]:
 * <pre>
 * nachoTextView.setNachoValidator(new ChipifyingChipValidator());
</pre> *
 * Note: The ChipValidator will be ignored if a ChipTokenizer is not set. To perform validation without a ChipTokenizer you can use
 * [AutoCompleteTextView]'s built-in [Validator] through [.setValidator]
 * <h1>Editing Chips</h1>
 * This class also supports editing chips on touch. To enable this behavior call [.enableEditChipOnTouch]. To disable this
 * behavior you can call [.disableEditChipOnTouch]
 * <h1>Example Setup:</h1>
 * A standard setup for this class could look something like the following:
 * <pre>
 * String[] suggestions = new String[]{"suggestion 1", "suggestion 2"};
 * ArrayAdapter&lt;String&gt; adapter = new ArrayAdapter&lt;&gt;(this, android.R.layout.simple_dropdown_item_1line, suggestions);
 * nachoTextView.setAdapter(adapter);
 * nachoTextView.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);
 * nachoTextView.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
 * nachoTextView.setIllegalCharacters('@');
 * nachoTextView.setNachoValidator(new ChipifyingChipValidator());
 * nachoTextView.enableEditChipOnTouch(true, true);
 * nachoTextView.setOnChipClickListener(new ChipTextView.OnChipClickListener() {
 * @Override
 * public void onChipClick(Chip chip, MotionEvent motionEvent) {
 * // Handle click event
 * }
 * });
 * nachoTextView.setOnChipRemoveListener(new ChipTextView.OnChipRemoveListener() {
 * @Override
 * public void onChipRemove(Chip chip) {
 * // Handle remove event
 * }
 * });
</pre> *
 *
 * @see SpanChipTokenizer
 *
 * @see DefaultChipTerminatorHandler
 *
 * @see ChipifyingChipValidator
 */
class ChipTextView : AppCompatMultiAutoCompleteTextView, TextWatcher, AdapterView.OnItemClickListener {

    // UI Attributes
    private var mChipHorizontalSpacing = -1
    private var mChipBackground: ColorStateList? = null
    private var mChipCornerRadius = -1
    private var mChipTextColor = Color.TRANSPARENT
    private var mChipTextSize = -1
    private var mChipHeight = -1
    private var mChipVerticalSpacing = -1

    private var mDefaultPaddingTop = 0
    private var mDefaultPaddingBottom = 0
    /**
     * Flag to keep track of the padding state so we only update the padding when necessary
     */
    private var mUsingDefaultPadding = true

    // Touch events
    private var mOnChipClickListener: OnChipClickListener? = null
    private var singleTapDetector: GestureDetector? = null
    private var mEditChipOnTouchEnabled: Boolean = false
    private var mMoveChipToEndOnEdit: Boolean = false
    private var mChipifyUnterminatedTokensOnEdit: Boolean = false

    // Text entry
    /**
     * Sets the [ChipTokenizer] to be used by this ChipSuggestionTextView.
     * Note that a Tokenizer set here will override any Tokenizer set by [.setTokenizer]
     *
     * @param chipTokenizer the [ChipTokenizer] to set
     */
    var chipTokenizer: ChipTokenizer? = null
        set(chipTokenizer) {
            field = chipTokenizer
            if (this.chipTokenizer != null) {
                setTokenizer(ChipTokenizerWrapper(this.chipTokenizer!!))
            } else {
                setTokenizer(null)
            }
            invalidateChips()
        }
    private var mChipTerminatorHandler: ChipTerminatorHandler? = null
    private var mNachoValidator: ChipValidator? = null
    private var illegalCharacterIdentifier: IllegalCharacterIdentifier? = null

    private var mOnChipRemoveListener: OnChipRemoveListener? = null
    private val mChipsToRemove = ArrayList<Chip>()
    private var mIgnoreTextChangedEvents: Boolean = false
    private var mTextChangedStart: Int = 0
    private var mTextChangedEnd: Int = 0
    private var mIsPasteEvent: Boolean = false

    // Measurement
    private var mMeasured: Boolean = false

    // Layout
    private var mLayoutComplete: Boolean = false

    var chipHorizontalSpacing: Int
        get() = mChipHorizontalSpacing
        set(@DimenRes chipHorizontalSpacingResId) {
            mChipHorizontalSpacing = context.resources.getDimensionPixelSize(chipHorizontalSpacingResId)
            invalidateChips()
        }

    var chipBackground: ColorStateList?
        get() = mChipBackground
        set(chipBackground) {
            mChipBackground = chipBackground
            invalidateChips()
        }

    /**
     * @return The chip background corner radius value, in pixels.
     */
    /**
     * Sets the chip background corner radius.
     *
     * @param chipCornerRadius The corner radius value, in pixels.
     */
    var chipCornerRadius: Int
        @Dimension
        get() = mChipCornerRadius
        set(@Dimension chipCornerRadius) {
            mChipCornerRadius = chipCornerRadius
            invalidateChips()
        }


    var chipTextColor: Int
        get() = mChipTextColor
        set(@ColorInt chipTextColor) {
            mChipTextColor = chipTextColor
            invalidateChips()
        }

    var chipTextSize: Int
        get() = mChipTextSize
        set(@DimenRes chipTextSizeResId) {
            mChipTextSize = context.resources.getDimensionPixelSize(chipTextSizeResId)
            invalidateChips()
        }

    var chipHeight: Int
        get() = mChipHeight
        set(@DimenRes chipHeightResId) {
            mChipHeight = context.resources.getDimensionPixelSize(chipHeightResId)
            invalidateChips()
        }

    var chipVerticalSpacing: Int
        get() = mChipVerticalSpacing
        set(@DimenRes chipVerticalSpacingResId) {
            mChipVerticalSpacing = context.resources.getDimensionPixelSize(chipVerticalSpacingResId)
            invalidateChips()
        }

    /**
     * @return all of the chips currently in the text view - this does not include any unchipped text
     */
    val allChips: List<Chip>
        get() {
            val text = text
            return if (chipTokenizer != null) Arrays.asList(*chipTokenizer!!.findAllChips(0, text.length, text)) else ArrayList()
        }

    /**
     * Returns a List of the string values of all the chips in the text (obtained through [Chip.getText]).
     * This does not include the text of any unterminated tokens.
     *
     * @return the List of chip values
     */
    val chipValues: List<String>
        get() {
            val chipValues = ArrayList<String>()

            val chips = allChips
            for (chip in chips) {
                chipValues.add(chip.text.toString())
            }

            return chipValues
        }

    /**
     * Returns a List of the string values of all the tokens (unchipped text) in the text
     * (obtained through [ChipTokenizer.findAllTokens]). This does not include any chipped text.
     *
     * @return the List of token values
     */
    val tokenValues: List<String>
        get() {
            val tokenValues = ArrayList<String>()

            if (chipTokenizer != null) {
                val text = text
                val unterminatedTokenIndexes = chipTokenizer!!.findAllTokens(text)
                for (indexes in unterminatedTokenIndexes) {
                    val tokenValue = text.subSequence(indexes.first, indexes.second).toString()
                    tokenValues.add(tokenValue)
                }
            }

            return tokenValues
        }

    /**
     * Returns a combination of the chip values and token values in the text.
     *
     * @return the List of all chip and token values
     * @see .getChipValues
     * @see .getTokenValues
     */
    val chipAndTokenValues: List<String>
        get() {
            val chipAndTokenValues = ArrayList<String>()
            chipAndTokenValues.addAll(chipValues)
            chipAndTokenValues.addAll(tokenValues)
            return chipAndTokenValues
        }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val context = context

        if (attrs != null) {
            val attributes = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.ChipTextView,
                    0,
                    R.style.DefaultChipSuggestionTextView)

            try {
                mChipHorizontalSpacing = attributes.getDimensionPixelSize(R.styleable.ChipTextView_chipHorizontalSpacing, -1)
                mChipBackground = attributes.getColorStateList(R.styleable.ChipTextView_chipBackground)
                mChipCornerRadius = attributes.getDimensionPixelSize(R.styleable.ChipTextView_chipCornerRadius, -1)
                mChipTextColor = attributes.getColor(R.styleable.ChipTextView_chipTextColor, Color.TRANSPARENT)
                mChipTextSize = attributes.getDimensionPixelSize(R.styleable.ChipTextView_chipTextSize, -1)
                mChipHeight = attributes.getDimensionPixelSize(R.styleable.ChipTextView_chipHeight, -1)
                mChipVerticalSpacing = attributes.getDimensionPixelSize(R.styleable.ChipTextView_chipVerticalSpacing, -1)
            } finally {
                attributes.recycle()
            }
        }

        mDefaultPaddingTop = paddingTop
        mDefaultPaddingBottom = paddingBottom

        singleTapDetector = GestureDetector(getContext(), SingleTapListener())

        imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN
        addTextChangedListener(this)
        chipTokenizer = SpanChipTokenizer(context, ChipSpanChipCreator(), ChipSpan::class.java)
        setChipTerminatorHandler(DefaultChipTerminatorHandler())
        onItemClickListener = this

        updatePadding()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (!mMeasured && width > 0) {
            // Refresh the tokenizer for width changes
            invalidateChips()
            mMeasured = true
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (!mLayoutComplete) {
            invalidateChips()
            mLayoutComplete = true
        }
    }

    /**
     * Updates the padding based on whether or not any chips are present to avoid the view from changing heights when chips are inserted/deleted.
     * Extra padding is added when there are no chips. When there are chips the padding is reverted to its defaults. This only affects top and bottom
     * padding because the chips only affect the height of the view.
     */
    private fun updatePadding() {
        if (mChipHeight != -1) {
            val chipsArePresent = !allChips.isEmpty()
            if (!chipsArePresent && mUsingDefaultPadding) {
                mUsingDefaultPadding = false
                val paint = paint
                val fm = paint.fontMetricsInt
                val textHeight = fm.descent - fm.ascent
                // Calculate how tall the view should be if there were chips
                val newTextHeight = mChipHeight + if (mChipVerticalSpacing != -1) mChipVerticalSpacing else 0
                // We need to add half our missing height above and below the text by increasing top and bottom padding
                val paddingAdjustment = (newTextHeight - textHeight) / 2
                super.setPadding(paddingLeft, mDefaultPaddingTop + paddingAdjustment, paddingRight, mDefaultPaddingBottom + paddingAdjustment)
            } else if (chipsArePresent && !mUsingDefaultPadding) {
                // If there are chips we can revert to default padding
                mUsingDefaultPadding = true
                super.setPadding(paddingLeft, mDefaultPaddingTop, paddingRight, mDefaultPaddingBottom)
            }
        }
    }

    /**
     * Sets the padding on this View. The left and right padding will be handled as they normally would in a TextView. The top and bottom padding passed
     * here will be the padding that is used when there are one or more chips in the text view. When there are no chips present, the padding will be
     * increased to make sure the overall height of the text view stays the same, since chips take up more vertical space than plain text.
     *
     * @param left   the left padding in pixels
     * @param top    the top padding in pixels
     * @param right  the right padding in pixels
     * @param bottom the bottom padding in pixels
     */
    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        // Call the super method so that left and right padding are updated
        // top and bottom padding will be handled in updatePadding()
        super.setPadding(left, top, right, bottom)
        mDefaultPaddingTop = top
        mDefaultPaddingBottom = bottom
        updatePadding()
    }

    fun setChipBackgroundResource(@ColorRes chipBackgroundResId: Int) {
        chipBackground = ContextCompat.getColorStateList(context, chipBackgroundResId)
    }

    /**
     * Sets the chip background corner radius.
     *
     * @param chipCornerRadiusResId The dimension resource with the corner radius value.
     */
    fun setChipCornerRadiusResource(@DimenRes chipCornerRadiusResId: Int) {
        chipCornerRadius = context.resources.getDimensionPixelSize(chipCornerRadiusResId)
    }

    fun setChipTextColorResource(@ColorRes chipTextColorResId: Int) {
        chipTextColor = ContextCompat.getColor(context, chipTextColorResId)
    }

    fun setOnChipClickListener(onChipClickListener: OnChipClickListener?) {
        mOnChipClickListener = onChipClickListener
    }

    fun setOnChipRemoveListener(onChipRemoveListener: OnChipRemoveListener?) {
        mOnChipRemoveListener = onChipRemoveListener
    }

    fun setChipTerminatorHandler(chipTerminatorHandler: ChipTerminatorHandler?) {
        mChipTerminatorHandler = chipTerminatorHandler
    }

    fun setNachoValidator(nachoValidator: ChipValidator?) {
        mNachoValidator = nachoValidator
    }

    /**
     * @see ChipTerminatorHandler.setChipTerminators
     */
    fun setChipTerminators(chipTerminators: Map<Char, Int>?) {
        if (mChipTerminatorHandler != null) {
            mChipTerminatorHandler!!.setChipTerminators(chipTerminators)
        }
    }

    /**
     * @see ChipTerminatorHandler.addChipTerminator
     */
    fun addChipTerminator(character: Char, behavior: Int) {
        if (mChipTerminatorHandler != null) {
            mChipTerminatorHandler!!.addChipTerminator(character, behavior)
        }
    }

    /**
     * @see ChipTerminatorHandler.setPasteBehavior
     */
    fun setPasteBehavior(pasteBehavior: Int) {
        if (mChipTerminatorHandler != null) {
            mChipTerminatorHandler!!.setPasteBehavior(pasteBehavior)
        }
    }

    /**
     * Sets the [IllegalCharacterIdentifier] that will identify characters that should
     * not show up in the field when typed (i.e. they will be deleted as soon as they are entered).
     * If a character is listed as both a chip terminator character and an illegal character,
     * it will be treated as an illegal character.
     *
     * @param illegalCharacterIdentifier the identifier to use
     */
    fun setIllegalCharacterIdentifier(illegalCharacterIdentifier: IllegalCharacterIdentifier?) {
        this.illegalCharacterIdentifier = illegalCharacterIdentifier
    }

    /**
     * Applies any updated configuration parameters to any existing chips and all future chips in the text view.
     *
     * @see ChipTokenizer.applyConfiguration
     */
    fun invalidateChips() {
        beginUnwatchedTextChange()

        if (chipTokenizer != null) {
            val text = text
            val availableWidth = width - compoundPaddingLeft - compoundPaddingRight
            val configuration = ChipConfiguration(
                    mChipHorizontalSpacing,
                    mChipBackground!!,
                    mChipCornerRadius,
                    mChipTextColor,
                    mChipTextSize,
                    mChipHeight,
                    mChipVerticalSpacing,
                    availableWidth)

            chipTokenizer!!.applyConfiguration(text, configuration)
        }

        endUnwatchedTextChange()
    }

    /**
     * Enables editing chips on touch events. When a touch event occurs, the touched chip will be put in editing mode. To later disable this behavior
     * call [.disableEditChipOnTouch].
     *
     *
     * Note: If an [OnChipClickListener] is set it's behavior will override the behavior described here if it's
     * [OnChipClickListener.onChipClick] method returns true. If that method returns false, the touched chip will be put
     * in editing mode as expected.
     *
     *
     * @param moveChipToEnd             if true, the chip will also be moved to the end of the text when it is put in editing mode
     * @param chipifyUnterminatedTokens if true, all unterminated tokens will be chipified before the touched chip is put in editing mode
     * @see .disableEditChipOnTouch
     */
    fun enableEditChipOnTouch(moveChipToEnd: Boolean, chipifyUnterminatedTokens: Boolean) {
        mEditChipOnTouchEnabled = true
        mMoveChipToEndOnEdit = moveChipToEnd
        mChipifyUnterminatedTokensOnEdit = chipifyUnterminatedTokens
    }

    /**
     * Disables editing chips on touch events. To re-enable this behavior call [.enableEditChipOnTouch].
     *
     * @see .enableEditChipOnTouch
     */
    fun disableEditChipOnTouch() {
        mEditChipOnTouchEnabled = false
    }

    /**
     * Puts the provided Chip in editing mode (i.e. reverts it to an unchipified token whose text can be edited).
     *
     * @param chip          the chip to edit
     * @param moveChipToEnd if true, the chip will also be moved to the end of the text
     */
    fun setEditingChip(chip: Chip, moveChipToEnd: Boolean) {
        if (chipTokenizer == null) {
            return
        }

        beginUnwatchedTextChange()

        val text = text
        if (moveChipToEnd) {
            // Move the chip text to the end of the text
            text.append(chip.text)
            // Delete the existing chip
            chipTokenizer!!.deleteChipAndPadding(chip, text)
            // Move the cursor to the end of the text
            setSelection(text.length)
        } else {
            val chipStart = chipTokenizer!!.findChipStart(chip, text)
            chipTokenizer!!.revertChipToToken(chip, text)
            setSelection(chipTokenizer!!.findTokenEnd(text, chipStart))
        }

        endUnwatchedTextChange()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var wasHandled = false
        clearChipStates()
        val touchedChip = findTouchedChip(event)
        if (touchedChip != null && isFocused && singleTapDetector!!.onTouchEvent(event)) {
            touchedChip.setState(View.PRESSED_SELECTED_STATE_SET)
            if (onChipClicked(touchedChip)) {
                wasHandled = true
            }
            if (mOnChipClickListener != null) {
                mOnChipClickListener!!.onChipClick(touchedChip, event)
            }
        }

        // Getting NullPointerException inside Editor.updateFloatingToolbarVisibility (Editor.java:1520)
        // primarily seen in Samsung Nougat devices.
        var superOnTouch = false
        try {
            superOnTouch = super.onTouchEvent(event)
        } catch (e: NullPointerException) {
            Timber.w(e, "Error during touch event of type [%d]", event.action)
            // can't handle or reproduce, but will monitor the error
        }

        return wasHandled || superOnTouch
    }

    private fun findTouchedChip(event: MotionEvent): Chip? {
        if (chipTokenizer == null) {
            return null
        }

        val text = text
        val offset = getOffsetForPosition(event.x, event.y)
        val chips = allChips
        for (chip in chips) {
            val chipStart = chipTokenizer!!.findChipStart(chip, text)
            val chipEnd = chipTokenizer!!.findChipEnd(chip, text) // This is actually the index of the character just past the end of the chip
            // When a touch event occurs getOffsetForPosition will either return the index of the first character of the span or the index of the
            // character one past the end of the span
            // This matches up perfectly with chipStart and chipEnd so we can just directly compare them...
            if (chipStart <= offset && offset <= chipEnd) {
                val startX = getXForIndex(chipStart)
                val endX = getXForIndex(chipEnd - 1)
                val eventX = event.x
                // ... however, when comparing the x coordinate we need to use (chipEnd - 1) because chipEnd will give us the x coordinate of the
                // beginning of the next span since that is actually what chipEnd holds. We want the x coordinate of the end of the current span so
                // we use (chipEnd - 1)
                if (startX <= eventX && eventX <= endX) {
                    return chip
                }
            }
        }
        return null
    }

    /**
     * Implement this method to handle chip clicked events.
     *
     * @param chip the chip that was clicked
     * @return true if the event was handled, otherwise false
     */
    fun onChipClicked(chip: Chip): Boolean {
        var wasHandled = false
        if (mEditChipOnTouchEnabled) {
            if (mChipifyUnterminatedTokensOnEdit) {
                chipifyAllUnterminatedTokens()
            }
            setEditingChip(chip, mMoveChipToEndOnEdit)
            wasHandled = true
        }
        return wasHandled
    }

    private fun getXForIndex(index: Int): Float {
        val layout = layout
        return layout.getPrimaryHorizontal(index)
    }

    private fun clearChipStates() {
        for (chip in allChips) {
            chip.setState(View.EMPTY_STATE_SET)
        }
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        val start = selectionStart
        val end = selectionEnd
        when (id) {
            android.R.id.cut -> {
                try {
                    setClipboardData(ClipData.newPlainText(null, getTextWithPlainTextSpans(start, end)))
                } catch (e: StringIndexOutOfBoundsException) {
                    throw StringIndexOutOfBoundsException(
                            String.format(
                                    "%s \nError cutting text index [%s, %s] for text [%s] and substring [%s]",
                                    e.message,
                                    start,
                                    end,
                                    text.toString(),
                                    text.subSequence(start, end)))
                }

                text.delete(selectionStart, selectionEnd)
                return true
            }
            android.R.id.copy -> {
                try {
                    setClipboardData(ClipData.newPlainText(null, getTextWithPlainTextSpans(start, end)))
                } catch (e: StringIndexOutOfBoundsException) {
                    throw StringIndexOutOfBoundsException(
                            String.format(
                                    "%s \nError copying text index [%s, %s] for text [%s] and substring [%s]",
                                    e.message,
                                    start,
                                    end,
                                    text.toString(),
                                    text.subSequence(start, end)))
                }

                return true
            }
            android.R.id.paste -> {
                mIsPasteEvent = true
                val returnValue = super.onTextContextMenuItem(id)
                mIsPasteEvent = false
                return returnValue
            }
            else -> return super.onTextContextMenuItem(id)
        }
    }

    private fun setClipboardData(clip: ClipData) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboard != null) {
            clipboard.primaryClip = clip
        }
    }

    /**
     * If a [Validator] was set, this method will validate the entire text.
     * (Overrides the superclass method which only validates the current token)
     */
    override fun performValidation() {
        if (mNachoValidator == null || chipTokenizer == null) {
            super.performValidation()
            return
        }

        val text = text
        if (!TextUtils.isEmpty(text) && !mNachoValidator!!.isValid(chipTokenizer!!, text)) {
            setRawText(mNachoValidator!!.fixText(chipTokenizer!!, text))
        }
    }

    /**
     * From the point this method is called to when [.endUnwatchedTextChange] is called, all TextChanged events will be ignored
     */
    private fun beginUnwatchedTextChange() {
        mIgnoreTextChangedEvents = true
    }

    /**
     * After this method is called TextChanged events will resume being handled.
     * This method also calls [.updatePadding] in case the unwatched changed created/destroyed chips
     */
    private fun endUnwatchedTextChange() {
        updatePadding()
        mIgnoreTextChangedEvents = false
    }

    /**
     * Sets the contents of this text view without performing any processing (nothing will be chipified, no characters will be removed etc.)
     *
     * @param text the text to set
     */
    private fun setRawText(text: CharSequence) {
        beginUnwatchedTextChange()
        super.setText(text)
        endUnwatchedTextChange()
    }

    /**
     * Sets the contents of this text view to contain the provided list of strings. The text view will be cleared then each string in the list will
     * be chipified and appended to the text.
     *
     * @param chipValues the list of strings to chipify and set as the contents of the text view or null to clear the text view
     */
    fun setText(chipValues: List<String>?) {
        if (chipTokenizer == null) {
            return
        }
        beginUnwatchedTextChange()

        val text = text
        text.clear()

        if (chipValues != null) {
            for (chipValue in chipValues) {
                val chippedText = chipTokenizer!!.terminateToken(chipValue, null)
                text.append(chippedText)
            }
        }
        setSelection(text.length)

        endUnwatchedTextChange()
    }

    fun setTextWithChips(chips: List<ChipInfo>?) {
        if (chipTokenizer == null) {
            return
        }
        beginUnwatchedTextChange()

        val text = text
        text.clear()

        if (chips != null) {
            for (chipInfo in chips) {
                val chippedText = chipTokenizer!!.terminateToken(chipInfo.text, chipInfo.data)
                text.append(chippedText)
            }
        }
        setSelection(text.length)
        endUnwatchedTextChange()
    }

    override fun onItemClick(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
        if (chipTokenizer == null) {
            return
        }
        val adapter = adapter ?: return
        beginUnwatchedTextChange()

        val data = getDataForSuggestion(adapter, position)
        val text = filter.convertResultToString(adapter.getItem(position))

        clearComposingText()
        var end = selectionEnd
        val editable = getText()
        var start = chipTokenizer!!.findTokenStart(editable, end)

        // guard against java.lang.StringIndexOutOfBoundsException
        start = Math.min(Math.max(0, start), editable.length)
        end = Math.min(Math.max(0, end), editable.length)
        if (end < start) {
            end = start
        }

        editable.replace(start, end, chipTokenizer!!.terminateToken(text, data))

        endUnwatchedTextChange()
    }

    /**
     * Returns a object that will be associated with a chip that is about to be created for the item at `position` in `adapter` because that
     * item was just tapped.
     *
     * @param adapter  the adapter supplying the suggestions
     * @param position the position of the suggestion that was tapped
     * @return the data object
     */
    protected fun getDataForSuggestion(adapter: Adapter, position: Int): Any {
        return adapter.getItem(position)
    }

    /**
     * If there is a ChipTokenizer set, this method will do nothing. Instead we wait until the OnItemClickListener is triggered to actually perform
     * the text replacement so we can also associate the suggestion data with it.
     *
     *
     * If there is no ChipTokenizer set, we call through to the super method.
     *
     * @param text the text to be chipified
     */
    override fun replaceText(text: CharSequence) {
        // If we have a ChipTokenizer, this will be handled by our OnItemClickListener so we can do nothing here.
        // If we don't have a ChipTokenizer, we'll use the default behavior
        if (chipTokenizer == null) {
            super.replaceText(text)
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (mIgnoreTextChangedEvents) {
            return
        }

        mTextChangedStart = start
        mTextChangedEnd = start + after

        // Check for backspace
        if (chipTokenizer != null) {
            if (count > 0 && after < count) {
                val end = start + count
                val message = text
                val chips = chipTokenizer!!.findAllChips(start, end, message)

                for (chip in chips) {
                    val spanStart = chipTokenizer!!.findChipStart(chip, message)
                    val spanEnd = chipTokenizer!!.findChipEnd(chip, message)
                    if (spanStart < end && spanEnd > start) {
                        // Add to remove list
                        mChipsToRemove.add(chip)
                    }
                }
            }
        }
    }

    override fun onTextChanged(textChanged: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(message: Editable) {
        if (mIgnoreTextChangedEvents) {
            return
        }

        // Avoid triggering text changed events from changes we make in this method
        beginUnwatchedTextChange()

        // Handle backspace key
        if (chipTokenizer != null) {
            val iterator = mChipsToRemove.iterator()
            while (iterator.hasNext()) {
                val chip = iterator.next()
                iterator.remove()
                chipTokenizer!!.deleteChip(chip, message)
                if (mOnChipRemoveListener != null) {
                    mOnChipRemoveListener!!.onChipRemove(chip)
                }
            }
        }

        // Handle an illegal or chip terminator character
        if (message.length >= mTextChangedEnd && message.length >= mTextChangedStart) {
            handleTextChanged(mTextChangedStart, mTextChangedEnd)
        }

        endUnwatchedTextChange()
    }

    private fun handleTextChanged(start: Int, end: Int) {
        var end = end
        if (start == end) {
            // If start and end are the same there was text deleted, so this type of event can be ignored
            return
        }

        // First remove any illegal characters
        val text = text
        val subText = text.subSequence(start, end)
        val withoutIllegalCharacters = removeIllegalCharacters(subText)

        // Check if illegal characters were found
        if (withoutIllegalCharacters.length < subText.length) {
            text.replace(start, end, withoutIllegalCharacters)
            end = start + withoutIllegalCharacters.length
            clearComposingText()
        }

        if (start == end) {
            // If start and end are the same here, it means only illegal characters were inserted so there's nothing left to do
            return
        }

        // Then handle chip terminator characters
        if (chipTokenizer != null && mChipTerminatorHandler != null) {
            val newSelectionIndex = mChipTerminatorHandler!!.findAndHandleChipTerminators(chipTokenizer!!, getText(), start, end, mIsPasteEvent)
            if (newSelectionIndex > 0) {
                setSelection(newSelectionIndex)
            }
        }
    }

    private fun removeIllegalCharacters(text: CharSequence): CharSequence {
        val newText = StringBuilder()

        for (i in 0 until text.length) {
            val theChar = text[i]
            if (!isIllegalCharacter(theChar)) {
                newText.append(theChar)
            }
        }

        return newText
    }

    private fun isIllegalCharacter(character: Char): Boolean {
        return if (illegalCharacterIdentifier != null) {
            illegalCharacterIdentifier!!.isCharacterIllegal(character)
        } else false
    }

    /**
     * Chipifies all existing plain text in the field
     */
    fun chipifyAllUnterminatedTokens() {
        beginUnwatchedTextChange()
        chipifyAllUnterminatedTokens(text)
        endUnwatchedTextChange()
    }

    private fun chipifyAllUnterminatedTokens(text: Editable) {
        if (chipTokenizer != null) {
            chipTokenizer!!.terminateAllTokens(text)
        }
    }

    /**
     * Replaces the text from start (inclusive) to end (exclusive) with a chip
     * containing the same text
     *
     * @param start the index of the first character to replace
     * @param end   one more than the index of the last character to replace
     */
    fun chipify(start: Int, end: Int) {
        beginUnwatchedTextChange()
        chipify(start, end, text, null)
        endUnwatchedTextChange()
    }

    private fun chipify(start: Int, end: Int, text: Editable, data: Any?) {
        if (chipTokenizer != null) {
            val textToChip = text.subSequence(start, end)
            val chippedText = chipTokenizer!!.terminateToken(textToChip, data)
            text.replace(start, end, chippedText)
        }
    }

    private fun getTextWithPlainTextSpans(start: Int, end: Int): CharSequence {
        val editable = text
        var selectedText = editable.subSequence(start, end).toString()

        if (chipTokenizer != null) {
            val chips = Arrays.asList(*chipTokenizer!!.findAllChips(start, end, editable))
            Collections.reverse(chips)
            for (chip in chips) {
                val chipText = chip.text.toString()
                val chipStart = chipTokenizer!!.findChipStart(chip, editable) - start
                val chipEnd = chipTokenizer!!.findChipEnd(chip, editable) - start
                selectedText = selectedText.substring(0, chipStart) + chipText + selectedText.substring(chipEnd, selectedText.length)
            }
        }
        return selectedText
    }

    override fun toString(): String {
        try {
            return getTextWithPlainTextSpans(0, text.length).toString()
        } catch (ex: ClassCastException) {  // Exception is thrown by cast in getText() on some LG devices
            return super.toString()
        } catch (e: StringIndexOutOfBoundsException) {
            throw StringIndexOutOfBoundsException(String.format("%s \nError converting toString() [%s]", e.message, text.toString()))
        }

    }

    private inner class ChipTokenizerWrapper(private val mChipTokenizer: ChipTokenizer) : Tokenizer {

        override fun findTokenStart(text: CharSequence, cursor: Int): Int {
            return mChipTokenizer.findTokenStart(text, cursor)
        }

        override fun findTokenEnd(text: CharSequence, cursor: Int): Int {
            return mChipTokenizer.findTokenEnd(text, cursor)
        }

        override fun terminateToken(text: CharSequence): CharSequence {
            return mChipTokenizer.terminateToken(text, null)
        }
    }

    interface OnChipClickListener {

        /**
         * Called when a chip in this TextView is touched. This callback is triggered by the [MotionEvent.ACTION_UP] event.
         *
         * @param chip  the [Chip] that was touched
         * @param event the [MotionEvent] that caused the touch
         */
        fun onChipClick(chip: Chip, event: MotionEvent)
    }

    interface OnChipRemoveListener {

        /**
         * Called when a chip in this TextView is removed
         *
         * @param chip  the [Chip] that was removed
         */
        fun onChipRemove(chip: Chip)
    }

    private inner class SingleTapListener : GestureDetector.SimpleOnGestureListener() {

        /**
         * @param e the [MotionEvent] passed to the GestureDetector
         * @return true if singleTapUp (click) was detected
         */
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
        }
    }
}
