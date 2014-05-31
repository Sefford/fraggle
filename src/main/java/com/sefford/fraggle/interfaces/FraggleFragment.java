package com.sefford.fraggle.interfaces;

/*
 * Copyright (C) 2014 Saúl Díaz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Saúl Díaz González <sefford@gmail.com>
 */
public interface FraggleFragment {
    /**
     * Returns the Fragment tag. This is a convenience method for both providing identification
     * of the Fragment in the {@link com.sefford.fraggle.interfaces.Logger Logger)
     * Logger} methods and through some of the {@link com.sefford.fraggle.FraggleManager FraggleManager}
     * operations.
     * <p/>
     * This tag is intended to be unique across all the application's fragments.
     *
     * @return A String with an ID of the Fragment.
     */
    String getFragmentTag();

    /**
     * An addition to the Fragment lifecycle to ensure that the developer has a chance to perform
     * some activation after the fragment is reactivated into the fragment stack. It will be executed
     * after the {@link android.app.Fragment#onResume() onResume()} method.
     */
    void onFragmentVisible();

    /**
     * An addition to the Fragment lifecycle to ensure that the developer has a chance to perform
     * some activation before the fragment is saved into the fragment backstack. It will be executed
     * after the {@link android.app.Fragment#onDestroyView() onDestroyView()} method.
     */
    void onFragmentNotVisible();

    /**
     * Flags the {@link com.sefford.fraggle.FraggleManager FraggleManager} if there is to be expected
     * a certain behavior on pressing the back button or performing a back operation. This can
     * be perform through {@link #onBackPressed() onBackPressed()} method.
     *
     * @return TRUE if the Fragment needs to delegate some custom back operation to the Fragment, FALSE
     * otherwise
     */
    boolean customizedOnBackPressed();

    /**
     * Indicates if the current Fragment should return back to other different Fragment than the
     * previous one. This will allow the {@link com.sefford.fraggle.FraggleManager#popBackStack(String, int) popBackstack(String, int)}
     * to do a jump several Fragments away.
     *
     * @return Valid FraggleFragment tag to provide the jump.
     */
    String onBackPressedTarget();

    /**
     * Utility method to execute custom back button press actions besides returning to the previous
     * fragment
     */
    void onBackPressed();
}
