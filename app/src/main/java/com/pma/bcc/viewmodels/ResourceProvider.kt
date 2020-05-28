package com.pma.bcc.viewmodels

interface ResourceProvider {
    fun getString(id: Int): String
    fun getString(id: Int, vararg formatArgs: Any): String
}