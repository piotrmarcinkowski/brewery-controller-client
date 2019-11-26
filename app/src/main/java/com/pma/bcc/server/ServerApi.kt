package com.pma.bcc.server

import io.reactivex.Observable
import com.pma.bcc.model.ThermSensor
import retrofit2.http.GET

interface ServerApi {

    @GET("therm_sensors")
    fun getSensors() : Observable<List<ThermSensor>>
}