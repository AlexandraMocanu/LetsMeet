package com.alexandra.sma_final.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatTextView;

public class MontserratAutocompleteText extends AppCompatAutoCompleteTextView {
    public MontserratAutocompleteText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MontserratAutocompleteText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MontserratAutocompleteText(Context context) {
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
