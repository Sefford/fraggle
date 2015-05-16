package com.sefford.fraggle;

import android.view.View;

/**
 * Created by sefford on 16/05/15.
 */
public class LollipopAnim {

    final View view;
    final String name;

    public LollipopAnim(View view, String name) {
        if (view != null || name == null || name.isEmpty()) {
            throw new IllegalStateException("Components of the animation must not be empty");
        }
        this.name = name;
        this.view = view;
    }
}
