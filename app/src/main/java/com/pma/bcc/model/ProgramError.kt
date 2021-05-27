package com.pma.bcc.model

import com.google.gson.annotations.SerializedName

data class ProgramError(
    @SerializedName("error_code")
    val errorCode: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("program")
    val program: Program?
) {
    companion object {
        const val ERROR_CODE_INVALID_ID = "invalid_id"
        const val ERROR_CODE_INVALID_OPERATION = "invalid_operation"
        const val ERROR_CODE_INVALID_RELAY = "invalid_relay"
        const val ERROR_CODE_INVALID_SENSOR = "invalid_sensor"
        const val ERROR_CODE_HEATING_RELAY_ALREADY_IN_USE = "heating_relay_already_in_use"
        const val ERROR_CODE_COOLING_RELAY_ALREADY_IN_USE = "cooling_relay_already_in_use"
        const val ERROR_CODE_SENSOR_ALREADY_IN_USE = "sensor_already_in_use"
        const val ERROR_CODE_MIN_TEMP_HIGHER_THAN_MAX = "min_temp_higher_than_max"
        const val ERROR_CODE_CANNOT_STORE_PROGRAMS = "cannot_store_programs"
        const val ERROR_CODE_CANNOT_LOAD_PROGRAMS = "cannot_load_programs"
    }
}
