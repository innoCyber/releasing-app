package ptml.releasing.app.views

import android.content.Context
import android.util.AttributeSet
import android.view.View

import androidx.appcompat.widget.AppCompatImageView

class FiveFourImageView : AppCompatImageView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sixteenNineHeight = View.MeasureSpec.getSize(widthMeasureSpec) * 5 / 4
        val sixteenNineHeightSpec =
            View.MeasureSpec.makeMeasureSpec(sixteenNineHeight, View.MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, sixteenNineHeightSpec)
    }
}
