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

/**
 * Fraggle wraps some common operations over Android's FragmentManager concerning the
 * addition, query and removal of Fragments.
 *
 * @author Saúl Díaz González <sefford></sefford>@gmail.com>
 */
object Fraggle {
    /**
     * Flag for normal backstack operation.
     *
     *
     * This means the backstack will not be cleared and the new fragment will be added atop of the
     * current one.
     */
    const val ADD_TO_BACKSTACK = 0
    /**
     * Flags addFragment method it has to add the fragment to the backstack.
     */
    const val DO_NOT_ADD_TO_BACKSTACK = 1
    /**
     * Flags addFragment method it has to clear the backstack.
     *
     *
     * All the previous fragments will be discarded.
     */
    const val CLEAR_BACKSTACK = 1 shl 1
    /**
     * Flag indicating to replace or not the fragment.
     *
     *
     * If the Fragment needs to be replaced, will Fraggle will substitue the current Fragment
     * with the new one. Otherwise the new will appear on top of the current one and call
     * [onFragmentNotVisible][com.sefford.fraggle.interfaces.FraggleFragment.onFragmentNotVisible]
     * instead
     */
    const val DO_NOT_REPLACE_FRAGMENT = 1 shl 2
}