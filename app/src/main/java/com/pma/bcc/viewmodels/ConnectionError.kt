package com.pma.bcc.viewmodels

data class ConnectionError(
    val errorId: String,
    val messageTitleResId: Int, val messageResId: Int,
    val retryActionAvailable: Boolean,
    val extraActionAvailable: Boolean,
    val retryActionLabelResId: Int,
    val extraActionLabelResId: Int)