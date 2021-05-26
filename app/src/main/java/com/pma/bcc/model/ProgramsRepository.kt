package com.pma.bcc.model

import com.pma.bcc.net.ServerApiFactory
import io.reactivex.Observable

interface ProgramsRepository {
    @Throws(ServerApiFactory.InvalidConnectionSettingsException::class)
    fun getPrograms(): Observable<List<Program>>
    @Throws(ServerApiFactory.InvalidConnectionSettingsException::class)
    fun getProgramStates(): Observable<Map<String, ProgramState>>
    @Throws(ServerApiFactory.InvalidConnectionSettingsException::class)
    fun getProgramState(programId: String): Observable<ProgramState?>
    @Throws(ServerApiFactory.InvalidConnectionSettingsException::class)
    fun getThermSensors() : Observable<List<ThermSensor>>
    @Throws(ServerApiFactory.InvalidConnectionSettingsException::class)
    fun updateProgram(program: Program) : Observable<Program>
    @Throws(ServerApiFactory.InvalidConnectionSettingsException::class)
    fun createProgram(program: Program) : Observable<Program>
    @Throws(ServerApiFactory.InvalidConnectionSettingsException::class)
    fun deleteProgram(program: Program) : Observable<Void>
}