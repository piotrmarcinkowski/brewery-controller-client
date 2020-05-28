package com.pma.bcc.androidglue

import android.content.Context
import android.content.SharedPreferences
import com.pma.bcc.R
import com.pma.bcc.model.AppProperties
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidAppProperties @Inject constructor(private val context: Context, private val sharedPreferences: SharedPreferences) : AppProperties {

    override val serverAddress: String
        get() = sharedPreferences.getString(
            context.getString(R.string.prefs_key_server_address),
            "")
    override val serverPort: Int
            get() = sharedPreferences.getString(
            context.getString(R.string.prefs_key_server_port),
            context.getString(R.string.prefs_server_port_default)).toInt()
    override val apiBaseUrl: String
        get() = sharedPreferences.getString(
            context.getString(R.string.prefs_key_server_api_url_base),
            context.getString(R.string.prefs_server_api_url_base_default))
    override val numberOfRelays: Int
        get() = sharedPreferences.getString(
            context.getString(R.string.prefs_key_number_of_relays),
            context.getString(R.string.prefs_number_of_relays_default)).toInt()
}