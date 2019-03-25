package ptml.releasing.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.widget.TextViewCompat;
import org.jetbrains.annotations.NotNull;
import ptml.releasing.R;
import ptml.releasing.app.utils.SizeUtils;

public class ReleasingButton extends ConstraintLayout {
    public static final String DEFAULT_BG_COLOR = "#E53935";
    public static final String DEFAULT_TEXT_COLOR = "#FFFFFF";
    public static final int DEFAULT_PADDING = 10;

    protected String text;
    protected Drawable icon;
    protected float drawablePadding;
    protected int backgroundColor;
    protected float cornerRadius;
    protected int textColor;
    protected float textSize;

    public ReleasingButton(@NonNull Context context) {
        super(context);
        initAttr(context, null);
    }

    public ReleasingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }

    public ReleasingButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs );
    }

    protected void initAttr(Context context, AttributeSet attributeSet){
        if(attributeSet == null){
            return;
        }


        //icon
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet,
                R.styleable.ReleasingButton);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            icon = typedArray.getDrawable(R.styleable.ReleasingButton_rb_icon);
        }else{
            int drawableRes = typedArray.getResourceId(R.styleable.ReleasingButton_rb_icon, -1);
            if(drawableRes != -1){
                icon = AppCompatResources.getDrawable(context, drawableRes);
            }
        }

        //text
        TypedValue typedValue = new TypedValue();
        typedArray.getValue(R.styleable.ReleasingButton_rb_text, typedValue);

        if(typedValue.type == TypedValue.TYPE_STRING){
            text =typedValue.string.toString();
        }else if(typedValue.type == TypedValue.TYPE_REFERENCE){
            text = getResources().getString(typedValue.resourceId);
        }

        //drawablePadding
        drawablePadding = typedArray.getDimension(R.styleable.ReleasingButton_rb_drawablePadding, DEFAULT_PADDING);

        //background color
        backgroundColor = typedArray.getColor(R.styleable.ReleasingButton_rb_backgroundColor, Color.parseColor(DEFAULT_BG_COLOR));

        //corner radius
        cornerRadius = typedArray.getDimension(R.styleable.ReleasingButton_rb_cornerRadius, getResources().getDimension(R.dimen.releasing_button_default_corner_radius));

        //text color
        textColor = typedArray.getColor(R.styleable.ReleasingButton_rb_textColor, Color.parseColor(DEFAULT_TEXT_COLOR));

        //text size
        textSize = typedArray.getDimensionPixelSize(R.styleable.ReleasingButton_rb_textSize, getResources().getDimensionPixelSize(R.dimen.home_btn_text_size));
        typedArray.recycle();

        initView();

    }

    protected void initView(){
        //create a TextView
        TextView textView = new TextView(getContext());

        //add the text
        addView(textView);

        //set the specified text
        textView.setText(text);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(android.R.style.TextAppearance_Medium);
        }else{
            textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
        }

        textView.setTextSize(SizeUtils.px2sp(getContext(), textSize));
        textView.setTextColor(textColor);
        textView.setGravity(Gravity.CENTER);
        textView.setId(R.id.releasing_btn_tv);



        //set the icon
        textView.setCompoundDrawablePadding((int) drawablePadding);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, icon, null, null ,null);

        GradientDrawable gradientDrawable = getGradientDrawable();

        //set the drawable as the background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(gradientDrawable);
        }else{
            setBackgroundDrawable(gradientDrawable);
        }

        //set the layout params
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this );
        constraintSet.constrainWidth(textView.getId(), ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainHeight(textView.getId(), ConstraintSet.WRAP_CONTENT);
        constraintSet.connect(textView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(textView.getId(), ConstraintSet.TOP,  ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.applyTo(this);


    }

    @NotNull
    protected GradientDrawable getGradientDrawable() {
        //create drawable for the background
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(backgroundColor); //set the color
        gradientDrawable.setCornerRadius(cornerRadius); //set the corner radius
        return gradientDrawable;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        initView();
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
        initView();
    }

    public int getDrawablePadding() {
        return (int) drawablePadding;
    }

    public void setDrawablePadding(int drawablePadding) {
        this.drawablePadding = drawablePadding;
        initView();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        initView();
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        initView();
    }

    public void setDrawablePadding(float drawablePadding) {
        this.drawablePadding = drawablePadding;
        initView();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        initView();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        initView();
    }
}
