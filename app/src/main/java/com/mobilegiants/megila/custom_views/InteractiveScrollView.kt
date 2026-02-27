package com.mobilegiants.megila.custom_views

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

class InteractiveScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ScrollView(context, attrs, defStyle) {

    var onBottomReached: (() -> Unit)? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        val view = getChildAt(childCount - 1)
        val diff = view.bottom - (height + scrollY)
        if (diff == 0) {
            onBottomReached?.invoke()
        }
        super.onScrollChanged(l, t, oldl, oldt)
    }
}
