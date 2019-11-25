/*
 * Copyright (C) 2015 Saúl Díaz
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

import android.view.View

/**
 * Component to allow supporting the new Lollipop Fragment Animations through Fraggle
 *
 * @author Saul Diaz Gonzalez<sefford></sefford>@gmail.com>
 */
class LollipopAnim
/**
 * Creates a new instance of the LollipopAnim.
 *
 *
 * As a requirement View and Name cannot be null or empty, otherwise the constuctor will throw IllegalArgumentException.
 *
 * @param view A View in a disappearing Fragment to match with a View in an appearing Fragment.
 * @param name The transitionName for a View in an appearing Fragment to match to the shared element.
 */
(
        /**
         * A View in a disappearing Fragment to match with a View in an appearing Fragment.
         */
        val view: View,
        /**
         * The transitionName for a View in an appearing Fragment to match to the shared element.
         */
        val name: String) {

    init {
        require(name.isNotEmpty()) { "Components of the animation must not be empty" }
    }
}
