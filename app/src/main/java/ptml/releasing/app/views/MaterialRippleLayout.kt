package ptml.releasing.app.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Property
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewParent
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.AdapterView
import android.widget.FrameLayout


import ptml.releasing.R

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.ViewGroup.LayoutParams.MATCH_PARENT

@Suppress("SENSELESS_COMPARISON")
class MaterialRippleLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bounds = Rect()

    private var rippleColor: Int = 0
    private var rippleOverlay: Boolean = false
    private var rippleHover: Boolean = false
    private var rippleDiameter: Int = 0
    private var rippleDuration: Int = 0
    private var rippleAlpha: Int = 0
    private var rippleDelayClick: Boolean = false
    private var rippleFadeDuration: Int = 0
    private var ripplePersistent: Boolean = false
    private var rippleBackground: Drawable? = null
    private var rippleInAdapter: Boolean = false
    private var rippleRoundedCorners: Float = 0.toFloat()

    private var radius: Float = 0.toFloat()
        set(radius) {
            field = radius
            invalidate()
        }

    private var parentAdapter: AdapterView<*>? = null
    private var childView: View? = null

    private var rippleAnimator: AnimatorSet? = null
    private var hoverAnimator: ObjectAnimator? = null

    private var currentCoords = Point()
    private var previousCoords = Point()

    private var mLayerType: Int = 0

    private var eventCancelled: Boolean = false
    private var prepressed: Boolean = false
    private var positionInAdapter: Int = 0

    private val gestureDetector: GestureDetector
    private var pendingClickEvent: PerformClickEvent? = null
    private var pendingPressEvent: PressedEvent? = null

    private var hasPerformedLongPress: Boolean = false
    private val longClickListener = object : SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
            hasPerformedLongPress = childView!!.performLongClick()
            if (hasPerformedLongPress) {
                if (rippleHover) {
                    startRipple(null)
                }
                cancelPressedEvent()
            }
        }

        override fun onDown(e: MotionEvent): Boolean {
            hasPerformedLongPress = false
            return super.onDown(e)
        }
    }

    private val endRadius: Float
        get() {
            val width = width
            val height = height

            val halfWidth = width / 2
            val halfHeight = height / 2

            val radiusX = (if (halfWidth > currentCoords.x) width - currentCoords.x else currentCoords.x).toFloat()
            val radiusY = (if (halfHeight > currentCoords.y) height - currentCoords.y else currentCoords.y).toFloat()

            return Math.sqrt(Math.pow(radiusX.toDouble(), 2.0) + Math.pow(radiusY.toDouble(), 2.0)).toFloat() * 1.2f
        }

    private val isInScrollingContainer: Boolean
        get() {
            var p: ViewParent? = parent
            while (p != null && p is ViewGroup) {
                if (p.shouldDelayChildPressedState()) {
                    return true
                }
                p = p.parent
            }
            return false
        }

    /*
     * Animations
     */
    private val radiusProperty = object : Property<MaterialRippleLayout, Float>(Float::class.java, "radius") {
        override fun get(`object`: MaterialRippleLayout): Float {
            return `object`.radius
        }

        override fun set(`object`: MaterialRippleLayout, value: Float) {
            `object`.radius = value
        }
    }

    private val circleAlphaProperty = object : Property<MaterialRippleLayout, Int>(Int::class.java, "rippleAlpha") {
        override fun get(`object`: MaterialRippleLayout): Int {
            return `object`.getRippleAlpha()
        }

        override fun set(`object`: MaterialRippleLayout, value: Int?) {
            `object`.setRippleAlpha(value)
        }
    }

    init {

        setWillNotDraw(false)
        gestureDetector = GestureDetector(context, longClickListener)

        val a = context.obtainStyledAttributes(attrs, R.styleable.MaterialRippleLayout)
        rippleColor = a.getColor(R.styleable.MaterialRippleLayout_mrl_rippleColor, DEFAULT_COLOR)
        rippleDiameter = a.getDimensionPixelSize(
                R.styleable.MaterialRippleLayout_mrl_rippleDimension,
                dpToPx(resources, DEFAULT_DIAMETER_DP).toInt()
        )
        rippleOverlay = a.getBoolean(R.styleable.MaterialRippleLayout_mrl_rippleOverlay, DEFAULT_RIPPLE_OVERLAY)
        rippleHover = a.getBoolean(R.styleable.MaterialRippleLayout_mrl_rippleHover, DEFAULT_HOVER)
        rippleDuration = a.getInt(R.styleable.MaterialRippleLayout_mrl_rippleDuration, DEFAULT_DURATION)
        rippleAlpha = (255 * a.getFloat(R.styleable.MaterialRippleLayout_mrl_rippleAlpha, DEFAULT_ALPHA)).toInt()
        rippleDelayClick = a.getBoolean(R.styleable.MaterialRippleLayout_mrl_rippleDelayClick, DEFAULT_DELAY_CLICK)
        rippleFadeDuration = a.getInteger(R.styleable.MaterialRippleLayout_mrl_rippleFadeDuration, DEFAULT_FADE_DURATION)
        rippleBackground = ColorDrawable(a.getColor(R.styleable.MaterialRippleLayout_mrl_rippleBackground, DEFAULT_BACKGROUND))
        ripplePersistent = a.getBoolean(R.styleable.MaterialRippleLayout_mrl_ripplePersistent, DEFAULT_PERSISTENT)
        rippleInAdapter = a.getBoolean(R.styleable.MaterialRippleLayout_mrl_rippleInAdapter, DEFAULT_SEARCH_ADAPTER)
        rippleRoundedCorners = a.getDimensionPixelSize(R.styleable.MaterialRippleLayout_mrl_rippleRoundedCorners, DEFAULT_ROUNDED_CORNERS).toFloat()

        a.recycle()

        paint.color = rippleColor
        paint.alpha = rippleAlpha

        enableClipPathSupportIfNecessary()
    }


    @Suppress("UNCHECKED_CAST")
    fun <T : View> getChildView(): T {
        return childView as T
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (childCount > 0) {
            throw IllegalStateException("MaterialRippleLayout can host only one child")
        }

        childView = child
        super.addView(child, index, params)
    }

    override fun setOnClickListener(onClickListener: View.OnClickListener?) {
        if (childView == null) {
            throw IllegalStateException("MaterialRippleLayout must have a child view to handle clicks")
        }
        childView!!.setOnClickListener(onClickListener)
    }

    override fun setOnLongClickListener(onClickListener: View.OnLongClickListener?) {
        if (childView == null) {
            throw IllegalStateException("MaterialRippleLayout must have a child view to handle clicks")
        }
        childView!!.setOnLongClickListener(onClickListener)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return !findClickableViewInChild(childView, event.x.toInt(), event.y.toInt())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val superOnTouchEvent = super.onTouchEvent(event)

        if (!isEnabled || !childView!!.isEnabled) return superOnTouchEvent

        val isEventInBounds = bounds.contains(event.x.toInt(), event.y.toInt())

        if (isEventInBounds) {
            previousCoords.set(currentCoords.x, currentCoords.y)
            currentCoords.set(event.x.toInt(), event.y.toInt())
        }

        val gestureResult = gestureDetector.onTouchEvent(event)
        if (gestureResult || hasPerformedLongPress) {
            return true
        } else {
            val action = event.actionMasked
            when (action) {
                MotionEvent.ACTION_UP -> {
                    pendingClickEvent = PerformClickEvent()

                    if (prepressed) {
                        childView!!.isPressed = true
                        postDelayed(
                                { childView!!.isPressed = false }, ViewConfiguration.getPressedStateDuration().toLong())
                    }

                    if (isEventInBounds) {
                        startRipple(pendingClickEvent)
                    } else if (!rippleHover) {
                        radius = 0f
                    }
                    if (!rippleDelayClick && isEventInBounds) {
                        pendingClickEvent!!.run()
                    }
                    cancelPressedEvent()
                }
                MotionEvent.ACTION_DOWN -> {
                    setPositionInAdapter()
                    eventCancelled = false
                    pendingPressEvent = PressedEvent(event)
                    if (isInScrollingContainer) {
                        cancelPressedEvent()
                        prepressed = true
                        postDelayed(pendingPressEvent, ViewConfiguration.getTapTimeout().toLong())
                    } else {
                        pendingPressEvent!!.run()
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (rippleInAdapter) {
                        // dont use current coords in adapter since they tend to jump drastically on scroll
                        currentCoords.set(previousCoords.x, previousCoords.y)
                        previousCoords = Point()
                    }
                    childView!!.onTouchEvent(event)
                    if (rippleHover) {
                        if (!prepressed) {
                            startRipple(null)
                        }
                    } else {
                        childView!!.isPressed = false
                    }
                    cancelPressedEvent()
                }
                MotionEvent.ACTION_MOVE -> {
                    if (rippleHover) {
                        if (isEventInBounds && !eventCancelled) {
                            invalidate()
                        } else if (!isEventInBounds) {
                            startRipple(null)
                        }
                    }

                    if (!isEventInBounds) {
                        cancelPressedEvent()
                        if (hoverAnimator != null) {
                            hoverAnimator!!.cancel()
                        }
                        childView!!.onTouchEvent(event)
                        eventCancelled = true
                    }
                }
            }
            return true
        }
    }

    private fun cancelPressedEvent() {
        if (pendingPressEvent != null) {
            removeCallbacks(pendingPressEvent)
            prepressed = false
        }
    }

    private fun startHover() {
        if (eventCancelled) return

        if (hoverAnimator != null) {
            hoverAnimator!!.cancel()
        }
        val radius = (Math.sqrt(Math.pow(width.toDouble(), 2.0) + Math.pow(height.toDouble(), 2.0)) * 1.2f).toFloat()
        hoverAnimator = ObjectAnimator.ofFloat<MaterialRippleLayout>(this, radiusProperty,  radius)
                .setDuration(HOVER_DURATION)
        hoverAnimator!!.interpolator = LinearInterpolator()
        hoverAnimator!!.start()
    }

    private fun startRipple(animationEndRunnable: Runnable?) {
        if (eventCancelled) return

        val endRadius = endRadius

        cancelAnimations()

        rippleAnimator = AnimatorSet()
        rippleAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!ripplePersistent) {
                    radius = 0f
                    setRippleAlpha(rippleAlpha)
                }
                if (animationEndRunnable != null && rippleDelayClick) {
                    animationEndRunnable.run()
                }
                childView!!.isPressed = false
            }
        })

        val ripple = ObjectAnimator.ofFloat(this, radiusProperty, this.radius, endRadius)
        ripple.duration = rippleDuration.toLong()
        ripple.interpolator = DecelerateInterpolator()
        val fade = ObjectAnimator.ofInt(this, circleAlphaProperty, rippleAlpha, 0)
        fade.duration = rippleFadeDuration.toLong()
        fade.interpolator = AccelerateInterpolator()
        fade.startDelay = (rippleDuration - rippleFadeDuration - FADE_EXTRA_DELAY).toLong()

        if (ripplePersistent) {
            rippleAnimator!!.play(ripple)
        } else if (radius > endRadius) {
            fade.startDelay = 0
            rippleAnimator!!.play(fade)
        } else {
            rippleAnimator!!.playTogether(ripple, fade)
        }
        rippleAnimator!!.start()
    }

    private fun cancelAnimations() {
        if (rippleAnimator != null) {
            rippleAnimator!!.cancel()
            rippleAnimator!!.removeAllListeners()
        }

        if (hoverAnimator != null) {
            hoverAnimator!!.cancel()
        }
    }

    private fun findParentAdapterView(): AdapterView<*> {
        if (parentAdapter != null) {
            return parentAdapter as AdapterView<*>
        }
        var current = parent
        while (true) {
            if (current is AdapterView<*>) {
                parentAdapter = current
                return parentAdapter as AdapterView<*>
            } else {
                try {
                    current = current.parent
                } catch (npe: NullPointerException) {
                    throw RuntimeException("Could not find a parent AdapterView")
                }

            }
        }
    }

    private fun setPositionInAdapter() {
        if (rippleInAdapter) {
            positionInAdapter = findParentAdapterView().getPositionForView(this@MaterialRippleLayout)
        }
    }

    private fun adapterPositionChanged(): Boolean {
        if (rippleInAdapter) {
            val newPosition = findParentAdapterView().getPositionForView(this@MaterialRippleLayout)
            val changed = newPosition != positionInAdapter
            positionInAdapter = newPosition
            if (changed) {
                cancelPressedEvent()
                cancelAnimations()
                childView!!.isPressed = false
                radius = 0f
            }
            return changed
        }
        return false
    }

    private fun findClickableViewInChild(view: View?, x: Int, y: Int): Boolean {
        if (view is ViewGroup) {
            val viewGroup = view as ViewGroup?
            for (i in 0 until viewGroup!!.childCount) {
                val child = viewGroup.getChildAt(i)
                val rect = Rect()
                child.getHitRect(rect)

                val contains = rect.contains(x, y)
                if (contains) {
                    return findClickableViewInChild(child, x - rect.left, y - rect.top)
                }
            }
        } else if (view !== childView) {
            return view!!.isEnabled && (view.isClickable || view.isLongClickable || view.isFocusableInTouchMode)
        }

        return view!!.isFocusableInTouchMode
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bounds.set(0, 0, w, h)
        rippleBackground!!.bounds = bounds
    }

    override fun isInEditMode(): Boolean {
        return true
    }

    /*
     * Drawing
     */
    override fun draw(canvas: Canvas) {
        val positionChanged = adapterPositionChanged()
        if (rippleOverlay) {
            if (!positionChanged) {
                rippleBackground!!.draw(canvas)
            }
            super.draw(canvas)
            if (!positionChanged) {
                if (rippleRoundedCorners != 0f) {
                    val clipPath = Path()
                    val rect = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
                    clipPath.addRoundRect(rect, rippleRoundedCorners, rippleRoundedCorners, Path.Direction.CW)
                    canvas.clipPath(clipPath)
                }
                canvas.drawCircle(currentCoords.x.toFloat(), currentCoords.y.toFloat(), this.radius, paint)
            }
        } else {
            if (!positionChanged) {
                rippleBackground!!.draw(canvas)
                canvas.drawCircle(currentCoords.x.toFloat(), currentCoords.y.toFloat(), this.radius, paint)
            }
            super.draw(canvas)
        }
    }

    fun getRippleAlpha(): Int {
        return paint.alpha
    }

    fun setRippleAlpha(rippleAlpha: Int?) {
        paint.alpha = rippleAlpha!!
        invalidate()
    }

    /*
    * Accessor
     */
    fun setRippleColor(rippleColor: Int) {
        this.rippleColor = rippleColor
        paint.color = rippleColor
        paint.alpha = rippleAlpha
        invalidate()
    }

    fun setRippleOverlay(rippleOverlay: Boolean) {
        this.rippleOverlay = rippleOverlay
    }

    fun setRippleDiameter(rippleDiameter: Int) {
        this.rippleDiameter = rippleDiameter
    }

    fun setRippleDuration(rippleDuration: Int) {
        this.rippleDuration = rippleDuration
    }

    fun setRippleBackground(color: Int) {
        rippleBackground = ColorDrawable(color)
        rippleBackground!!.bounds = bounds
        invalidate()
    }

    fun setRippleHover(rippleHover: Boolean) {
        this.rippleHover = rippleHover
    }

    fun setRippleDelayClick(rippleDelayClick: Boolean) {
        this.rippleDelayClick = rippleDelayClick
    }

    fun setRippleFadeDuration(rippleFadeDuration: Int) {
        this.rippleFadeDuration = rippleFadeDuration
    }

    fun setRipplePersistent(ripplePersistent: Boolean) {
        this.ripplePersistent = ripplePersistent
    }

    fun setRippleInAdapter(rippleInAdapter: Boolean) {
        this.rippleInAdapter = rippleInAdapter
    }

    fun setRippleRoundedCorners(rippleRoundedCorner: Int) {
        this.rippleRoundedCorners = rippleRoundedCorner.toFloat()
        enableClipPathSupportIfNecessary()
    }

    fun setDefaultRippleAlpha(alpha: Float) {
        this.rippleAlpha = (255 * alpha).toInt()
        paint.alpha = rippleAlpha
        invalidate()
    }

    fun performRipple() {
        currentCoords = Point(width / 2, height / 2)
        startRipple(null)
    }

    fun performRipple(anchor: Point) {
        currentCoords = Point(anchor.x, anchor.y)
        startRipple(null)
    }

    /**
     * [Canvas.clipPath] is not supported in hardware accelerated layers
     * before API 18. Use software layer instead
     *
     *
     * https://developer.android.com/guide/topics/graphics/hardware-accel.html#unsupported
     */
    private fun enableClipPathSupportIfNecessary() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (rippleRoundedCorners != 0f) {
                mLayerType = getLayerType()
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            } else {
                setLayerType(mLayerType, null)
            }
        }
    }

    /*
     * Helper
     */
    private inner class PerformClickEvent : Runnable {

        override fun run() {
            if (hasPerformedLongPress) return

            // if parent is an AdapterView, try to call its ItemClickListener
            if (parent is AdapterView<*>) {
                // try clicking direct child first
                if (!childView!!.performClick())
                // if it did not handle it dispatch to adapterView
                    clickAdapterView(parent as AdapterView<*>)
            } else if (rippleInAdapter) {
                // find adapter view
                clickAdapterView(findParentAdapterView())
            } else {
                // otherwise, just perform click on child
                childView!!.performClick()
            }
        }

        private fun clickAdapterView(parent: AdapterView<*>) {
            val position = parent.getPositionForView(this@MaterialRippleLayout)
            val itemId = if (parent.adapter != null)
                parent.adapter.getItemId(position)
            else
                0
            if (position != AdapterView.INVALID_POSITION) {
                parent.performItemClick(this@MaterialRippleLayout, position, itemId)
            }
        }
    }

    private inner class PressedEvent(private val event: MotionEvent) : Runnable {

        override fun run() {
            prepressed = false
            childView!!.isLongClickable = false//prevent the child's long click,let's the ripple layout call it's performLongClick
            childView!!.onTouchEvent(event)
            childView!!.isPressed = true
            if (rippleHover) {
                startHover()
            }
        }
    }

    /*
     * Builder
     */

    class RippleBuilder(private val child: View) {

        private val context: Context

        private var rippleColor = DEFAULT_COLOR
        private var rippleOverlay = DEFAULT_RIPPLE_OVERLAY
        private var rippleHover = DEFAULT_HOVER
        private var rippleDiameter = DEFAULT_DIAMETER_DP
        private var rippleDuration = DEFAULT_DURATION
        private var rippleAlpha = DEFAULT_ALPHA
        private var rippleDelayClick = DEFAULT_DELAY_CLICK
        private var rippleFadeDuration = DEFAULT_FADE_DURATION
        private var ripplePersistent = DEFAULT_PERSISTENT
        private var rippleBackground = DEFAULT_BACKGROUND
        private var rippleSearchAdapter = DEFAULT_SEARCH_ADAPTER
        private var rippleRoundedCorner = DEFAULT_ROUNDED_CORNERS.toFloat()

        init {
            this.context = child.context
        }

        fun rippleColor(color: Int): RippleBuilder {
            this.rippleColor = color
            return this
        }

        fun rippleOverlay(overlay: Boolean): RippleBuilder {
            this.rippleOverlay = overlay
            return this
        }

        fun rippleHover(hover: Boolean): RippleBuilder {
            this.rippleHover = hover
            return this
        }

        fun rippleDiameterDp(diameterDp: Int): RippleBuilder {
            this.rippleDiameter = diameterDp.toFloat()
            return this
        }

        fun rippleDuration(duration: Int): RippleBuilder {
            this.rippleDuration = duration
            return this
        }

        fun rippleAlpha(alpha: Float): RippleBuilder {
            this.rippleAlpha = alpha
            return this
        }

        fun rippleDelayClick(delayClick: Boolean): RippleBuilder {
            this.rippleDelayClick = delayClick
            return this
        }

        fun rippleFadeDuration(fadeDuration: Int): RippleBuilder {
            this.rippleFadeDuration = fadeDuration
            return this
        }

        fun ripplePersistent(persistent: Boolean): RippleBuilder {
            this.ripplePersistent = persistent
            return this
        }

        fun rippleBackground(color: Int): RippleBuilder {
            this.rippleBackground = color
            return this
        }

        fun rippleInAdapter(inAdapter: Boolean): RippleBuilder {
            this.rippleSearchAdapter = inAdapter
            return this
        }

        fun rippleRoundedCorners(radiusDp: Int): RippleBuilder {
            this.rippleRoundedCorner = radiusDp.toFloat()
            return this
        }

        fun create(): MaterialRippleLayout {
            val layout = MaterialRippleLayout(context)
            layout.setRippleColor(rippleColor)
            layout.setDefaultRippleAlpha(rippleAlpha)
            layout.setRippleDelayClick(rippleDelayClick)
            layout.setRippleDiameter(dpToPx(context.resources, rippleDiameter).toInt())
            layout.setRippleDuration(rippleDuration)
            layout.setRippleFadeDuration(rippleFadeDuration)
            layout.setRippleHover(rippleHover)
            layout.setRipplePersistent(ripplePersistent)
            layout.setRippleOverlay(rippleOverlay)
            layout.setRippleBackground(rippleBackground)
            layout.setRippleInAdapter(rippleSearchAdapter)
            layout.setRippleRoundedCorners(dpToPx(context.resources, rippleRoundedCorner).toInt())

            val params = child.layoutParams
            val parent = child.parent as ViewGroup
            var index = 0

            if (parent != null && parent is MaterialRippleLayout) {
                throw IllegalStateException("MaterialRippleLayout could not be created: parent of the view already is a MaterialRippleLayout")
            }

            if (parent != null) {
                index = parent.indexOfChild(child)
                parent.removeView(child)
            }

            layout.addView(child, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))

            parent.addView(layout, index, params)

            return layout
        }
    }

    companion object {

        private val DEFAULT_DURATION = 350
        private val DEFAULT_FADE_DURATION = 75
        private val DEFAULT_DIAMETER_DP = 35f
        private val DEFAULT_ALPHA = 0.2f
        private val DEFAULT_COLOR = Color.BLACK
        private val DEFAULT_BACKGROUND = Color.TRANSPARENT
        private val DEFAULT_HOVER = true
        private val DEFAULT_DELAY_CLICK = true
        private val DEFAULT_PERSISTENT = false
        private val DEFAULT_SEARCH_ADAPTER = false
        private val DEFAULT_RIPPLE_OVERLAY = false
        private val DEFAULT_ROUNDED_CORNERS = 0

        private val FADE_EXTRA_DELAY = 50
        private val HOVER_DURATION: Long = 2500

        fun on(view: View): RippleBuilder {
            return RippleBuilder(view)
        }

        internal fun dpToPx(resources: Resources, dp: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
        }
    }
}
