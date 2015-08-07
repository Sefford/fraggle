package com.sefford.fraggle;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FragmentAnimationTest {

    static final int EXPECTED_ENTER_ANIMATION = 1;
    static final int EXPECTED_EXIT_ANIMATION = 2;
    static final int EXPECTED_PUSH_ANIMATION = 3;
    static final int EXPECTED_POP_ANIMATION = 4;

    FragmentAnimation animation;

    @Before
    public void setUp() throws Exception {
        animation = new FragmentAnimation(EXPECTED_ENTER_ANIMATION, EXPECTED_EXIT_ANIMATION,
                EXPECTED_PUSH_ANIMATION, EXPECTED_POP_ANIMATION);
    }

    @Test
    public void testGetEnterAnim() throws Exception {
        assertEquals(animation.getEnterAnim(), EXPECTED_ENTER_ANIMATION);
    }

    @Test
    public void testGetExitAnim() throws Exception {
        assertEquals(animation.getExitAnim(), EXPECTED_EXIT_ANIMATION);
    }

    @Test
    public void testGetPushInAnim() throws Exception {
        assertEquals(animation.getPushInAnim(), EXPECTED_PUSH_ANIMATION);
    }

    @Test
    public void testGetPopOutAnim() throws Exception {
        assertEquals(animation.getPopOutAnim(), EXPECTED_POP_ANIMATION);
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
}