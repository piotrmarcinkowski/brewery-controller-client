package com.pma.bcc.androidglue

import com.pma.bcc.model.AppProperties

class AndroidAppPropertiesStub : AppProperties {
    override val serverAddress: String
        get() = "127.0.0.1"
    override val serverPort: Int
        get() = 8080
    override val apiBaseUrl: String
        get() = "/brewery/api/1.0"

}