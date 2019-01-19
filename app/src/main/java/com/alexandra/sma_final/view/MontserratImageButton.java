package com.alexandra.sma_final.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageButton;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

public class MontserratImageButton extends AppCompatImageButton {

    public MontserratImageButton(Context context) {
        super(context);
        init();
    }

    public MontserratImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MontserratImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Montserrat-Regular.ttf");
//            setTypeface(tf);
        }
    }
}
