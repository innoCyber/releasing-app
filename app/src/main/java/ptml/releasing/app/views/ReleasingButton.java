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
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.TextViewCompat;
import ptml.releasing.R;

public class ReleasingButton extends FrameLayout {
    public static final String DEFAULT_BG_COLOR = "#E53935";
    public static final String DEFAULT_TEXT_COLOR = "#FFFFFF";
    public static final int DEFAULT_PADDING = 10;
    public static final int DEFAULT_RADIUS = 8;
    public static final int DEFAULT_TEXT_SIZE = 18;

    private String text;
    private Drawable icon;
    private float drawablePadding;
    private int backgroundColor;
    private float cornerRadius;
    private int textColor;
    private float textSize;

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

    private void initAttr(Context context, AttributeSet attributeSet){
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
        cornerRadius = typedArray.getDimension(R.styleable.ReleasingButton_rb_cornerRadius, DEFAULT_RADIUS);

        //text color
        textColor = typedArray.getColor(R.styleable.ReleasingButton_tb_textColor, Color.parseColor(DEFAULT_TEXT_COLOR));

        //text size
        textSize = typedArray.getDimension(R.styleable.ReleasingButton_tb_textSize, DEFAULT_TEXT_SIZE);
        typedArray.recycle();

        initView();

    }

    private void initView(){
        //create a TextView
        TextView textView = new TextView(getContext());
        //set the specified text
        textView.setText(text);
        textView.setTextSize(textSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(android.R.style.TextAppearance_Medium);
        }else{
            textView.setTextAppearance(getContext(), android.R.style.TextAppearance_Medium);
        }
        textView.setTextColor(textColor);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));

        //set the icon
        textView.setCompoundDrawablePadding((int) drawablePadding);
        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(textView, icon, null, null ,null);

        //create drawable for the background
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(backgroundColor); //set the color
        gradientDrawable.setCornerRadius(cornerRadius); //set the corner radius

        //set the drawable as the background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(gradientDrawable);
        }else{
            setBackgroundDrawable(gradientDrawable);
        }

        //add the text
        addView(textView);

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
