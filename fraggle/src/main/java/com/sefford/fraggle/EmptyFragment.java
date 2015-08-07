package com.sefford.fraggle;

import com.sefford.fraggle.interfaces.FraggleFragment;

/**
 * Created by sefford on 7/08/15.
 */
public class EmptyFragment implements FraggleFragment {

    public static final String TAG = "";

    @Override
    public boolean customizedOnBackPressed() {
        return false;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }

    @Override
    public boolean isEntryFragment() {
        return false;
    }

    @Override
    public boolean isSingleInstance() {
        return false;
    }

    @Override
    public void onFragmentVisible() {

    }

    @Override
    public void onFragmentNotVisible() {

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public String onBackPressedTarget() {
        return null;
    }
}
