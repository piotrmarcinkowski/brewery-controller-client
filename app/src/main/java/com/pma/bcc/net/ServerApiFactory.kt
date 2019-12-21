package com.pma.bcc.net

interface ServerApiFactory {
    class InvalidConnectionSettingsException : Exception()

    @Throws(InvalidConnectionSettingsException::class)
    fun create(): ServerApi

    fun invalidate()
}