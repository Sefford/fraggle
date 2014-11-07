package com.sefford.fraggle;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FragmentAnimationTest {

    private static final int EXPECTED_ENTER_ANIMATION = 1;
    private static final int EXPECTED_EXIT_ANIMATION = 2;
    private static final int EXPECTED_PUSH_ANIMATION = 3;
    private static final int EXPECTED_POP_ANIMATION = 4;

    FragmentAnimation animation;

    @Before
    public void setUp() throws Exception {
        animation = new FragmentAnimation(EXPECTED_ENTER_ANIMATION, EXPECTED_EXIT_ANIMATION,
                EXPECTED_PUSH_ANIMATION, EXPECTED_POP_ANIMATION);
    }

    @Test
    public void testGetEnterAnim() throws Exception {
        assertEquals(EXPECTED_ENTER_ANIMATION, animation.getEnterAnim());
    }

    @Test
    public void testGetExitAnim() throws Exception {
        assertEquals(EXPECTED_EXIT_ANIMATION, animation.getExitAnim());
    }

    @Test
    public void testGetPushInAnim() throws Exception {
        assertEquals(EXPECTED_PUSH_ANIMATION, animation.getPushInAnim());
    }

    @Test
    public void testGetPopOutAnim() throws Exception {
        assertEquals(EXPECTED_POP_ANIMATION ,animation.getPopOutAnim());
    }

    @Test
    public void testIsCompletedAnimation() throws Exception {
        assertTrue(animation.isCompletedAnimation());
    }

    @Test
    public void testIsCompletedAnimationNotFull() throws Exception {
        animation = new FragmentAnimation(EXPECTED_ENTER_ANIMATION, EXPECTED_EXIT_ANIMATION);
        assertFalse(animation.isCompletedAnimation());
    }

    @Test
    public void testIsCompletedAnimationNotFullWithPushIn() throws Exception {
        animation = new FragmentAnimation(EXPECTED_ENTER_ANIMATION, EXPECTED_EXIT_ANIMATION, EXPECTED_PUSH_ANIMATION, FragmentAnimation.NO_ANIMATION);
        assertTrue(animation.isCompletedAnimation());
    }

    @Test
    public void testIsCompletedAnimationNotFullWithPopOut() throws Exception {
        animation = new FragmentAnimation(EXPECTED_ENTER_ANIMATION, EXPECTED_EXIT_ANIMATION, FragmentAnimation.NO_ANIMATION, EXPECTED_POP_ANIMATION);
        assertTrue(animation.isCompletedAnimation());
    }
}