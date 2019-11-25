package com.sefford.fraggle

import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.BackStackEntry
import androidx.fragment.app.FragmentTransaction
import com.nhaarman.mockitokotlin2.*
import com.sefford.fraggle.interfaces.FraggleFragment
import io.kotlintest.TestCase
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class FraggleExtensionsTest : StringSpec() {

    lateinit var fragmentManager: FragmentManager

    override fun beforeTest(testCase: TestCase) {
        this.fragmentManager = spy(TestFragmentManager())
    }

    init {
        "clear should be properly executed" {
            doNothing().whenever(fragmentManager).popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

            fragmentManager.clear()

            verify(fragmentManager).popBackStack(isNull<String>(), eq(FragmentManager.POP_BACK_STACK_INCLUSIVE))
        }

        "contains returns true when the target fragment exists in the backstack" {
            whenever(fragmentManager.findFragmentByTag(EXPECTED_FRAGMENT_TAG_1)).thenReturn(Fragment())

            fragmentManager.contains(EXPECTED_FRAGMENT_TAG_1).shouldBeTrue()
        }

        "contains returns false when the target fragment does not exist in the backstack" {
            whenever(fragmentManager.findFragmentByTag(EXPECTED_FRAGMENT_TAG_1)).thenReturn(null)

            fragmentManager.contains(EXPECTED_FRAGMENT_TAG_1).shouldBeFalse()
        }

        "currentFragmentHasCustomBack returns true when the current fragment has custom back protocol" {
            val fragment = mock<TestFragment>()
            whenever(fragment.customizedOnBackPressed).thenReturn(true)
            givenAFragment(fragment)

            fragmentManager.currentFragmentHasCustomBack().shouldBeTrue()
        }

        "currentFragmentHasCustomBack returns false when the current fragment does not have custom back protocol" {
            val fragment = mock<TestFragment>()
            whenever(fragment.customizedOnBackPressed).thenReturn(false)
            givenAFragment(fragment)

            fragmentManager.currentFragmentHasCustomBack().shouldBeFalse()
        }

        "executeBack triggers fragment's onBackPressed" {
            val fragment = mock<TestFragment>()
            givenAFragment(fragment)

            fragmentManager.executeBack()

            verify(fragment).onBackPressed()
        }

        "peek retrieves the topmost fragment from the stack" {
            val fragment = mock<TestFragment>()
            givenAFragment(fragment)

            fragmentManager.peek().shouldBe(fragment)
        }

        "peek returns NoFragment if no fragment has been stacked" {
            fragmentManager.peek().shouldBe(NoFragment)
        }

        "peek retrieves the indicated fragment from the stack" {
            val fragment = mock<TestFragment>()
            givenAFragment(fragment)

            fragmentManager.peek(EXPECTED_FRAGMENT_TAG_1).shouldBe(fragment)
        }

        "peek returns NoFragment if fragment cannot be found" {
            fragmentManager.peek(EXPECTED_FRAGMENT_TAG_1).shouldBe(NoFragment)
        }

        "safeGetBackStack returns a backstack if found" {
            val backstack = mock<BackStackEntry>()
            givenABackstack(backstack)

            fragmentManager.safeGetBackStackEntryCount(0).shouldBe(backstack)
        }

        "safeGetBackStack returns an empty backstack if no backstacks are available" {
            fragmentManager.safeGetBackStackEntryCount(0).shouldBe(NullBackStack)
        }

        "popBackStack pops back to the previous fragment if there is no custom back and target and more than one fragment" {
            doReturn(true).whenever(fragmentManager).popBackStackImmediate()

            val firstFragment = mock<TestFragment>()
            whenever(firstFragment.customizedOnBackPressed).thenReturn(false)
            whenever(firstFragment.onBackPressedTarget).thenReturn("")

            val secondFragment = mock<TestFragment>()

            val backstack = mock<BackStackEntry>()
            whenever(backstack.name).thenReturn(EXPECTED_FRAGMENT_TAG_2)

            whenever(fragmentManager.backStackEntryCount).thenReturn(1)

            doReturn(backstack).whenever(fragmentManager).getBackStackEntryAt(0)
            whenever(fragmentManager.findFragmentById(EXPECTED_VIEW_ID)).thenReturn(firstFragment)
            whenever(fragmentManager.findFragmentByTag(EXPECTED_FRAGMENT_TAG_2)).thenReturn(secondFragment)

            fragmentManager.popBackStack(EXPECTED_VIEW_ID)

            verify(fragmentManager).popBackStackImmediate()
        }

        "popBackStack pops back to the selected fragment if there is no custom back and custom target and more than one fragment" {
            doReturn(true).whenever(fragmentManager).popBackStackImmediate(EXPECTED_FRAGMENT_TAG_2, 0)

            val firstFragment = mock<TestFragment>()
            whenever(firstFragment.customizedOnBackPressed).thenReturn(false)
            whenever(firstFragment.onBackPressedTarget).thenReturn(EXPECTED_FRAGMENT_TAG_2)

            val secondFragment = mock<TestFragment>()

            val backstack = mock<BackStackEntry>()
            whenever(backstack.name).thenReturn(EXPECTED_FRAGMENT_TAG_2)

            whenever(fragmentManager.backStackEntryCount).thenReturn(1)

            doReturn(backstack).whenever(fragmentManager).getBackStackEntryAt(0)
            whenever(fragmentManager.findFragmentById(EXPECTED_VIEW_ID)).thenReturn(firstFragment)
            whenever(fragmentManager.findFragmentByTag(EXPECTED_FRAGMENT_TAG_2)).thenReturn(secondFragment)

            fragmentManager.popBackStack(EXPECTED_VIEW_ID)

            verify(fragmentManager).popBackStackImmediate(EXPECTED_FRAGMENT_TAG_2, 0)
        }

        "popBackStack executes onBackPressed of the target fragment with customBack" {
            doReturn(true).whenever(fragmentManager).popBackStackImmediate(EXPECTED_FRAGMENT_TAG_2, 0)

            val fragment = mock<TestFragment>()
            whenever(fragment.customizedOnBackPressed).thenReturn(true)

            whenever(fragmentManager.findFragmentById(EXPECTED_VIEW_ID)).thenReturn(fragment)

            fragmentManager.popBackStack(EXPECTED_VIEW_ID)

            verify(fragment).onBackPressed()
        }

        "popBackStack attempts to refresh visibility of the previous fragment after popping back" {
            doReturn(true).whenever(fragmentManager).popBackStackImmediate(EXPECTED_FRAGMENT_TAG_2, 0)

            val firstFragment = mock<TestFragment>()
            whenever(firstFragment.customizedOnBackPressed).thenReturn(false)
            whenever(firstFragment.onBackPressedTarget).thenReturn(EXPECTED_FRAGMENT_TAG_2)

            val secondFragment = mock<TestFragment>()

            val backstack = mock<BackStackEntry>()
            whenever(backstack.name).thenReturn(EXPECTED_FRAGMENT_TAG_2)

            whenever(fragmentManager.backStackEntryCount).thenReturn(1)

            doReturn(backstack).whenever(fragmentManager).getBackStackEntryAt(0)
            whenever(fragmentManager.findFragmentById(EXPECTED_VIEW_ID)).thenReturn(firstFragment)
            whenever(fragmentManager.findFragmentByTag(EXPECTED_FRAGMENT_TAG_2)).thenReturn(secondFragment)

            fragmentManager.popBackStack(EXPECTED_VIEW_ID)

            verify(secondFragment).onFragmentVisible()
        }

        "popBackNumberOfScreens pops back the requested number of screens"{
            doReturn(true).whenever(fragmentManager).popBackStackImmediate(EXPECTED_FRAGMENT_TAG_1, 0)

            val backstack = mock<BackStackEntry>()
            whenever(backstack.name).thenReturn(EXPECTED_FRAGMENT_TAG_1)
            whenever(fragmentManager.backStackEntryCount).thenReturn(4)
            doReturn(backstack).whenever(fragmentManager).getBackStackEntryAt(2)

            fragmentManager.popBackScreenNumber(2)

            verify(fragmentManager).popBackStackImmediate(EXPECTED_FRAGMENT_TAG_1, 0)
        }

        "popBackNumberOfScreens pops back the maximum number of screens available if asked for more"{
            doReturn(true).whenever(fragmentManager).popBackStackImmediate(EXPECTED_FRAGMENT_TAG_1, 0)

            val backstack = mock<BackStackEntry>()
            whenever(backstack.name).thenReturn(EXPECTED_FRAGMENT_TAG_1)
            whenever(fragmentManager.backStackEntryCount).thenReturn(1)
            doReturn(backstack).whenever(fragmentManager).getBackStackEntryAt(0)

            fragmentManager.popBackScreenNumber(6)

            verify(fragmentManager).popBackStackImmediate(EXPECTED_FRAGMENT_TAG_1, 0)
        }

        "popBackNumberOfScreens does nothing with no screens available"{
            whenever(fragmentManager.backStackEntryCount).thenReturn(0)

            fragmentManager.popBackScreenNumber(10)

            verify(fragmentManager, never()).popBackStackImmediate(EXPECTED_FRAGMENT_TAG_1, 0)
        }

        "reattaches a fragment" {
            val fragment = mock<Fragment>()
            doReturn(fragment).whenever(fragmentManager).peek(EXPECTED_FRAGMENT_TAG_1)
            val transaction = mock<FragmentTransaction>()
            doReturn(transaction).whenever(fragmentManager).beginTransaction()

            fragmentManager.reattach(EXPECTED_FRAGMENT_TAG_1)

            inOrder(transaction, fragment) {
                verify(transaction).detach(fragment)
                verify(transaction).attach(fragment)
                verify(transaction).commit()
            }
        }

        "handleFragmentAdditionMode replaces a fragment by default" {
            val transaction = mock<FragmentTransaction>()
            val fragment = mock<TestFragment>()
            whenever(fragment.fragmentTag).thenReturn(EXPECTED_FRAGMENT_TAG_1)

            transaction.handleFragmentAdditionMode(fragment, mock(), 0, EXPECTED_TARGET_CONTAINER)

            verify(transaction).replace(EXPECTED_TARGET_CONTAINER, fragment, EXPECTED_FRAGMENT_TAG_1)
        }

        "handleFragmentAdditionMode does not replace a fragment when DO_NOT_REPLACE flag is given" {
            val transaction = mock<FragmentTransaction>()
            val newFragment = mock<TestFragment>()
            whenever(newFragment.fragmentTag).thenReturn(EXPECTED_FRAGMENT_TAG_1)
            val previousFragment = mock<TestFragment>()

            transaction.handleFragmentAdditionMode(newFragment, previousFragment, Fraggle.DO_NOT_REPLACE_FRAGMENT, EXPECTED_TARGET_CONTAINER)

            verify(transaction).add(EXPECTED_TARGET_CONTAINER, newFragment, EXPECTED_FRAGMENT_TAG_1)
            verify(previousFragment).onFragmentNotVisible()
        }

        "handleClearingMode clears the stack when the flag CLEAR_BACKSTACK is present" {
            doNothing().whenever(fragmentManager).clear()

            fragmentManager.handleClearingMode(Fraggle.CLEAR_BACKSTACK)

            verify(fragmentManager).clear()
        }

        "handleClearingMode does not clear the stack when the flag CLEAR_BACKSTACK is not present" {
            doNothing().whenever(fragmentManager).clear()

            fragmentManager.handleClearingMode(0)

            verify(fragmentManager, never()).clear()
        }

        "handleBackStackAddition adds the fragment to the backstack if the flag ADD_TO_BACKSTACK is present" {
            val transaction = mock<FragmentTransaction>()

            transaction.handleBackStackAddition(EXPECTED_FRAGMENT_TAG_1, Fraggle.ADD_TO_BACKSTACK)

            verify(transaction).addToBackStack(EXPECTED_FRAGMENT_TAG_1)
        }

        "handleBackStackAddition does not add the fragment to the backstack if the flag DO_NOT_ADD_TO_BACKSTACK is present" {
            val transaction = mock<FragmentTransaction>()

            transaction.handleBackStackAddition(EXPECTED_FRAGMENT_TAG_1, Fraggle.DO_NOT_ADD_TO_BACKSTACK)

            verify(transaction, never()).addToBackStack(EXPECTED_FRAGMENT_TAG_1)
        }

        "addAnimations adds all the animations" {
            val transaction = mock<FragmentTransaction>()
            val targetView = mock<View>()

            transaction.addAnimations(FragmentAnimation(ENTER_ANIM, EXIT_ANIM,
                    PUSH_IN_ANIM, POP_OUT_ANIM,
                    listOf(LollipopAnim(targetView, SHARED_ANIM_NAME))))

            verify(transaction).setCustomAnimations(ENTER_ANIM, EXIT_ANIM, PUSH_IN_ANIM, POP_OUT_ANIM)
        }

        "addAnimations adds partial animations with the minimum fragment animation" {
            val transaction = mock<FragmentTransaction>()

            transaction.addAnimations(FragmentAnimation(ENTER_ANIM, EXIT_ANIM))

            verify(transaction).setCustomAnimations(ENTER_ANIM, EXIT_ANIM)
        }

        "adds a DialogFragment" {
            val navigator = mock<DialogNavigator>()
            val fragment = mock<DialogFragment>()
            whenever(fragment.tag).thenReturn(EXPECTED_FRAGMENT_TAG_1)
            whenever(navigator.newInstance()).thenReturn(fragment)

            fragmentManager.addFragment(navigator)

            verify(fragment).show(fragmentManager, EXPECTED_FRAGMENT_TAG_1)
        }
    }

    private fun givenAFragment(fragment: TestFragment) {
        givenABackstack(mock())
        whenever(fragmentManager.findFragmentByTag(EXPECTED_FRAGMENT_TAG_1)).thenReturn(fragment)
    }

    private fun givenABackstack(backstack: BackStackEntry) {
        whenever(backstack.name).thenReturn(EXPECTED_FRAGMENT_TAG_1)
        whenever(fragmentManager.backStackEntryCount).thenReturn(1)
        doReturn(backstack).whenever(fragmentManager).getBackStackEntryAt(0)
    }

    private class TestFragmentManager : FragmentManager()

    private abstract class TestFragment : Fragment(), FraggleFragment

    companion object {
        private const val EXPECTED_FRAGMENT_TAG_1 = "TIMEY FRAGMENT"
        private const val EXPECTED_FRAGMENT_TAG_2 = "WIMEY FRAGMENT"
        private const val EXPECTED_VIEW_ID = 0
        private const val EXPECTED_TARGET_CONTAINER = 0x1234
        private const val ENTER_ANIM = 1
        private const val EXIT_ANIM = 2
        private const val PUSH_IN_ANIM = 3
        private const val POP_OUT_ANIM = 4
        private const val SHARED_ANIM_NAME = "shared anim"
    }
}