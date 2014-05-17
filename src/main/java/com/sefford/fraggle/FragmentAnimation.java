package com.sefford.fraggle;

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
public class FragmentAnimation {

    /**
     * Constant for no animation with short duration
     */
    public static final int NO_ANIMATION = 0;
    /**
     * Animation for when the Fragment is loaded
     */
    private int enterAnim = NO_ANIMATION;
    /**
     * Animation for when the fragment is removed
     */
    private int exitAnim = NO_ANIMATION;
    /**
     * Animation for when the fragment is pushed into the stack
     */
    private int pushInAnim = NO_ANIMATION;
    /**
     * Animation for when the fragment is popped from the stack
     */
    private int popOutAnim = NO_ANIMATION;

    /**
     * Creates a partially completed fragment animation, only with enter and exit animation
     *
     * @param enterAnim Enter animaition
     * @param exitAnim  Exit animation
     */
    public FragmentAnimation(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
    }

    /**
     * Creates a fully completed fragment animation, with all the required animations (enter, exit, pop out of the stack
     * and push into the stack
     *
     * @param enterAnim  Enter animaition
     * @param exitAnim   Exit animation
     * @param popInAnim  Pop in animation
     * @param popOutAnim Pop out animation
     */
    public FragmentAnimation(int enterAnim, int exitAnim, int popInAnim, int popOutAnim) {
        this(enterAnim, exitAnim);
        this.pushInAnim = popInAnim;
        this.popOutAnim = popOutAnim;
    }

    public int getEnterAnim() {
        return enterAnim;
    }

    public void setEnterAnim(int enterAnim) {
        this.enterAnim = enterAnim;
    }

    public int getExitAnim() {
        return exitAnim;
    }

    public void setExitAnim(int exitAnim) {
        this.exitAnim = exitAnim;
    }

    public int getPushInAnim() {
        return pushInAnim;
    }

    public void setPushInAnim(int pushInAnim) {
        this.pushInAnim = pushInAnim;
    }

    public int getPopOutAnim() {
        return popOutAnim;
    }

    public void setPopOutAnim(int popOutAnim) {
        this.popOutAnim = popOutAnim;
    }

    /**
     * Return if the animation covers the four possible animations
     *
     * @return TRUE if any of the pop out animation of the push in animation are defined, FALSE otherwise
     */
    public boolean isCompletedAnimation() {
        return popOutAnim != NO_ANIMATION || pushInAnim != NO_ANIMATION;
    }
}