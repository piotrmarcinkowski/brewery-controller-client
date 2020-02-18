package com.pma.bcc.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.pma.bcc.R
import com.pma.bcc.databinding.ProgramBinding
import com.pma.bcc.viewmodels.ProgramDataViewModel
import kotlinx.android.synthetic.main.program.view.*


class ProgramView : FrameLayout {
    private lateinit var binding: ProgramBinding

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        if (isInEditMode) {
            LayoutInflater.from(context).inflate(R.layout.program, this, true)
        }
        else {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.program, this, true)
        }
    }

    var programDataViewModel : ProgramDataViewModel?
        get() = binding.programData
        set(value) { binding.programData = value }
}