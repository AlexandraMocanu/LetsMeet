package com.alexandra.sma_final.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;

public class MontserratButton extends AppCompatButton {

    public MontserratButton(Context context) {
        super(context);
        init();
    }

    public MontserratButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MontserratButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Montserrat-Regular.ttf");
            setTypeface(tf);
        }
    }
}
