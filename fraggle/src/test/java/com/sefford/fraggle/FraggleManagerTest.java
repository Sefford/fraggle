package com.sefford.fraggle;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.sefford.common.interfaces.Loggable;
import com.sefford.fraggle.interfaces.FraggleFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricGradleTestRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Fever Fragment Manager Tests
 *
 * @author Saul Diaz <sefford@gmail.com>
 */
@RunWith(RobolectricGradleTestRunner.class)
public class FraggleManagerTest {

    private static final String EXPECTED_TITLE = "TIMEY FRAGMENT";
    private static final String EXPECTED_TITLE_2 = "WIMEY FRAGMENT";
    private static final int EXPECTED_ANIMATION = 1;
    private static final int EXPECTED_BACKSTACK_COUNT = 2;
    public static final int EXPECTED_CONTAINER_ID = 0x1234;
    public static final String MOCKED_FRAGMENT_TAG = "MockedFragment";
    private static final String EXPECTED_EXCEPTION_MESSAGE = "ErrorErrorError";
    private FragmentAnimation partialAnimation = new FragmentAnimation(EXPECTED_ANIMATION, EXPECTED_ANIMATION);
    private FragmentAnimation fullAnimation = new FragmentAnimation(EXPECTED_ANIMATION,
            EXPECTED_ANIMATION, EXPECTED_ANIMATION, EXPECTED_ANIMATION);
    @Mock
    FragmentManager fm;
    @Mock
    FragmentTransaction transaction;
    @Mock
    TestFragment newMockedFragment;
    @Mock
    TestFragment mockedFragment;
    @Mock
    FragmentManager.BackStackEntry mockedBackStack1;
    @Mock
    FragmentManager.BackStackEntry mockedBackStack2;
    @Mock
    Loggable log;

    FraggleManager manager;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(fm.beginTransaction()).thenReturn(transaction);
        when(fm.findFragmentByTag(EXPECTED_TITLE)).thenReturn(mockedFragment);
        when(fm.findFragmentByTag(EXPECTED_TITLE_2)).thenReturn(newMockedFragment);
        when(mockedFragment.getFragmentTag()).thenReturn(EXPECTED_TITLE);
        when(newMockedFragment.getFragmentTag()).thenReturn(EXPECTED_TITLE_2);
        manager = spy(new FraggleManager(log));
        manager.initialize(fm);
    }

    @Test
    public void testOnPopBackCustomizedBackPressed() {
        FraggleFragment mockedFragment = mock(TestFragment.class);
        when(fm.findFragmentById(EXPECTED_CONTAINER_ID)).thenReturn((Fragment) mockedFragment);
        when(mockedFragment.customizedOnBackPressed()).thenReturn(Boolean.TRUE);
        when(mockedFragment.onBackPressedTarget()).thenReturn("");
        manager.popBackStack(EXPECTED_CONTAINER_ID);
        verify(mockedFragment, times(1)).onBackPressed();
    }

    @Test
    public void testOnPopBackNotCustomizedBackPressedAndNormalPopBack() {
        FraggleFragment mockedFragment = mock(TestFragment.class);
        when(fm.findFragmentById(EXPECTED_CONTAINER_ID)).thenReturn((Fragment) mockedFragment);
        when(mockedFragment.onBackPressedTarget()).thenReturn("");
        manager.popBackStack(EXPECTED_CONTAINER_ID);
        verify(fm, times(1)).popBackStackImmediate();
    }

    @Test
    public void testOnPopBackNotCustomizedBackPressedAndForcedPopBack() {
        FraggleFragment mockedFragment = mock(TestFragment.class);
        when(mockedFragment.onBackPressedTarget()).thenReturn(MOCKED_FRAGMENT_TAG);
        when(fm.findFragmentById(EXPECTED_CONTAINER_ID)).thenReturn((Fragment) mockedFragment);
        manager.popBackStack(EXPECTED_CONTAINER_ID);
        verify(fm, times(1)).popBackStack(MOCKED_FRAGMENT_TAG, 0);
    }

    @Test
    public void testOnPopBackWithSingleFragment() {
        TestFragment mockedFragment = mock(TestFragment.class);
        FragmentManager.BackStackEntry mockedEntry = mock(FragmentManager.BackStackEntry.class);
        when(fm.findFragmentById(EXPECTED_CONTAINER_ID)).thenReturn(mockedFragment);
        when(fm.getBackStackEntryCount()).thenReturn(2);
        when(fm.getBackStackEntryAt(1)).thenReturn(mockedEntry);
        when(mockedEntry.getName()).thenReturn(EXPECTED_TITLE);
        when(fm.findFragmentByTag(EXPECTED_TITLE)).thenReturn(mockedFragment);
        when(mockedFragment.onBackPressedTarget()).thenReturn("");
        manager.popBackStack(EXPECTED_CONTAINER_ID);
        verify(fm, times(1)).popBackStackImmediate();
        verify(mockedFragment, times(1)).onFragmentVisible();
    }


    @Test
    public void testGetBackstackEntryCount() throws Exception {
        when(fm.getBackStackEntryCount()).thenReturn(EXPECTED_BACKSTACK_COUNT);
        assertEquals(manager.getBackStackEntryCount(), EXPECTED_BACKSTACK_COUNT);
    }

    @Test
    public void testProcessClearBackstackNoClearBackstack() throws Exception {
        manager.processClearBackstack(FraggleManager.ADD_TO_BACKSTACK);
        verify(fm, times(0)).popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Test
    public void testProcessClearBackstackClearBackstack() throws Exception {
        manager.processClearBackstack(FraggleManager.CLEAR_BACKSTACK);
        verify(fm, times(1)).popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Test
    public void testProcessClearBackstackThrowsException() throws Exception {
        IllegalStateException mockedException = mock(IllegalStateException.class);
        when(mockedException.getMessage()).thenReturn(EXPECTED_EXCEPTION_MESSAGE);
        doThrow(mockedException).when(fm).popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.processClearBackstack(FraggleManager.CLEAR_BACKSTACK);
        verify(log, times(1)).e(FraggleManager.TAG, EXPECTED_EXCEPTION_MESSAGE, mockedException);
    }

    @Test
    public void testProcessAddToBackstackDoNotAddToBackstack() throws Exception {
        manager.processAddToBackstackFlag(EXPECTED_TITLE, FraggleManager.DO_NOT_ADD_TO_BACKSTACK, transaction);
        verify(transaction, times(0)).addToBackStack(EXPECTED_TITLE);
    }

    @Test
    public void testProcessAddToBackstackAddToBackstack() throws Exception {
        manager.processAddToBackstackFlag(EXPECTED_TITLE, FraggleManager.ADD_TO_BACKSTACK, transaction);
        verify(transaction, times(1)).addToBackStack(EXPECTED_TITLE);
    }

    @Test
    public void testConfigureAnimationsWithNullAnimations() throws Exception {
        manager.processAnimations(null, transaction);
        verify(transaction, times(0)).setCustomAnimations(anyInt(), anyInt());
        verify(transaction, times(0)).setCustomAnimations(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    public void testConfigureAnimationsWithShortAnimations() throws Exception {
        manager.processAnimations(new FragmentAnimation(EXPECTED_ANIMATION, EXPECTED_ANIMATION), transaction);
        verify(transaction, times(1)).setCustomAnimations(EXPECTED_ANIMATION, EXPECTED_ANIMATION);
        verify(transaction, times(0)).setCustomAnimations(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    public void testConfigureAnimationsWithFullAnimations() throws Exception {
        manager.processAnimations(new FragmentAnimation(EXPECTED_ANIMATION, EXPECTED_ANIMATION, EXPECTED_ANIMATION, EXPECTED_ANIMATION), transaction);
        verify(transaction, times(0)).setCustomAnimations(anyInt(), anyInt());
        verify(transaction, times(1)).setCustomAnimations(EXPECTED_ANIMATION, EXPECTED_ANIMATION, EXPECTED_ANIMATION, EXPECTED_ANIMATION);
    }

    @Test
    public void testConfigureAdditionModeReplace() throws Exception {
        TestFragment frag = new TestFragment();
        manager.configureAdditionMode(frag, FraggleManager.CLEAR_BACKSTACK, transaction, EXPECTED_CONTAINER_ID);
        verify(transaction, times(1)).replace(EXPECTED_CONTAINER_ID, frag, frag.getFragmentTag());
    }

    @Test
    public void testConfigureAdditionModeAdd() throws Exception {
        TestFragment frag = new TestFragment();
        doReturn(frag).when(manager).peek();
        manager.configureAdditionMode(frag, FraggleManager.DO_NOT_REPLACE_FRAGMENT, transaction, EXPECTED_CONTAINER_ID);
        verify(transaction, times(1)).add(EXPECTED_CONTAINER_ID, frag, frag.getFragmentTag());
    }

    @Test
    public void testPerformTransactionAddNewFragment() throws Exception {
        TestFragment frag = mock(TestFragment.class);
        when(frag.isSingleInstance()).thenReturn(true);
        doReturn(frag).when(manager).peek();
        manager.performTransaction(frag, FraggleManager.DO_NOT_REPLACE_FRAGMENT, transaction, EXPECTED_CONTAINER_ID);
        verify(transaction, times(1)).commitAllowingStateLoss();
    }

    @Test
    public void testPerformTransactionPopBack() throws Exception {
        TestFragment frag = mock(TestFragment.class);
        when(frag.isSingleInstance()).thenReturn(false);
        when(frag.getFragmentTag()).thenReturn(EXPECTED_TITLE);
        when(fm.getBackStackEntryCount()).thenReturn(1);
        FragmentManager.BackStackEntry mockBackStack = mock(FragmentManager.BackStackEntry.class);
        when(fm.getBackStackEntryAt(0)).thenReturn(mockBackStack);
        when(mockBackStack.getName()).thenReturn(EXPECTED_TITLE);
        doReturn(frag).when(manager).peek();
        manager.performTransaction(frag, FraggleManager.DO_NOT_REPLACE_FRAGMENT, transaction, EXPECTED_CONTAINER_ID);
        verify(fm, times(0)).popBackStack(frag.getFragmentTag(), 0);
    }

    @Test
    public void testPerformAddFragmentWithPopBack() throws Exception {
        TestFragment frag = mock(TestFragment.class);
        when(frag.isSingleInstance()).thenReturn(true);
        when(frag.getFragmentTag()).thenReturn(MOCKED_FRAGMENT_TAG);

        FragmentManager.BackStackEntry mockBackStack = mock(FragmentManager.BackStackEntry.class);
        when(fm.getBackStackEntryCount()).thenReturn(1);
        when(fm.getBackStackEntryAt(0)).thenReturn(mockBackStack);
        when(fm.findFragmentByTag(MOCKED_FRAGMENT_TAG)).thenReturn(mock(TestFragment.class));
        when(mockBackStack.getName()).thenReturn(MOCKED_FRAGMENT_TAG);

        manager.addFragment(frag, frag.getFragmentTag(), null, FraggleManager.DO_NOT_REPLACE_FRAGMENT, EXPECTED_CONTAINER_ID);
        verify(fm, times(1)).popBackStack(frag.getFragmentTag(), 0);
    }

    @Test
    public void testAddFragmentWithNullFragment() throws Exception {
        manager.addFragment(null, TestFragment.TAG, null, 0, EXPECTED_CONTAINER_ID);
        verify(manager, times(0)).processClearBackstack(anyInt());
        // Might try the rest, but is unnecessary
    }

    @Test
    public void testAddFragmentWithFragment() throws Exception {
        TestFragment frag = mock(TestFragment.class);
        when(frag.isSingleInstance()).thenReturn(false);
        manager.addFragment(frag, TestFragment.TAG, null, 0, EXPECTED_CONTAINER_ID);
        verify(manager, times(1)).processClearBackstack(anyInt());
        // Might try the rest, but is unnecessary
    }

    @Test
    public void testPeek() throws Exception {
        final FragmentManager.BackStackEntry mockedEntry = mock(FragmentManager.BackStackEntry.class);
        final FraggleFragment fragment = new TestFragment();
        when(fm.getBackStackEntryCount()).thenReturn(1);
        when(mockedEntry.getName()).thenReturn(TestFragment.TAG);
        when(fm.getBackStackEntryAt(0)).thenReturn(mockedEntry);
        when(fm.findFragmentByTag(TestFragment.TAG)).thenReturn((Fragment) fragment);

        assertThat(manager.peek(), equalTo(fragment));
    }

    @Test
    public void testCalculateCorrectAdditionModeNoFragments() throws Exception {
        when(fm.getBackStackEntryCount()).thenReturn(0);
        assertThat(manager.calculateCorrectMode(), equalTo(0));
    }

    @Test
    public void testCalculateCorrectAdditionModeFragments() throws Exception {
        when(fm.getBackStackEntryCount()).thenReturn(1);
        assertThat(manager.calculateCorrectMode(), equalTo(FraggleManager.DO_NOT_REPLACE_FRAGMENT));
    }

    @Test
    public void testPeekTag() throws Exception {
        when(fm.findFragmentByTag(EXPECTED_TITLE)).thenReturn(mockedFragment);
        assertThat(manager.peek(EXPECTED_TITLE), equalTo(((FraggleFragment) mockedFragment)));
    }

    class TestFragment extends Fragment implements FraggleFragment {

        public static final String TAG = "TestFragmentTag";

        @Override
        public String getFragmentTag() {
            return TAG;
        }

        @Override
        public boolean isEntryFragment() {
            return false;
        }

        @Override
        public boolean isSingleInstance() {
            return false;
        }

        @Override
        public void onFragmentVisible() {

        }

        @Override
        public void onFragmentNotVisible() {

        }

        @Override
        public boolean customizedOnBackPressed() {
            return false;
        }

        @Override
        public String onBackPressedTarget() {
            return null;
        }

        @Override
        public void onNewArgumentsReceived(Bundle arguments) {

        }

        @Override
        public void onBackPressed() {

        }
    }
}