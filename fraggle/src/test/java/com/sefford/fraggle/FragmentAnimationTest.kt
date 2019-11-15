package com.sefford.fraggle

import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.StringSpec

class FragmentAnimationTest : StringSpec({
    "FragmentAnimation.complete should return true if all the animations are in place" {
        FragmentAnimation(R.anim.fragment_close_enter,
                R.anim.fragment_close_exit,
                R.anim.fragment_fade_enter,
                R.anim.fragment_fade_enter)
                .complete
                .shouldBeTrue()
    }

    "FragmentAnimation.complete should return false if all the animations are in place" {
        FragmentAnimation(R.anim.fragment_close_enter, R.anim.fragment_close_exit).complete.shouldBeFalse()
    }
})