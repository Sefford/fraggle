package com.sefford.fraggle

import androidx.fragment.app.FragmentManager

object NullBackStack : FragmentManager.BackStackEntry {
    override fun getId() = 0

    override fun getName() = ""

    override fun getBreadCrumbTitleRes() = 0

    override fun getBreadCrumbShortTitleRes() = 0

    override fun getBreadCrumbTitle() = ""

    override fun getBreadCrumbShortTitle() = ""
}