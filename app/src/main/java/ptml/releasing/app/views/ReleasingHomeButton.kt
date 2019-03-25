package ptml.releasing.app.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.widget.TextViewCompat
import ptml.releasing.R
import ptml.releasing.app.utils.SizeUtils
import ptml.releasing.app.utils.updatePadding
import timber.log.Timber

class ReleasingHomeButton : ReleasingButton {

    private var bgDrawable: Drawable? = null
    private var iconPaddingStart: Int = 64
    private var iconPaddingEnd: Int = 64
    private var iconPaddingTop: Int = 32
    private var iconPaddingBottom: Int = 32
    private var iconWidth: Int = 64
    private var iconHeight: Int = 64
    private var textMarginTop: Int = 8


    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun initAttr(context: Context, attributeSet: AttributeSet?) {

        val typedArray = context.obtainStyledAttributes(
            attributeSet,
            R.styleable.ReleasingHomeButton
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bgDrawable = typedArray.getDrawable(R.styleable.ReleasingHomeButton_rb_bg_drawable)
        } else {
            val drawableRes = typedArray.getResourceId(R.styleable.ReleasingHomeButton_rb_bg_drawable, -1)
            if (drawableRes != -1) {
                bgDrawable = AppCompatResources.getDrawable(context, drawableRes)
            }
        }

        //drawablePadding
        iconPaddingStart = typedArray.getDimensionPixelSize(
            R.styleable.ReleasingHomeButton_rb_iconPaddingStart,
            resources.getDimensionPixelSize(R.dimen.home_btn_icon_padding_start)
        )
        iconPaddingEnd = typedArray.getDimensionPixelSize(
            R.styleable.ReleasingHomeButton_rb_iconPaddingEnd,
            resources.getDimensionPixelSize(R.dimen.home_btn_icon_padding_end)
        )
        iconPaddingTop = typedArray.getDimensionPixelSize(
            R.styleable.ReleasingHomeButton_rb_iconPaddingTop,
            resources.getDimensionPixelSize(R.dimen.home_btn_icon_padding_top)
        )
        iconPaddingBottom = typedArray.getDimensionPixelSize(
            R.styleable.ReleasingHomeButton_rb_iconPaddingBottom,
            resources.getDimensionPixelSize(R.dimen.home_btn_icon_padding_bottom)
        )

        iconWidth = typedArray.getDimensionPixelSize(
            R.styleable.ReleasingHomeButton_rb_iconWidth,
            resources.getDimensionPixelSize(R.dimen.home_btn_icon_size)
        )
        iconHeight = typedArray.getDimensionPixelSize(
            R.styleable.ReleasingHomeButton_rb_iconHeight,
            resources.getDimensionPixelSize(R.dimen.home_btn_icon_size)
        )

        textMarginTop = typedArray.getDimensionPixelSize(
            R.styleable.ReleasingHomeButton_rb_textMarginTop,
            resources.getDimensionPixelSize(R.dimen.home_btn_tv_margin_top)
        )

        //ensure to call super to initialize
        super.initAttr(context, attributeSet)
        typedArray?.recycle()
    }

    override fun initView() {

        //create an ImageView
        val imageView = ImageView(context)
        imageView.id = R.id.releasing_btn_home_img
        imageView.layoutParams = LayoutParams(
            iconWidth.toInt(),
            iconHeight.toInt()
        )
        //add the ImageView
        addView(imageView)

        //set the icon
        imageView.setImageDrawable(icon)


        //set the drawable as the background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.background = getBg()
        } else {
            imageView.setBackgroundDrawable(getBg())
        }


        val textView = TextView(context)
        textView.id = R.id.releasing_btn_home_tv
        //add the text
        addView(textView)



        //set the specified text
        textView.text = text

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(android.R.style.TextAppearance_Medium)
        } else {
            textView.setTextAppearance(context, android.R.style.TextAppearance_Medium)
        }
        textView.textSize = SizeUtils.px2sp(context, textSize).toFloat()
        textView.gravity = Gravity.CENTER
        textView.setTextColor(textColor)


        //set the layout params
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)

        constraintSet.create(R.id.releasing_btn_guideline, ConstraintSet.HORIZONTAL_GUIDELINE)
        constraintSet.setGuidelinePercent(R.id.releasing_btn_guideline, 0.8f)

        constraintSet.connect(imageView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(imageView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.connect(imageView.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(imageView.id, ConstraintSet.BOTTOM, R.id.releasing_btn_guideline, ConstraintSet.TOP)

        constraintSet.constrainWidth(imageView.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.constrainHeight(imageView.id, ConstraintSet.MATCH_CONSTRAINT)

        Timber.d("Text margin top: %s", textMarginTop)
        Timber.d("End pA top: %s", iconPaddingEnd)
        Timber.d("Padding Start top: %s", iconPaddingStart)
        Timber.d("Padding T top: %s", iconPaddingTop)
        Timber.d("Padding B top: %s", iconPaddingBottom)
        Timber.d("W %s", iconWidth)
        Timber.d("H; %s", iconHeight)
        constraintSet.connect(textView.id, ConstraintSet.TOP, imageView.id, ConstraintSet.BOTTOM, textMarginTop)
        constraintSet.connect(textView.id, ConstraintSet.START, imageView.id, ConstraintSet.START)
        constraintSet.connect(textView.id, ConstraintSet.END, imageView.id, ConstraintSet.END)
        constraintSet.constrainWidth(textView.id, ConstraintSet.MATCH_CONSTRAINT)
        constraintSet.constrainHeight(textView.id, ConstraintSet.WRAP_CONTENT)
        constraintSet.applyTo(this)


        //add padding
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            imageView.updatePadding(
                iconPaddingStart,
                iconPaddingTop,
                iconPaddingEnd,
                iconPaddingBottom
            )
        } else {
            imageView.setPadding(
                iconPaddingStart,
                iconPaddingTop,
                iconPaddingEnd,
                iconPaddingBottom
            )
        }


        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(textView, 12, 48, 1, TypedValue.COMPLEX_UNIT_SP)

    }

    private fun getBg(): Drawable? {
        return when (bgDrawable == null) {
            true -> gradientDrawable
            else -> bgDrawable
        }
    }
}
