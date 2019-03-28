package com.samuelchowi.intercorpretail.common;

import android.content.res.AssetManager;
import android.graphics.Typeface;

public class TypeFontUtils {

    public static Typeface loadHelveticaNormal(AssetManager assetManager) {
        return Typeface.createFromAsset(assetManager, "fonts/helvetica.otf");
    }

    public static Typeface loadHelveticaBold(AssetManager assetManager) {
        return Typeface.createFromAsset(assetManager, "fonts/helvetica_bold.ttf");
    }

    public static Typeface loadHelveticaNeueMedium(AssetManager assetManager) {
        return Typeface.createFromAsset(assetManager, "fonts/helvetica_neue_medium.ttf");
    }

    public static Typeface loadOmmnesSemiBold(AssetManager assetManager) {
        return Typeface.createFromAsset(assetManager, "fonts/omnes-semibold.otf");
    }
}
