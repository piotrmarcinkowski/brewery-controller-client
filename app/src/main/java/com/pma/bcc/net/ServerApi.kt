package com.pma.bcc.net

import com.pma.bcc.model.Program
import com.pma.bcc.model.ProgramState
import io.reactivex.Observable
import com.pma.bcc.model.ThermSensor
import retrofit2.http.GET
import retrofit2.http.Path

interface ServerApi {

    @GET("therm_sensors")
    fun getSensors() : Observable<List<ThermSensor>>

    @GET("programs")
    fun getPrograms() : Observable<List<Program>>

    @GET("programs/states")
    fun getProgramStates() : Observable<List<ProgramState>>

    @GET("programs/states/{id}")
    fun getProgramState(@Path(value="id") programId: String) : Observable<ProgramState>
}