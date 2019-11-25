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
package com.sefford.fraggle.interfaces

import android.os.Bundle

/**
 * Interface that adds some utility methods to the Fragment Class to be used with
 * [Fraggle][com.sefford.fraggle.Fraggle]
 *
 * @author Saúl Díaz González <sefford@gmail.com>
 */
interface FraggleFragment {

    /**
     * Returns the Fragment tag. This is a convenience method for both providing identification
     * of the Fragment in the [Loggable)][com.sefford.common.interfaces.Loggable] methods and through some of the [Fraggle][com.sefford.fraggle.Fraggle]
     * operations.
     *
     *
     * This tag is intended to be unique across all the application's fragments.
     *
     * @return A String with an ID of the Fragment.
     */
    val fragmentTag: String

    /**
     * Checks if it is an Entry Framgent.
     *
     *
     * An entry fragment is considered any Fragment which upon a back button press will exit the application
     * instead of popping a Fragment from the back stack.
     *
     *
     * The default behavior for this method is to declare that no fragment is an entry fragment. The
     * developer can override this method to declare under which conditions a fragment is considered
     * "entry fragment".
     *
     * @return TRUE if is is one of those, FALSE otherwise
     */
    val isEntryFragment: Boolean

    /**
     * Discerns if we can popBackStack a Fragment.
     *
     *
     * The default behavior is to always add the Fragment. However the developer might find useful
     * to implement an "up" navigation and avoid infinite loop navigation through their application.
     *
     *
     * If the Fragment is not found to need to be added, the Fraggle will look for the
     * first known instance of it on the backstack and pop back all the fragments until it.
     *
     * @return TRUE if the Fragment needs to be Instantiated, FALSE if the fragment was popped
     */
    val isSingleInstance: Boolean

    /**
     * Flags the [Fraggle][com.sefford.fraggle.Fraggle] if there is to be expected
     * a certain behavior on pressing the back button or performing a back operation. This can
     * be perform through [onBackPressed()][.onBackPressed] method.
     *
     * @return TRUE if the Fragment needs to delegate some custom back operation to the Fragment, FALSE
     * otherwise
     */
    val customizedOnBackPressed : Boolean
    /**
     * Indicates if the current Fragment should return back to other different Fragment than the
     * previous one. This will allow the [popBackstack(String, int)][com.sefford.fraggle.Fraggle.popBackStack]
     * to do a jump several Fragments away.
     *
     * @return Valid FraggleFragment tag to provide the jump.
     */
    val onBackPressedTarget : String
    /**
     * An addition to the Fragment lifecycle to ensure that the developer has a chance to perform
     * some activation after the fragment is reactivated into the fragment stack. It will be executed
     * after the [onResume()][android.app.Fragment.onResume] method.
     */
    fun onFragmentVisible()

    /**
     * An addition to the Fragment lifecycle to ensure that the developer has a chance to perform
     * some activation before the fragment is saved into the fragment backstack. It will be executed
     * after the [onDestroyView()][android.app.Fragment.onDestroyView] method.
     */
    fun onFragmentNotVisible()

    /**
     * Utility method to execute custom back button press actions besides returning to the previous
     * fragment
     */
    fun onBackPressed()

    /**
     * Passes the new arguments into the current fragment. This is applied only in the case of single instance fragments
     * being instanced several times. Executes before [onFragmentVisible()][FraggleFragment.onFragmentVisible]
     *
     * @param arguments New arguments passed to the fragment
     */
    fun onNewArgumentsReceived(arguments: Bundle)
}
