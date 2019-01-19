package com.alexandra.sma_final.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class MontserratTextView extends AppCompatTextView {
    public MontserratTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MontserratTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MontserratTextView(Context context) {
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
