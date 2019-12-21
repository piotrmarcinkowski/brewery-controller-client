package com.pma.bcc.view

import android.content.Context
import android.media.Image
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.pma.bcc.R
import kotlinx.android.synthetic.main.program.view.*


class ProgramView : FrameLayout {
    private lateinit var textProgramName : TextView
    private lateinit var textCurrentTemperature : TextView
    private lateinit var progressTemperature : ProgressBar
    private lateinit var textMaxTemperature : TextView
    private lateinit var textMinTemperature : TextView
    private lateinit var imageHeatingActivated : ImageView
    private lateinit var imageCoolingActivated : ImageView
    private lateinit var image_program_disabled : ImageView
    private var pulsingAnimation: Animation? = null

    var programName : String
        get() = text_program_name.text.toString()
        set(value) { text_program_name.text = value }

    var currentTemperature : String
        get() = text_current_temperature.text.toString()
        set(value) { text_current_temperature.text = value }

    var currentTemperatureAvailable : Boolean
        get() = progressTemperature.visibility == View.VISIBLE
        set(value) {
            if (value) {
                textCurrentTemperature.visibility = View.VISIBLE
                progressTemperature.visibility = View.GONE
            }
            else {
                textCurrentTemperature.visibility = View.GONE
                progressTemperature.visibility = View.VISIBLE
            }
        }

    var maxTemperature : String
        get() = text_max_temperature.text.toString()
        set(value) { text_max_temperature.text = value }

    var minTemperature : String
        get() = text_min_temperature.text.toString()
        set(value) { text_min_temperature.text = value }

    var heatingActivated : Boolean
        get() = image_heating_activated_indicator.visibility == View.VISIBLE
        set(value) {
            if (value) {
                image_heating_activated_indicator.visibility = View.VISIBLE
                startPulsingAnimation(imageHeatingActivated)
            }
            else {
                image_heating_activated_indicator.visibility = View.INVISIBLE
                image_heating_activated_indicator.clearAnimation()
            }
        }

    var coolingActivated : Boolean
        get() = image_cooling_activated_indicator.visibility == View.VISIBLE
        set(value) {
            if (value) {
                image_cooling_activated_indicator.visibility = View.VISIBLE
                startPulsingAnimation(imageCoolingActivated)
            } else {
                image_cooling_activated_indicator.visibility = View.INVISIBLE
                image_cooling_activated_indicator.clearAnimation()
            }
        }

    var active : Boolean
        get() = image_program_disabled.visibility == View.VISIBLE
        set(value) { image_program_disabled.visibility = if (value) View.GONE else View.VISIBLE }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.program, this)
        textProgramName = view.text_program_name
        textCurrentTemperature = view.text_current_temperature
        progressTemperature = view.progress_temperature
        textMaxTemperature = view.text_max_temperature
        textMinTemperature = view.text_min_temperature
        imageHeatingActivated = view.image_heating_activated_indicator
        imageCoolingActivated = view.image_cooling_activated_indicator
        image_program_disabled = view.image_program_disabled_indicator

        currentTemperatureAvailable = true
    }

    private fun startPulsingAnimation(view: View) {
        if (pulsingAnimation == null) {
            pulsingAnimation = AnimationUtils.loadAnimation(view.context, R.anim.pulse)
        }
        view.startAnimation(pulsingAnimation)
    }
}