package com.pma.bcc.viewmodels

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.pma.bcc.R
import com.pma.bcc.model.ProgramError
import retrofit2.HttpException

class ErrorResponseHandler(private val resourceProvider: ResourceProvider) {
    fun createNotification(baseMessage: String, throwable: Throwable?): Notification {
        var message = baseMessage
        if (throwable != null) {
            try {
                val programError = parseProgramError(throwable)
                if (programError != null) {
                    message += "\n" + createErrorDescription(programError)
                }
            } catch (e: Exception) {
            }
        }
        return Notification(message)
    }

    private fun parseProgramError(throwable: Throwable): ProgramError? {
        if(throwable is HttpException) {
            val body = throwable.response()?.errorBody()
            val gson = Gson()
            val adapter: TypeAdapter<ProgramError> = gson.getAdapter(ProgramError::class.java)
            return adapter.fromJson(body?.string())
        }
        return null
    }

    private fun createErrorDescription(programError: ProgramError): String {
        return when (programError.errorCode) {
            ProgramError.ERROR_CODE_CANNOT_LOAD_PROGRAMS -> resourceProvider.getString(R.string.response_error_invalid_id)
            ProgramError.ERROR_CODE_INVALID_OPERATION -> resourceProvider.getString(R.string.response_error_invalid_operation)
            ProgramError.ERROR_CODE_INVALID_RELAY -> resourceProvider.getString(R.string.response_error_invalid_relay)
            ProgramError.ERROR_CODE_INVALID_SENSOR -> resourceProvider.getString(R.string.response_error_invalid_sensor)
            ProgramError.ERROR_CODE_HEATING_RELAY_ALREADY_IN_USE -> resourceProvider.getString(R.string.response_error_heating_relay_in_use)
            ProgramError.ERROR_CODE_COOLING_RELAY_ALREADY_IN_USE -> resourceProvider.getString(R.string.response_error_cooling_relay_in_use)
            ProgramError.ERROR_CODE_SENSOR_ALREADY_IN_USE -> resourceProvider.getString(R.string.response_error_sensor_in_use)
            ProgramError.ERROR_CODE_MIN_TEMP_HIGHER_THAN_MAX -> resourceProvider.getString(R.string.response_error_min_temp_higher_than_max)
            ProgramError.ERROR_CODE_CANNOT_STORE_PROGRAMS -> resourceProvider.getString(R.string.response_error_store_programs)
            ProgramError.ERROR_CODE_CANNOT_LOAD_PROGRAMS -> resourceProvider.getString(R.string.response_error_load_programs)
            else -> {
                resourceProvider.getString(R.string.response_error_generic, programError.errorCode)
            }
        }
    }
}