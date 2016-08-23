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
 */
package com.sefford.fraggle.interfaces;

import android.os.Bundle;

/**
 * Interface that adds some utility methods to the Fragment Class to be used with
 * {@link com.sefford.fraggle.FraggleManager FraggleManager}
 *
 * @author Saúl Díaz González <sefford@gmail.com>
 */
public interface FraggleFragment {
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
     * Returns the Fragment tag. This is a convenience method for both providing identification
     * of the Fragment in the {@link com.sefford.common.interfaces.Loggable Loggable)
     * Logger} methods and through some of the {@link com.sefford.fraggle.FraggleManager FraggleManager}
     * operations.
     * <p/>
     * This tag is intended to be unique across all the application's fragments.
     *
     * @return A String with an ID of the Fragment.
     */
    String getFragmentTag();

    /**
     * Checks if it is an Entry Framgent.
     * <p/>
     * An entry fragment is considered any Fragment which upon a back button press will exit the application
     * instead of popping a Fragment from the back stack.
     * <p/>
     * The default behavior for this method is to declare that no fragment is an entry fragment. The
     * developer can override this method to declare under which conditions a fragment is considered
     * "entry fragment".
     *
     * @return TRUE if is is one of those, FALSE otherwise
     */
    boolean isEntryFragment();

    /**
     * Discerns if we can popBackStack a Fragment.
     * <p/>
     * The default behavior is to always add the Fragment. However the developer might find useful
     * to implement an "up" navigation and avoid infinite loop navigation through their application.
     * <p/>
     * If the Fragment is not found to need to be added, the FraggleManager will look for the
     * first known instance of it on the backstack and pop back all the fragments until it.
     *
     * @return TRUE if the Fragment needs to be Instantiated, FALSE if the fragment was popped
     */
    boolean isSingleInstance();

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
     * Utility method to execute custom back button press actions besides returning to the previous
     * fragment
     */
    void onBackPressed();

    /**
     * Indicates if the current Fragment should return back to other different Fragment than the
     * previous one. This will allow the {@link com.sefford.fraggle.FraggleManager#popBackStack(String, int) popBackstack(String, int)}
     * to do a jump several Fragments away.
     *
     * @return Valid FraggleFragment tag to provide the jump.
     */
    String onBackPressedTarget();

    /**
     * Passes the new arguments into the current fragment. This is applied only in the case of single instance fragments
     * being instanced several times. Executes before {@link FraggleFragment#onFragmentVisible() onFragmentVisible()}
     *
     * @param arguments New arguments passed to the fragment
     */
    void onNewArgumentsReceived(Bundle arguments);
}
