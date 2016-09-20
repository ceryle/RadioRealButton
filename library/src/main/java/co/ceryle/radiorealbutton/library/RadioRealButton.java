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

        marginBetweenImgAndText = (int) typedArray.getDimension(R.styleable.RadioRealButton_rrb_marginBetweenImgAndText, 4);

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
    }

    private void setTextAttrs() {
        textView.setText(buttonText);
        textView.setTextColor(buttonTextColor);
    }

    private void setOtherAttrs() {
        if (hasPadding)
            setButtonPadding(buttonPadding);
        else if (hasPaddingBottom || hasPaddingTop || hasPaddingLeft || hasPaddingRight)
            setButtonPadding(buttonPaddingLeft, buttonPaddingTop, buttonPaddingRight, buttonPaddingBottom);
        else
            setButtonPadding(30);


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
        if (hasImage) {
            if(!hasText)
                marginBetweenImgAndText = buttonPaddingRight;
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            p.setMargins(buttonPaddingLeft, buttonPaddingTop, marginBetweenImgAndText, buttonPaddingBottom);
        }
        if (hasText) {
            if(!hasImage)
                marginBetweenImgAndText = buttonPaddingLeft;
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            p.setMargins(marginBetweenImgAndText, buttonPaddingTop, buttonPaddingRight, buttonPaddingBottom);
        }
    }

    public void setButtonPadding(int buttonPadding) {
        if (hasImage) {
            if(!hasText)
                marginBetweenImgAndText = buttonPadding;
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            p.setMargins(buttonPadding, buttonPadding, marginBetweenImgAndText, buttonPadding);
        }
        if (hasText) {
            if(!hasImage)
                marginBetweenImgAndText = buttonPadding;
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            p.setMargins(marginBetweenImgAndText, buttonPadding, buttonPadding, buttonPadding);
        }
    }

    protected void bounceImage(float scale, int duration, Interpolator interpolator) {
        imageView.animate().setDuration(duration).setInterpolator(interpolator).scaleXBy(scale).scaleYBy(scale);
    }

    protected void bounceText(float scale, int duration, Interpolator interpolator) {
        textView.animate().setDuration(duration).setInterpolator(interpolator).scaleXBy(scale).scaleYBy(scale);
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
}
