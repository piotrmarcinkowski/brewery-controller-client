package com.pma.bcc.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.pma.bcc.R

class RelayView : ImageView {

    private var pulsingAnimation: Animation? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {

    }

    override fun setVisibility(visibility: Int) {
        val currentVisibility = getVisibility()
        super.setVisibility(visibility)
        if (currentVisibility != getVisibility()) {
            onVisibilityChanged(visibility)
        }
    }

    private fun onVisibilityChanged(visibility: Int) {
        when(visibility) {
            View.VISIBLE -> startPulsingAnimation()
            View.INVISIBLE, View.GONE -> {
                clearAnimation()
                pulsingAnimation = null
            }
        }
    }

    private fun startPulsingAnimation() {
        if (pulsingAnimation == null) {
            pulsingAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse)
        }
        startAnimation(pulsingAnimation)
    }
}