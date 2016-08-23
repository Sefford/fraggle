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
package com.sefford.fraggle;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;

import com.sefford.common.interfaces.Loggable;
import com.sefford.fraggle.interfaces.FraggleFragment;

/**
 * FraggleManager wraps some common operations over Android's FragmentManager concerning the
 * addition, query and removal of Fragments.
 *
 * @author Saúl Díaz González <sefford@gmail.com>
 */
public class FraggleManager {

    /**
     * Empty backstack to use as a placeholder for invalid situations
     */
    static final FragmentManager.BackStackEntry NULL_BACKSTACK = new FragmentManager.BackStackEntry() {
        @Override
        public int getId() {
            return 0;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public int getBreadCrumbTitleRes() {
            return 0;
        }

        @Override
        public int getBreadCrumbShortTitleRes() {
            return 0;
        }

        @Override
        public CharSequence getBreadCrumbTitle() {
            return "";
        }

        @Override
        public CharSequence getBreadCrumbShortTitle() {
            return "";
        }
    };

    /**
     * Flag for normal backstack operation.
     * <p/>
     * This means the backstack will not be cleared and the new fragment will be added atop of the
     * current one.
     */
    public static final int ADD_TO_BACKSTACK = 0;
    /**
     * Flags addFragment method it has to add the fragment to the backstack.
     */
    public static final int DO_NOT_ADD_TO_BACKSTACK = 1;
    /**
     * Flags addFragment method it has to clear the backstack.
     * <p/>
     * All the previous fragments will be discarded.
     */
    public static final int CLEAR_BACKSTACK = (1 << 1);
    /**
     * Flag indicating to replace or not the fragment.
     * <p/>
     * If the Fragment needs to be replaced, will FraggleManager will substitue the current Fragment
     * with the new one. Otherwise the new will appear on top of the current one and call
     * {@link com.sefford.fraggle.interfaces.FraggleFragment#onFragmentNotVisible() onFragmentNotVisible}
     * instead
     */
    public static final int DO_NOT_REPLACE_FRAGMENT = (1 << 2);
    /**
     * Logger tag
     */
    protected static final String TAG = "FraggleManager";
    /**
     * Logger facilities
     */
    protected final Loggable log;
    /**
     * Injected Fragment Manager
     */
    protected FragmentManager fm;

    /**
     * Creates a new instance of Fraggle Manager
     *
     * @param log Logger Facilities
     */
    public FraggleManager(Loggable log) {
        this.log = log;
    }

    /**
     * Initializes the Fraggle Manager by using a FragmentManager.
     *
     * @param fm FragmentManager to wrap around the FraggleManager.
     */
    public void initialize(FragmentManager fm) {
        this.fm = fm;
    }

    /**
     * Calculates the correct mode of adding a Fragment.
     * <p/>
     * If there are no available Fragments the FraggleManager will not try to call a previous
     * Fragment lifecycle.
     *
     * @return No flags if there are no fragments available or DO_NOT_REPLACE otherwise.
     */
    protected int calculateCorrectMode() {
        return fm.getBackStackEntryCount() == 0 ? 0 : DO_NOT_REPLACE_FRAGMENT;
    }

    /**
     * Adds a fragment to the activity content viewgroup. This will typically pass by a several
     * stages, in this order:
     * <ul>
     * <li>Considering if the necessity of {@link com.sefford.fraggle.interfaces.FraggleFragment#isSingleInstance() adding another instance of such fragment class}</li>
     * <li>{@link #processClearBackstack(int) Processing clearing backstack flags conditions}</li>
     * <li>{@link #processAddToBackstackFlag(String, int, android.app.FragmentTransaction) Process adding to backstack flags conditions}</li>
     * <li>{@link #processAnimations(FragmentAnimation, android.app.FragmentTransaction) Process the state of the deserved animations if any}</li>
     * <li>{@link #performTransaction(android.app.Fragment, int, android.app.FragmentTransaction, int) Perform the actual transaction}</li>
     * </ul>
     * <p/>
     * If the fragment is not required to be readded (as in a up navigation) the fragment manager
     * will pop all the backstack until the desired fragment and the {@link com.sefford.fraggle.interfaces.FraggleFragment#onFragmentVisible() onFragmentVisible()}
     * method will be called instead to bring up the dormant fragment.
     *
     * @param frag        Fragment to add
     * @param tag         Fragment tag
     * @param flags       Adds flags to manipulate the state of the backstack
     * @param containerId Container ID where to insert the fragment
     */
    public void addFragment(Fragment frag, String tag, FragmentAnimation animation, int flags, int containerId) {
        if (frag != null) {
            if ((!((FraggleFragment) frag).isSingleInstance()) || peek(tag) == null) {
                FragmentTransaction ft = fm.beginTransaction();
                processClearBackstack(flags);
                processAddToBackstackFlag(tag, flags, ft);
                processAnimations(animation, ft);
                performTransaction(frag, flags, ft, containerId);
            } else {
                fm.popBackStack(tag, 0);
                if (frag.getArguments() != null && !frag.getArguments().equals(((Fragment) peek(tag)).getArguments())) {
                    peek(tag).onNewArgumentsReceived(frag.getArguments());
                }
                peek(tag).onFragmentVisible();
            }
        }
    }

    /**
     * Returns the first fragment in the stack with the tag "tag".
     *
     * @param tag Tag to look for in the Fragment stack
     * @return First fragment in the stack with the name Tag
     */
    protected FraggleFragment peek(String tag) {
        if (fm != null) {
            return (FraggleFragment) fm.findFragmentByTag(tag);
        }
        return new EmptyFragment();
    }

    /**
     * Process Clear backstack flag.
     * <p/>
     * FraggleManager will clear the back stack before trying to add the next Fragment if
     * {@link #CLEAR_BACKSTACK CLEAR_BACKSTACK} flag is found
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
     * Processes Add to Backstack flag.
     * <p/>
     * Will not add the Fragment to the backstack if the
     * {@link #DO_NOT_ADD_TO_BACKSTACK DO_NOT_ADD_TO_BACKSTACK} flag is found.
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                for (LollipopAnim sharedElement : animation.getSharedViews()) {
                    ft.addSharedElement(sharedElement.view, sharedElement.name);
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
     * Commits the transaction to the Fragment Manager.
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
     * Peeks the last fragment in the Fragment stack.
     *
     * @return Last Fragment in the fragment stack
     * @throws java.lang.NullPointerException if there is no Fragment Added
     */
    protected FraggleFragment peek() {
        if (fm.getBackStackEntryCount() > 0) {
            return ((FraggleFragment) fm.findFragmentByTag(
                    fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 1).getName()));
        } else {
            return new EmptyFragment();
        }
    }

    /**
     * Decides what to do with the backstack.
     * <p/>
     * The default behavior is as follows:
     * <p/>
     * FraggleManager will determine if the Fragment has a
     * {@link com.sefford.fraggle.interfaces.FraggleFragment#customizedOnBackPressed() customized action(s) for backpressing}
     * If so, the Fraggle Manager will execute its {@link com.sefford.fraggle.interfaces.FraggleFragment#onBackPressed() onBackPressed()} method.
     * <p/>
     * If the Fragment does not have any kind of custom action, then the FraggleManager will try
     * to determine if there is a {@link com.sefford.fraggle.interfaces.FraggleFragment#onBackPressedTarget()}.
     * <p/>
     * If positive, the FraggleManager will pop until it finds the Fragment.
     * <p/>
     * Otherwise will pop the inmediate Fragment and execute its {@link com.sefford.fraggle.interfaces.FraggleFragment#onFragmentVisible()}
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
                popBackStack(currentFragment.onBackPressedTarget(), 0);
            }
        } else {
            currentFragment.onBackPressed();
        }
    }

    /**
     * Pops the Fragment with the tag, applying the necessary flags
     *
     * @param tag   Tag to look for in the Fragment stack
     * @param flags Flags to apply for the
     */
    public void popBackStack(String tag, int flags) {
        fm.popBackStack(tag, flags);
    }

    /**
     * Checks Backstack Entry Count.
     *
     * @return Backstack Entry Count.
     */
    public int getBackStackEntryCount() {
        return fm != null ? fm.getBackStackEntryCount() : 0;
    }

    /**
     * Clears fragment manager backstack and the fragment manager itself.
     */
    public void clear() {
        if (fm != null) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fm = null;
    }

    /**
     * Reattaches a Fragment
     *
     * @param tag Tag of the Fragment to reattach
     */
    public void reattach(String tag) {
        final Fragment currentFragment = (Fragment) peek(tag);
        FragmentTransaction fragTransaction = fm.beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
    }

    /**
     * Adds a {@link android.support.v4.app.FragmentManager.OnBackStackChangedListener OnBackStackChangedListener}
     *
     * @param listener Listener to add
     */
    public void addOnBackstackListener(FragmentManager.OnBackStackChangedListener listener) {
        fm.addOnBackStackChangedListener(listener);
    }


    /**
     * Removes a {@link android.support.v4.app.FragmentManager.OnBackStackChangedListener OnBackStackChangedListener}
     *
     * @param listener Listener to remove
     */
    public void removeOnBackstackListener(FragmentManager.OnBackStackChangedListener listener) {
        fm.removeOnBackStackChangedListener(listener);
    }

    /**
     * Returns if a fragment is already on the Fragment backstack
     *
     * @param tag Tag to check
     * @return TRUE if the fragment is contained, FALSE otherwise
     */
    public boolean contains(String tag) {
        return peek(tag) != null;
    }

    /**
     * Returns the backstack entry at index. Returns a {@link NULL_BACKSTACK NULL_BACKSTACK} if fragment is in an inconsistent state.
     *
     * @param index Index to check
     * @return the backstack
     */
    protected FragmentManager.BackStackEntry getBackStackEntryAt(int index) {
        return getBackStackEntryCount() <= 0 ? NULL_BACKSTACK : this.fm.getBackStackEntryAt(index);
    }


}