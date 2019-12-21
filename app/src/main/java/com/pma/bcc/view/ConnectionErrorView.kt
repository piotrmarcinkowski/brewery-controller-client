package com.pma.bcc.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.pma.bcc.R
import com.pma.bcc.viewmodels.ConnectionError
import kotlinx.android.synthetic.main.connection_error.view.*

class ConnectionErrorView : FrameLayout {
    var buttonClickListener: ButtonsClickListener? = null
    private lateinit var view: View

    interface ButtonsClickListener {
        fun onRetry()
        fun onExtraAction()
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        val view = View.inflate(context, R.layout.connection_error, this)
        this.view = view.view_connection_error_container
        setConnectionError(null)
    }

    fun setConnectionError(error: ConnectionError?) {
        view.view_connection_error_container.visibility = View.GONE
        view.view_connection_error_button_extra.visibility = View.GONE
        view.view_connection_error_button_extra.setOnClickListener(null)
        view.view_connection_error_button_retry.visibility = View.GONE
        view.view_connection_error_button_retry.setOnClickListener(null)

        if (error != null) {
            view.view_connection_error_container.visibility = View.VISIBLE
            view.view_connection_error_text_title.text = resources.getText(error.messageTitleResId)
            view.view_connection_error_text_message.text = resources.getText(error.messageResId)
            if (error.extraActionAvailable) {
                view.view_connection_error_button_extra.visibility = View.VISIBLE
                view.view_connection_error_button_extra.setOnClickListener {
                    buttonClickListener?.onExtraAction()
                }
            }
            if (error.retryActionAvailable) {
                view.view_connection_error_button_retry.visibility = View.VISIBLE
                view.view_connection_error_button_retry.setOnClickListener {
                    buttonClickListener?.onRetry()
                }
            }
        }
    }
}