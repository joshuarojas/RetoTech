package com.samuelchowi.intercorpretail.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.samuelchowi.intercorpretail.R;
import com.samuelchowi.intercorpretail.common.TypeFontUtils;

import androidx.appcompat.widget.AppCompatTextView;

public class IRTextView extends AppCompatTextView {

    public IRTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.IRTextStyle,
                0, 0);

        int fontType;
        try {
            fontType = array.getInteger(R.styleable.IRTextStyle_customFont, 0);
        } finally {
            array.recycle();
        }

        setTypeface(getCustomTypeFace(fontType));
    }

    private Typeface getCustomTypeFace(int fontType) {
        switch (fontType) {
            case 0:
                return TypeFontUtils.loadHelveticaNormal(getContext().getAssets());
            case 1:
                return TypeFontUtils.loadHelveticaBold(getContext().getAssets());
            case 2:
                return TypeFontUtils.loadHelveticaNeueMedium(getContext().getAssets());
            default:
                return TypeFontUtils.loadOmmnesSemiBold(getContext().getAssets());
        }
    }
}
