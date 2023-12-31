package com.pma.bcc.net

import com.pma.bcc.model.Program
import com.pma.bcc.model.ProgramState
import io.reactivex.Observable
import com.pma.bcc.model.ThermSensor
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Body

interface ServerApi {

    @GET("therm_sensors")
    fun getSensors() : Observable<List<ThermSensor>>

    @GET("programs")
    fun getPrograms() : Observable<List<Program>>

    @POST("programs")
    fun createProgram(@Body program: Program) : Observable<Program>

    @PUT("programs/{id}")
    fun updateProgram(@Path(value="id") programId: String, @Body program: Program) : Observable<Program>

    @DELETE("programs/{id}")
    fun deleteProgram(@Path(value="id") programId: String) : Observable<Program>

    @GET("states")
    fun getProgramStates() : Observable<List<ProgramState>>

    @GET("states/{id}")
    fun getProgramState(@Path(value="id") programId: String) : Observable<ProgramState?>
}