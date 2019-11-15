package com.sefford.fraggle

import android.os.Bundle
import androidx.fragment.app.Fragment

interface Navigator {

    val animation: FragmentAnimation

    val flags: Int

    val target: Int

    val arguments: Bundle

    val fragment: Fragment

    fun newInstance(): Fragment {
        val fragment = this.fragment
        fragment.arguments = arguments
        return fragment
    }

    companion object {

        val NONE: Navigator = object : Navigator {
            override val fragment: Fragment = Fragment()

            override val animation: FragmentAnimation
                get() = NO_ANIMATION

            override val flags = 0

            override val target: Int = 0

            override val arguments: Bundle = Bundle.EMPTY

        }

        val NO_ANIMATION = FragmentAnimation(FragmentAnimation.NO_ANIMATION,
                FragmentAnimation.NO_ANIMATION,
                FragmentAnimation.NO_ANIMATION,
                FragmentAnimation.NO_ANIMATION)

        val FADE_TRANSITION = FragmentAnimation(android.R.anim.fade_in,
                FragmentAnimation.NO_ANIMATION,
                FragmentAnimation.NO_ANIMATION,
                android.R.anim.fade_out)

        val FULL_FADE_TRANSITION = FragmentAnimation(android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out)
    }
}