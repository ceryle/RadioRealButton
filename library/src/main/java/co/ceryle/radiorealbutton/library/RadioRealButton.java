/*
 * Copyright (C) 2016 Ege Aker <egeaker@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.ceryle.radiorealbutton.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.ceryle.radiorealbutton.R;
import co.ceryle.radiorealbutton.library.util.ConversionUtil;
import co.ceryle.radiorealbutton.library.util.RippleHelper;

/**
 * Created by EGE on 09/08/2016.
 */
public class RadioRealButton extends LinearLayout {
    public RadioRealButton(Context context) {
        super(context);
        this.init(null);
    }

    public RadioRealButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public RadioRealButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RadioRealButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(attrs);
    }

    private ImageView imageView;
    private TextView textView;
    private LinearLayout container;

    private void init(AttributeSet attrs) {
        getAttributes(attrs);
        View view = inflate(getContext(), co.ceryle.radiorealbutton.R.layout.ceryle_radiorealbutton, this);

        container = (LinearLayout) view.findViewById(co.ceryle.radiorealbutton.R.id.ceryle_radioRealButton_container);
        container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onClickedButton)
                    onClickedButton.onClickedButton(view);
            }
        });

        imageView = (ImageView) view.findViewById(co.ceryle.radiorealbutton.R.id.ceryle_radioRealButton_imageView);
        textView = (TextView) view.findViewById(co.ceryle.radiorealbutton.R.id.ceryle_radioRealButton_textView);

        setImageAttrs();
        setTextAttrs();
        setOtherAttrs();
    }

    private OnClickedButton onClickedButton;

    public void setOnClickedButton(OnClickedButton onClickedButton) {
        this.onClickedButton = onClickedButton;
    }

    public interface OnClickedButton {
        void onClickedButton(View view);
    }

    private int buttonImage, buttonImageTint, buttonTextColor, buttonBackgroundColor, buttonRippleColor, buttonImageWidth, buttonImageHeight, buttonPadding, buttonPaddingLeft, buttonPaddingRight, buttonPaddingTop, buttonPaddingBottom, marginBetweenImgAndText;

    private String buttonText;
    private boolean buttonRipple, hasPadding, hasPaddingLeft, hasPaddingRight, hasPaddingTop, hasPaddingBottom, hasButtonImageTint, hasImage, hasText;


    private boolean imageLeft, imageRight, imageTop, imageBottom;

    private void getAttributes(AttributeSet attrs) {
        /** GET ATTRIBUTES FROM XML **/
        // Custom attributes
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RadioRealButton);

        hasImage = typedArray.hasValue(R.styleable.RadioRealButton_rrb_image);
        buttonImage = typedArray.getResourceId(R.styleable.RadioRealButton_rrb_image, -1);
        buttonImageTint = typedArray.getColor(R.styleable.RadioRealButton_rrb_imageTint, 0);
        hasButtonImageTint = typedArray.hasValue(R.styleable.RadioRealButton_rrb_imageTint);
        buttonImageWidth = (int) typedArray.getDimension(R.styleable.RadioRealButton_rrb_imageWidth, -1);
        buttonImageHeight = (int) typedArray.getDimension(R.styleable.RadioRealButton_rrb_imageHeight, -1);

        hasText = typedArray.hasValue(R.styleable.RadioRealButton_rrb_text);
        buttonText = typedArray.getString(R.styleable.RadioRealButton_rrb_text);
        buttonTextColor = typedArray.getColor(R.styleable.RadioRealButton_rrb_textColor, Color.BLACK);

        buttonRipple = typedArray.getBoolean(R.styleable.RadioRealButton_rrb_ripple, false);
        buttonRippleColor = typedArray.getColor(R.styleable.RadioRealButton_rrb_rippleColor, -1);

        buttonBackgroundColor = typedArray.getColor(R.styleable.RadioRealButton_rrb_backgroundColor, -1);

        buttonPadding = (int) typedArray.getDimension(R.styleable.RadioRealButton_rrb_buttonPadding, 0);
        buttonPaddingLeft = (int) typedArray.getDimension(R.styleable.RadioRealButton_rrb_buttonPaddingLeft, 0);
        buttonPaddingRight = (int) typedArray.getDimension(R.styleable.RadioRealButton_rrb_buttonPaddingRight, 0);
        buttonPaddingTop = (int) typedArray.getDimension(R.styleable.RadioRealButton_rrb_buttonPaddingTop, 0);
        buttonPaddingBottom = (int) typedArray.getDimension(R.styleable.RadioRealButton_rrb_buttonPaddingBottom, 0);

        hasPadding = typedArray.hasValue(R.styleable.RadioRealButton_rrb_buttonPadding);
        hasPaddingLeft = typedArray.hasValue(R.styleable.RadioRealButton_rrb_buttonPaddingLeft);
        hasPaddingRight = typedArray.hasValue(R.styleable.RadioRealButton_rrb_buttonPaddingRight);
        hasPaddingTop = typedArray.hasValue(R.styleable.RadioRealButton_rrb_buttonPaddingTop);
        hasPaddingBottom = typedArray.hasValue(R.styleable.RadioRealButton_rrb_buttonPaddingBottom);

        marginBetweenImgAndText = (int) typedArray.getDimension(R.styleable.RadioRealButton_rrb_marginBetweenImageAndText, 4);

        imageLeft = typedArray.getBoolean(R.styleable.RadioRealButton_rrb_imageLeft, true);
        imageRight = typedArray.getBoolean(R.styleable.RadioRealButton_rrb_imageRight, false);
        imageTop = typedArray.getBoolean(R.styleable.RadioRealButton_rrb_imageTop, false);
        imageBottom = typedArray.getBoolean(R.styleable.RadioRealButton_rrb_imageBottom, false);

        typedArray.recycle();
    }

    private void setImageAttrs() {
        if (buttonImage != -1) {
            imageView.setImageResource(buttonImage);
            if (hasButtonImageTint)
                imageView.setColorFilter(buttonImageTint);
        } else {
            imageView.setVisibility(GONE);
        }

        if (buttonImageWidth != -1)
            setImageSizePixel(buttonImageWidth, buttonImageHeight);


        if (imageRight || imageBottom) {
            container.removeViewAt(0);
            container.addView(imageView, 1);
        }

        if ((imageTop || imageBottom) && hasText && hasImage)
            container.setOrientation(VERTICAL);
    }

    private void setTextAttrs() {
        textView.setText(buttonText);
        textView.setTextColor(buttonTextColor);
    }

    private void setOtherAttrs() {
        if (hasPadding)
            setButtonPadding(buttonPadding, buttonPadding, buttonPadding, buttonPadding);
        else if (hasPaddingBottom || hasPaddingTop || hasPaddingLeft || hasPaddingRight)
            setButtonPadding(buttonPaddingLeft, buttonPaddingTop, buttonPaddingRight, buttonPaddingBottom);
        else
            setButtonPadding(30, 30, 30, 30);


        int backgroundColor = Color.WHITE;
        int rippleColor = Color.GRAY;

        boolean hasRipple = false;

        if (buttonRippleColor != -1) {
            rippleColor = buttonRippleColor;
            hasRipple = true;
        } else if (buttonRipple)
            hasRipple = true;

        if (buttonBackgroundColor != -1) {
            backgroundColor = buttonBackgroundColor;
        }

        if (hasRipple)
            RippleHelper.setRipple(container, backgroundColor, rippleColor);
        else
            container.setBackgroundColor(backgroundColor);
    }

    public void setImageSizePixel(int width, int height) {
        if (width != -1)
            imageView.getLayoutParams().width = width;
        if (height != -1)
            imageView.getLayoutParams().height = height;
    }

    public void setImageSizeDp(int width, int height) {
        imageView.getLayoutParams().width = (int) ConversionUtil.convertDpToPixel(width, getContext());
        imageView.getLayoutParams().height = (int) ConversionUtil.convertDpToPixel(height, getContext());
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setButtonPadding(int buttonPaddingLeft, int buttonPaddingTop, int buttonPaddingRight, int buttonPaddingBottom) {
        ViewGroup.MarginLayoutParams imageParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
        ViewGroup.MarginLayoutParams textParams = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();

        if (hasImage) {
            if (!hasText) {
                imageParams.setMargins(buttonPaddingLeft, buttonPaddingTop, buttonPaddingRight, buttonPaddingBottom);
            } else {
                if (imageRight)
                    imageParams.setMargins(marginBetweenImgAndText, buttonPaddingTop, buttonPaddingRight, buttonPaddingBottom);
                else if (imageTop)
                    imageParams.setMargins(buttonPaddingLeft, buttonPaddingTop, buttonPaddingRight, marginBetweenImgAndText);
                else if (imageBottom)
                    imageParams.setMargins(buttonPaddingLeft, marginBetweenImgAndText, buttonPaddingRight, buttonPaddingBottom);
                else
                    imageParams.setMargins(buttonPaddingLeft, buttonPaddingTop, marginBetweenImgAndText, buttonPaddingBottom);
            }
        }
        if (hasText) {
            if (!hasImage)
                textParams.setMargins(buttonPaddingLeft, buttonPaddingTop, buttonPaddingRight, buttonPaddingBottom);
            else {
                if (imageRight)
                    textParams.setMargins(buttonPaddingLeft, buttonPaddingTop, marginBetweenImgAndText, buttonPaddingBottom);
                else if (imageTop)
                    textParams.setMargins(buttonPaddingLeft, marginBetweenImgAndText, buttonPaddingRight, buttonPaddingBottom);
                else if (imageBottom)
                    textParams.setMargins(buttonPaddingLeft, buttonPaddingTop, buttonPaddingRight, marginBetweenImgAndText);
                else
                    textParams.setMargins(marginBetweenImgAndText, buttonPaddingTop, buttonPaddingRight, buttonPaddingBottom);
            }
        }
    }

    protected void bounceImage(float scale, int duration, Interpolator interpolator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            imageView.animate().setDuration(duration).setInterpolator(interpolator).scaleXBy(scale).scaleYBy(scale);
        }else{
            imageView.setScaleX(scale);
            textView.setScaleY(scale);
        }
    }

    protected void bounceText(float scale, int duration, Interpolator interpolator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            textView.animate().setDuration(duration).setInterpolator(interpolator).scaleXBy(scale).scaleYBy(scale);
        }else{
            textView.setScaleX(scale);
            textView.setScaleY(scale);
        }
    }

    // Direct access to imageView if it is needed
    public ImageView getImageView() {
        return imageView;
    }

    // Direct access to textView if it is needed
    public TextView getTextView() {
        return textView;
    }

    public void setRipple(int backgroundColor, int rippleColor) {
        RippleHelper.setRipple(container, backgroundColor, rippleColor);
    }

    public void setBackgroundColor(int backgroundColor) {
        container.setBackgroundColor(backgroundColor);
    }

    public int getButtonImage() {
        return buttonImage;
    }

    public void setButtonImage(int buttonImage) {
        this.buttonImage = buttonImage;
    }

    public int getButtonImageTint() {
        return buttonImageTint;
    }

    public void setButtonImageTint(int buttonImageTint) {
        this.buttonImageTint = buttonImageTint;
    }

    public int getButtonTextColor() {
        return buttonTextColor;
    }

    public void setButtonTextColor(int buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
    }

    public int getButtonBackgroundColor() {
        return buttonBackgroundColor;
    }

    public void setButtonBackgroundColor(int buttonBackgroundColor) {
        this.buttonBackgroundColor = buttonBackgroundColor;
    }

    public int getButtonRippleColor() {
        return buttonRippleColor;
    }

    public void setButtonRippleColor(int buttonRippleColor) {
        this.buttonRippleColor = buttonRippleColor;
    }

    public int getButtonImageWidth() {
        return buttonImageWidth;
    }

    public void setButtonImageWidth(int buttonImageWidth) {
        this.buttonImageWidth = buttonImageWidth;
    }

    public int getButtonImageHeight() {
        return buttonImageHeight;
    }

    public void setButtonImageHeight(int buttonImageHeight) {
        this.buttonImageHeight = buttonImageHeight;
    }

    public int getButtonPadding() {
        return buttonPadding;
    }

    public int getButtonPaddingLeft() {
        return buttonPaddingLeft;
    }

    public void setButtonPaddingLeft(int buttonPaddingLeft) {
        this.buttonPaddingLeft = buttonPaddingLeft;
    }

    public int getButtonPaddingRight() {
        return buttonPaddingRight;
    }

    public void setButtonPaddingRight(int buttonPaddingRight) {
        this.buttonPaddingRight = buttonPaddingRight;
    }

    public int getButtonPaddingTop() {
        return buttonPaddingTop;
    }

    public void setButtonPaddingTop(int buttonPaddingTop) {
        this.buttonPaddingTop = buttonPaddingTop;
    }

    public int getButtonPaddingBottom() {
        return buttonPaddingBottom;
    }

    public void setButtonPaddingBottom(int buttonPaddingBottom) {
        this.buttonPaddingBottom = buttonPaddingBottom;
    }

    public int getMarginBetweenImageAndText() {
        return marginBetweenImgAndText;
    }

    public void setMarginBetweenImageAndText(int marginBetweenImageAndText) {
        this.marginBetweenImgAndText = marginBetweenImageAndText;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public boolean isButtonRipple() {
        return buttonRipple;
    }

    public void setButtonRipple(boolean buttonRipple) {
        this.buttonRipple = buttonRipple;
    }

    public boolean isHasPadding() {
        return hasPadding;
    }

    public void setHasPadding(boolean hasPadding) {
        this.hasPadding = hasPadding;
    }

    public boolean isHasPaddingLeft() {
        return hasPaddingLeft;
    }

    public void setHasPaddingLeft(boolean hasPaddingLeft) {
        this.hasPaddingLeft = hasPaddingLeft;
    }

    public boolean isHasPaddingRight() {
        return hasPaddingRight;
    }

    public void setHasPaddingRight(boolean hasPaddingRight) {
        this.hasPaddingRight = hasPaddingRight;
    }

    public boolean isHasPaddingTop() {
        return hasPaddingTop;
    }

    public void setHasPaddingTop(boolean hasPaddingTop) {
        this.hasPaddingTop = hasPaddingTop;
    }

    public boolean isHasPaddingBottom() {
        return hasPaddingBottom;
    }

    public void setHasPaddingBottom(boolean hasPaddingBottom) {
        this.hasPaddingBottom = hasPaddingBottom;
    }

    public boolean isHasButtonImageTint() {
        return hasButtonImageTint;
    }

    public void setHasButtonImageTint(boolean hasButtonImageTint) {
        this.hasButtonImageTint = hasButtonImageTint;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public boolean isHasText() {
        return hasText;
    }

    public void setHasText(boolean hasText) {
        this.hasText = hasText;
    }
}
