package com.sefford.fraggle;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.sefford.fraggle.interfaces.FraggleFragment;
import com.sefford.fraggle.interfaces.Logger;

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
public class FraggleManager {
    /**
     * Flag for normal backstack operation
     */
    public static final int ADD_TO_BACKSTACK = 0;
    /**
     * Flags addFragment method it has to add the fragment to the backstack
     */
    public static final int DO_NOT_ADD_TO_BACKSTACK = 1;
    /**
     * Flags addFragment method it has to clear the backstack
     */
    public static final int CLEAR_BACKSTACK = (1 << 1);
    /**
     * Flag indicating to replace or not the fragment
     */
    public static final int DO_NOT_REPLACE_FRAGMENT = (1 << 2);
    protected static final String TAG = "FraggleManager";
    /**
     * Logger facilities
     */
    private final Logger log;
    /**
     * Injected Fragment Manager
     */
    private FragmentManager fm;

    /**
     * Creates a new instance of Fraggle Manager
     *
     * @param log Logger Facilities
     */
    public FraggleManager(Logger log) {
        this.log = log;
    }

    public void initialize(FragmentManager fm) {
        this.fm = fm;
    }

    /**
     * Calculates the correct mode of adding a Fragment
     *
     * @return
     */
    protected int calculateCorrectMode() {
        return fm.getBackStackEntryCount() == 0 ? 0 : DO_NOT_REPLACE_FRAGMENT;
    }

    /**
     * Adds a fragment to the activity content viewgroup.
     *
     * @param frag        Fragment to add
     * @param title       Title to set to the actionBar
     * @param flags       Adds flags to manipulate the state of the backstack
     * @param containerId Container ID where to insert the fragment
     */
    public void addFragment(Fragment frag, String title, FragmentAnimation animation, int flags, int containerId) {
        if (frag != null) {
            if (needsToAddTheFragment(((FraggleFragment) frag).getFragmentTag())) {
                FragmentTransaction ft = fm.beginTransaction();
                processClearBackstack(flags);
                processAddToBackstackFlag(title, flags, ft);
                processAnimations(animation, ft);
                performTransaction(frag, flags, ft, containerId);
            } else {
                fm.popBackStack(((FraggleFragment) frag).getFragmentTag(), 0);
                peek(title).onFragmentVisible();
            }
        }
    }

    /**
     * Returns the first fragment in the stack with the name "tag"
     *
     * @param tag Tag to look for in the Fragment stack
     * @return First fragment in the stack with the name Tag
     */
    protected FraggleFragment peek(String tag) {
        return (FraggleFragment) fm.findFragmentByTag(tag);
    }

    /**
     * Process Clear backstack flag
     *
     * @param flags Added flags to the Fragment configuration
     */
    protected void processClearBackstack(int flags) {
        if ((flags & CLEAR_BACKSTACK) == CLEAR_BACKSTACK) {
            try {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (IllegalStateException exception) {
                log.e(TAG, exception.getMessage(), exception);
            }
        }
    }

    /**
     * Processes Add to Backstack flag
     *
     * @param title Title of the fragment
     * @param flags Added flags to the Fragment configuration
     * @param ft    Transaction to add to backstack from
     */
    protected void processAddToBackstackFlag(String title, int flags, FragmentTransaction ft) {
        if ((flags & DO_NOT_ADD_TO_BACKSTACK) != DO_NOT_ADD_TO_BACKSTACK) {
            ft.addToBackStack(title);
        }
    }

    /**
     * Processes the custom animations element, adding them as required
     *
     * @param animation Animation object to process
     * @param ft        Fragment transaction to add to the transition
     */
    protected void processAnimations(FragmentAnimation animation, FragmentTransaction ft) {
        if (animation != null) {
            if (animation.isCompletedAnimation()) {
                ft.setCustomAnimations(animation.getEnterAnim(), animation.getExitAnim(),
                        animation.getPushInAnim(), animation.getPopOutAnim());
            } else {
                ft.setCustomAnimations(animation.getEnterAnim(), animation.getExitAnim());
            }
        }
    }

    /**
     * Configures the way to add the Fragment into the transaction. It can vary from adding a new fragment,
     * to using a previous instance and refresh it, or replacing the last one.
     *
     * @param frag        Fragment to add
     * @param flags       Added flags to the Fragment configuration
     * @param ft          Transaction to add the fragment
     * @param containerId Target container ID
     */
    protected void configureAdditionMode(Fragment frag, int flags, FragmentTransaction ft, int containerId) {
        if ((flags & DO_NOT_REPLACE_FRAGMENT) != DO_NOT_REPLACE_FRAGMENT) {
            ft.replace(containerId, frag, ((FraggleFragment) frag).getFragmentTag());
        } else {
            ft.add(containerId, frag, ((FraggleFragment) frag).getFragmentTag());
            peek().onFragmentNotVisible();
        }
    }

    /**
     * Commits the transaction to the Fragment Manager
     *
     * @param frag        Fragment to add
     * @param flags       Added flags to the Fragment configuration
     * @param ft          Transaction to add the fragment
     * @param containerId Target containerID
     */
    protected void performTransaction(Fragment frag, int flags, FragmentTransaction ft, int containerId) {
        configureAdditionMode(frag, flags, ft, containerId);
        ft.commitAllowingStateLoss();
    }

    /**
     * Discerns if we can popBackStack a Fragment
     *
     * @param tag Tag to check
     * @return TRUE if the Fragment needs to be Instantiated, FALSE if the fragment was popped
     */
    protected boolean needsToAddTheFragment(String tag) {
        return true;
    }

    /**
     * Checks if it is an Entry Framgent
     *
     * @return TRUE if is is one of those, FALSE otherwise
     */
    public boolean isEntryFragment() {
        return false;
    }

    /**
     * Peeks the last fragment in the Fragment stack
     *
     * @return Last Fragment in the fragment stack
     * @throws java.lang.NullPointerException if there is no Fragment Added
     */
    protected FraggleFragment peek() {
        return ((FraggleFragment) fm.findFragmentByTag(
                fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName()));
    }

    /**
     * Decides what to do with the backstack.
     *
     * @param containerId Target container ID
     */
    public void popBackStack(int containerId) {
        FraggleFragment currentFragment = (FraggleFragment) fm.findFragmentById(containerId);
        if (!currentFragment.customizedOnBackPressed()) {
            if (currentFragment.onBackPressedTarget().isEmpty()) {
                fm.popBackStackImmediate();
                if (fm.getBackStackEntryCount() >= 1) {
                    peek().onFragmentVisible();
                }
            } else {
                //Clean all until containerId
                fm.popBackStack(currentFragment.onBackPressedTarget(), 0);
            }
        } else {
            currentFragment.onBackPressed();
        }
    }

    /**
     * Checks Backstack Entry Count
     *
     * @return Backstack Entry Count
     */
    public int getBackStackEntryCount() {
        return fm.getBackStackEntryCount();
    }
}