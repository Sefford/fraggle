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

import android.os.Build;

/**
 * Encapsulates an object to pass some animations to the {@link com.sefford.fraggle.FraggleManager FraggleManager}
 * and ultimely Android's FragmentManager to add some eye candy to the transitions.
 * <p/>
 * The FragmentAnimation class accepts only R.animator ids from android.R classes.
 *
 * @author Saúl Díaz González <sefford@gmail.com>
 */
public class FragmentAnimation {

    /**
     * Constant for no animation with short duration
     */
    public static final int NO_ANIMATION = 0;
    /**
     * Empty constant for no animations
     */
    public static final LollipopAnim[] EMPTY_LOLLIPOP_ANIMS = new LollipopAnim[0];
    /**
     * Animation for when the Fragment is loaded
     */
    protected int enterAnim = NO_ANIMATION;
    /**
     * Animation for when the fragment is removed
     */
    protected int exitAnim = NO_ANIMATION;
    /**
     * Animation for when the fragment is pushed into the stack
     */
    protected int pushInAnim = NO_ANIMATION;
    /**
     * Animation for when the fragment is popped from the stack
     */
    protected int popOutAnim = NO_ANIMATION;
    /**
     * Disappearing View for Lollipop Animations
     */
    protected LollipopAnim[] sharedViews;

    /**
     * Creates a partially completed fragment animation, only with enter and exit animation
     *
     * @param enterAnim Enter animaition
     * @param exitAnim  Exit animation
     */
    public FragmentAnimation(int enterAnim, int exitAnim) {
        this(enterAnim, exitAnim, NO_ANIMATION, NO_ANIMATION, EMPTY_LOLLIPOP_ANIMS);
    }

    /**
     * Creates a partially completed fragment animation, only with enter and exit animation plus Lollipop Animations
     *
     * @param enterAnim   Enter animaition
     * @param exitAnim    Exit animation
     * @param sharedViews Array with necessary elements to build one or several Lollipop animations
     */
    public FragmentAnimation(int enterAnim, int exitAnim, LollipopAnim[] sharedViews) {
        this(enterAnim, exitAnim, NO_ANIMATION, NO_ANIMATION, sharedViews);
    }

    /**
     * Creates a fully completed fragment animation, with all the required animations (enter, exit, pop out of the stack
     * and push into the stack
     *
     * @param enterAnim  Enter animaition
     * @param exitAnim   Exit animation
     * @param pushInAnim Pop in animation
     * @param popOutAnim Pop out animation
     */
    public FragmentAnimation(int enterAnim, int exitAnim, int pushInAnim, int popOutAnim) {
        this(enterAnim, exitAnim, pushInAnim, popOutAnim, EMPTY_LOLLIPOP_ANIMS);
    }

    /**
     * Creates a fully completed fragment animation, with all the required animations (enter, exit, pop out of the stack
     * and push into the stack
     *
     * @param enterAnim   Enter animaition
     * @param exitAnim    Exit animation
     * @param pushInAnim  Pop in animation
     * @param popOutAnim  Pop out animation
     * @param sharedViews Array with necessary elements to build one or several Lollipop animations.
     */
    public FragmentAnimation(int enterAnim, int exitAnim, int pushInAnim, int popOutAnim, LollipopAnim[] sharedViews) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        this.pushInAnim = pushInAnim;
        this.popOutAnim = popOutAnim;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            this.sharedViews = sharedViews;
        } else {
            this.sharedViews = EMPTY_LOLLIPOP_ANIMS;
        }
    }

    /**
     * Retrieves the enter animation
     *
     * @return R.animator ID of enter animation
     */
    public int getEnterAnim() {
        return enterAnim;
    }

    /**
     * Retrieves the exit animation
     *
     * @return R.animator ID of exit animation
     */
    public int getExitAnim() {
        return exitAnim;
    }

    /**
     * Retrieves the push in animation
     *
     * @return R.animator ID of push in animation
     */
    public int getPushInAnim() {
        return pushInAnim;
    }

    /**
     * Retrieves the pop out animation
     *
     * @return R.animator ID of pop out animation
     */
    public int getPopOutAnim() {
        return popOutAnim;
    }

    /**
     * Return if the animation covers the four possible animations:
     * <ul>
     * <li>New Fragment being shown</li>
     * <li>Old Fragment being stored to the backstack</li>
     * <li>New Fragment being being removed</li>
     * <li>Old Fragment being popped</li>
     * <p/>
     * </ul>
     *
     * @return TRUE if any of the pop out animation of the push in animation are defined, FALSE otherwise
     */
    public boolean isCompletedAnimation() {
        return popOutAnim != NO_ANIMATION || pushInAnim != NO_ANIMATION;
    }

    public LollipopAnim[] getSharedViews() {
        return sharedViews;
    }
}