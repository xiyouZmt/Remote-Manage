package com.zmt.manager.CheckBox;

import android.content.Context;

/**
 * Created by Dangelo on 2016/1/26.
 */
public class CompatUtils {
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
