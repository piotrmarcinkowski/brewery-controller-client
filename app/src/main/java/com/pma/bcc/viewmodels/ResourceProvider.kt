package com.pma.bcc.viewmodels

interface ResourceProvider {
    fun getString(id: Int): String
}