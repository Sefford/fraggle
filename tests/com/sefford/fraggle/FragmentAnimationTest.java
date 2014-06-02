package com.sefford.fraggle;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
        assertThat(animation.getEnterAnim(), equalTo(EXPECTED_ENTER_ANIMATION));
    }

    @Test
    public void testGetExitAnim() throws Exception {
        assertThat(animation.getExitAnim(), equalTo(EXPECTED_EXIT_ANIMATION));
    }

    @Test
    public void testGetPushInAnim() throws Exception {
        assertThat(animation.getPushInAnim(), equalTo(EXPECTED_PUSH_ANIMATION));
    }

    @Test
    public void testGetPopOutAnim() throws Exception {
        assertThat(animation.getPopOutAnim(), equalTo(EXPECTED_POP_ANIMATION));
    }

    @Test
    public void testIsCompletedAnimation() throws Exception {
        assertThat(animation.isCompletedAnimation(), equalTo(true));
    }

    @Test
    public void testIsCompletedAnimationNotFull() throws Exception {
        animation = new FragmentAnimation(EXPECTED_ENTER_ANIMATION, EXPECTED_EXIT_ANIMATION);
        assertThat(animation.isCompletedAnimation(), equalTo(false));
    }

    @Test
    public void testIsCompletedAnimationNotFullWithPushIn() throws Exception {
        animation = new FragmentAnimation(EXPECTED_ENTER_ANIMATION, EXPECTED_EXIT_ANIMATION, EXPECTED_PUSH_ANIMATION, FragmentAnimation.NO_ANIMATION);
        assertThat(animation.isCompletedAnimation(), equalTo(true));
    }

    @Test
    public void testIsCompletedAnimationNotFullWithPopOut() throws Exception {
        animation = new FragmentAnimation(EXPECTED_ENTER_ANIMATION, EXPECTED_EXIT_ANIMATION, FragmentAnimation.NO_ANIMATION, EXPECTED_POP_ANIMATION);
        assertThat(animation.isCompletedAnimation(), equalTo(true));
    }
}