package com.pma.bcc.model

import com.google.gson.annotations.SerializedName

data class ProgramState(
    @SerializedName("program_id")
    val programId: String,
    @SerializedName("program_crc")
    val programCrc: String,
    @SerializedName("current_temperature")
    val currentTemp: Double,
    @SerializedName("heating_activated")
    val heatingActivated: Boolean,
    @SerializedName("cooling_activated")
    val coolingActivated: Boolean,
    @SerializedName("error")
    val error: String? = null
)
