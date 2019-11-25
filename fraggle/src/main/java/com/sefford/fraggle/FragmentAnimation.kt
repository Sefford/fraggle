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
package com.sefford.fraggle

import android.os.Build

/**
 * Encapsulates an object to pass some animations to the [Fraggle][com.sefford.fraggle.Fraggle]
 * and ultimely Android's FragmentManager to add some eye candy to the transitions.
 *
 *
 * The FragmentAnimation class accepts only R.animator ids from android.R classes.
 *
 * @author Saúl Díaz González <sefford@gmail.com>
 */
class FragmentAnimation
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
@JvmOverloads constructor(val enterAnim: Int,
                          val exitAnim: Int,
                          val pushInAnim: Int = NO_ANIMATION,
                          val popOutAnim: Int = NO_ANIMATION,
                          sharedViews: List<LollipopAnim> = EMPTY_LOLLIPOP_ANIMS) {

    val sharedViews: List<LollipopAnim>

    /**
     * Return if the animation covers the four possible animations:
     *
     *  * New Fragment being shown
     *  * Old Fragment being stored to the backstack
     *  * New Fragment being being removed
     *  * Old Fragment being popped
     *
     *
     *
     *
     * @return TRUE if any of the pop out animation of the push in animation are defined, FALSE otherwise
     */
    val complete = popOutAnim != NO_ANIMATION || pushInAnim != NO_ANIMATION

    /**
     * Creates a partially completed fragment animation, only with enter and exit animation plus Lollipop Animations
     *
     * @param enterAnim   Enter animaition
     * @param exitAnim    Exit animation
     * @param sharedViews Array with necessary elements to build one or several Lollipop animations
     */
    constructor(enterAnim: Int, exitAnim: Int, sharedViews: List<LollipopAnim>) : this(enterAnim, exitAnim, NO_ANIMATION, NO_ANIMATION, sharedViews)

    init {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            this.sharedViews = sharedViews
        } else {
            this.sharedViews = EMPTY_LOLLIPOP_ANIMS
        }
    }

    companion object {

        /**
         * Constant for no animation with short duration
         */
        const val NO_ANIMATION = 0
        /**
         * Empty constant for no animations
         */
        val EMPTY_LOLLIPOP_ANIMS = emptyList<LollipopAnim>()
    }
}