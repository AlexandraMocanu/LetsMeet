package com.alexandra.sma_final.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.google.android.material.textfield.TextInputEditText;

public class MontserratInputEditText extends TextInputEditText {

    public MontserratInputEditText(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

    public MontserratInputEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

    public MontserratInputEditText(Context context) {
            super(context);
            init();
        }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Montserrat-Regular.ttf");
            setTypeface(tf);
        }
    }
}

