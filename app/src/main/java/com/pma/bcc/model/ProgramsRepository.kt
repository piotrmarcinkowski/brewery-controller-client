package com.pma.bcc.model

import com.pma.bcc.net.ServerApiFactory
import io.reactivex.Observable

interface ProgramsRepository {
    @Throws(ServerApiFactory.InvalidConnectionSettingsException::class)
    fun getPrograms(): Observable<List<Program>>
    @Throws(ServerApiFactory.InvalidConnectionSettingsException::class)
    fun getProgramStates(): Observable<Map<String, ProgramState>>
    @Throws(ServerApiFactory.InvalidConnectionSettingsException::class)
    fun getThermSensors() : Observable<List<ThermSensor>>
}