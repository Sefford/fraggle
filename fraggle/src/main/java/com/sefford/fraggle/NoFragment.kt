package com.sefford.fraggle

import android.os.Bundle
import androidx.fragment.app.Fragment

import com.sefford.fraggle.interfaces.FraggleFragment

/**
 * Created by sefford on 7/08/15.
 */
object NoFragment : Fragment(), FraggleFragment {

    override val fragmentTag = ""

    override val isEntryFragment = false

    override val isSingleInstance = false

    override val customizedOnBackPressed = false

    override val onBackPressedTarget: String = ""

    override fun onFragmentVisible() {}

    override fun onFragmentNotVisible() {}

    override fun onBackPressed() {}

    override fun onNewArgumentsReceived(arguments: Bundle) {}
}
