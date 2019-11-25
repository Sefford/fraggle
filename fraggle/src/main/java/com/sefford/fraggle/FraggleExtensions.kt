package com.sefford.fraggle

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.sefford.fraggle.Fraggle.CLEAR_BACKSTACK
import com.sefford.fraggle.Fraggle.DO_NOT_ADD_TO_BACKSTACK
import com.sefford.fraggle.Fraggle.DO_NOT_REPLACE_FRAGMENT
import com.sefford.fraggle.interfaces.FraggleFragment

/**
 * Clears fragment manager backstack and the fragment manager itself.
 */
fun FragmentManager.clear() {
    popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
}

/**
 * Returns if a fragment is already on the Fragment backstack
 *
 * @param tag Tag to check
 * @return TRUE if the fragment is contained, FALSE otherwise
 */
fun FragmentManager.contains(tag: String): Boolean {
    return peek(tag) != NoFragment
}

fun FragmentManager.currentFragmentHasCustomBack() = (peek() as? FraggleFragment)?.customizedOnBackPressed
        ?: false

fun FragmentManager.executeBack() = (peek() as? FraggleFragment)?.onBackPressed()

/**
 * Peeks the last fragment in the Fragment stack.
 *
 * @return Last Fragment in the fragment stack
 */
fun FragmentManager.peek(): Fragment {
    return if (backStackEntryCount > 0) {
        peek(getBackStackEntryAt(backStackEntryCount - 1).name)
    } else {
        NoFragment
    }
}

/**
 * Returns the first fragment in the stack with the tag "tag".
 *
 * @param tag Tag to look for in the Fragment stack
 * @return First fragment in the stack with the name Tag
 */
fun FragmentManager.peek(tag: String?): Fragment {
    return findFragmentByTag(tag) ?: NoFragment
}

/**
 * Returns the backstack entry at index. Returns a [NULL_BACKSTACK] if fragment is in an inconsistent state.
 *
 * @param index Index to check
 * @return the backstack
 */
fun FragmentManager.safeGetBackStackEntryCount(index: Int): FragmentManager.BackStackEntry {
    return if (backStackEntryCount <= 0) NullBackStack else getBackStackEntryAt(index)
}

/**
 * Decides what to do with the backstack.
 *
 *
 * The default behavior is as follows:
 *
 *
 * Fraggle will determine if the Fragment has a
 * [FraggleFragment.getCustomizedOnBackPressed]  customized action(s) for backpressing}
 * If so, the Fraggle Manager will execute its [onBackPressed()][com.sefford.fraggle.interfaces.FraggleFragment.onBackPressed] method.
 *
 *
 * If the Fragment does not have any kind of custom action, then the Fraggle will try
 * to determine if there is a [FraggleFragment.getCustomizedOnBackPressed].
 *
 *
 * If positive, the Fraggle will pop until it finds the Fragment.
 *
 *
 * Otherwise will pop the inmediate Fragment and execute its [com.sefford.fraggle.interfaces.FraggleFragment.onFragmentVisible]
 *
 * @param containerId Target container ID
 */
fun FragmentManager.popBackStack(containerId: Int) {
    val fragment = findFragmentById(containerId) as? FraggleFragment ?: NoFragment
    if (!fragment.customizedOnBackPressed) {
        if (fragment.onBackPressedTarget.isEmpty()) {
            popBackStackImmediate()
        } else {
            popBackStackImmediate(fragment.onBackPressedTarget, 0)
        }
        (peek() as? FraggleFragment)?.onFragmentVisible()
    } else {
        fragment.onBackPressed()
    }
}

fun FragmentManager.popBackScreenNumber(numOfScreens: Int) {
    if (backStackEntryCount > 0) {
        popBackStackImmediate(getBackStackEntryAt((backStackEntryCount - numOfScreens).coerceAtLeast(0)).name, 0)
    }
}

/**
 * Reattaches a Fragment
 *
 * @param tag Tag of the Fragment to reattach
 */
fun FragmentManager.reattach(tag: String) {
    val currentFragment = peek(tag)
    val fragTransaction = beginTransaction()
    fragTransaction.detach(currentFragment)
    fragTransaction.attach(currentFragment)
    fragTransaction.commit()
}


/**
 * Navigates to the determined screen
 *
 * @param navigator target destination
 */
fun FragmentManager.navigate(navigator: Navigator) {
    if (Looper.myLooper() === Looper.getMainLooper()) {
        addFragment(navigator)
    } else {
        Handler(Looper.getMainLooper()).post { addFragment(navigator) }
    }
}

/**
 * Adds a fragment to the activity content viewgroup. This will typically pass by a several
 * stages, in this order:
 *
 *  * Considering if the necessity of [adding another instance of such fragment class][com.sefford.fraggle.interfaces.FraggleFragment.isSingleInstance]
 *  * [Processing clearing backstack flags conditions][.processClearBackstack]
 *  * [Process adding to backstack flags conditions][.processAddToBackstackFlag]
 *  * [Process the state of the deserved animations if any][.processAnimations]
 *  * [Perform the actual transaction][.performTransaction]
 *
 *
 *
 * If the fragment is not required to be readded (as in a up navigation) the fragment manager
 * will pop all the backstack until the desired fragment and the [onFragmentVisible()][com.sefford.fraggle.interfaces.FraggleFragment.onFragmentVisible]
 * method will be called instead to bring up the dormant fragment.
 *
 * @param frag        Fragment to add
 * @param tag         Fragment tag
 * @param flags       Adds flags to manipulate the state of the backstack
 * @param containerId Container ID where to insert the fragment
 */
fun FragmentManager.addFragment(frag: Fragment,
                                tag: String,
                                animation: FragmentAnimation,
                                flags: Int,
                                containerId: Int) {
    val currentFragment = peek(tag)
    if ((frag as? FraggleFragment)?.isSingleInstance == false || currentFragment == NoFragment) {
        handleClearingMode(flags)
        with(beginTransaction()) {
            handleBackStackAddition(tag, flags)
            addAnimations(animation)
            handleFragmentAdditionMode(frag, peek(), flags, containerId)
            commitAllowingStateLoss()
        }
    } else {
        popBackStack(tag, 0)
        val arguments = frag.arguments
        if (arguments != null && arguments != currentFragment.arguments && currentFragment is FraggleFragment) {
            currentFragment.onNewArgumentsReceived(arguments)
        }
        (currentFragment as? FraggleFragment)?.onFragmentVisible()
    }
}

/**
 * Configures the way to +add the Fragment into the transaction. It can vary from adding a new fragment,
 * to using a previous instance and refresh it, or replacing the last one.
 *
 * @param newFragment        Fragment to add
 * @param flags       Added flags to the Fragment configuration
 * @param ft          Transaction to add the fragment
 * @param containerId Target container ID
 */
internal fun FragmentTransaction.handleFragmentAdditionMode(newFragment: Fragment,
                                                            previousFragment: Fragment,
                                                            flags: Int,
                                                            containerId: Int) {
    if (flags and DO_NOT_REPLACE_FRAGMENT != DO_NOT_REPLACE_FRAGMENT) {
        replace(containerId, newFragment, (newFragment as? FraggleFragment)?.fragmentTag
                ?: newFragment.tag)
    } else {
        add(containerId, newFragment, (newFragment as? FraggleFragment)?.fragmentTag
                ?: newFragment.tag)
        (previousFragment as? FraggleFragment)?.onFragmentNotVisible()
    }
}

/**
 * Process Clear backstack flag.
 *
 *
 * Fraggle will clear the back stack before trying to add the next Fragment if
 * [CLEAR_BACKSTACK][.CLEAR_BACKSTACK] flag is found
 *
 * @param flags Added flags to the Fragment configuration
 */
internal fun FragmentManager.handleClearingMode(flags: Int) {
    if (flags and CLEAR_BACKSTACK == CLEAR_BACKSTACK) {
        try {
            clear()
        } catch (exception: IllegalStateException) {
            Log.e("FragmentManager", exception.message, exception)
        }

    }
}

/**
 * Processes Add to Backstack flag.
 *
 *
 * Will not add the Fragment to the backstack if the
 * [DO_NOT_ADD_TO_BACKSTACK][.DO_NOT_ADD_TO_BACKSTACK] flag is found.
 *
 * @param title Title of the fragment
 * @param flags Added flags to the Fragment configuration
 * @param ft    Transaction to add to backstack from
 */
internal fun FragmentTransaction.handleBackStackAddition(title: String, flags: Int) {
    if (flags and DO_NOT_ADD_TO_BACKSTACK != DO_NOT_ADD_TO_BACKSTACK) {
        addToBackStack(title)
    }
}

/**
 * Processes the custom animations element, adding them as required
 *
 * @param animation Animation object to process
 * @param ft        Fragment transaction to add to the transition
 */
internal fun FragmentTransaction.addAnimations(animation: FragmentAnimation?) {
    animation?.let { anim ->
        if (anim.complete) {
            setCustomAnimations(anim.enterAnim, anim.exitAnim, anim.pushInAnim, anim.popOutAnim)
        } else {
            setCustomAnimations(anim.enterAnim, anim.exitAnim)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            anim.sharedViews.forEach { sharedElement ->
                addSharedElement(sharedElement.view, sharedElement.name)
            }
        }
    }
}

internal fun FragmentManager.addFragment(navigator: Navigator) {
    when (val fragment = navigator.newInstance()) {
        is DialogFragment -> {
            fragment.show(this, fragment.tag)
        }
        is FraggleFragment -> addFragment(fragment,
                fragment.fragmentTag,
                navigator.animation,
                navigator.flags,
                navigator.target)
        else -> addFragment(fragment,
                fragment.tag ?: "",
                navigator.animation,
                navigator.flags,
                navigator.target)
    }
}