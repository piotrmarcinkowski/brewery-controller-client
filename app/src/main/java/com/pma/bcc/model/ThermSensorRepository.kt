package com.pma.bcc.model

import io.reactivex.Observable
import com.pma.bcc.server.ServerApi
import javax.inject.Singleton

@Singleton
class ThermSensorRepository(private val serverApi : ServerApi) {

    var cachedSensors = emptyList<ThermSensor>()

    fun getThermSensors() : Observable<List<ThermSensor>> {
        if (cachedSensors.isEmpty()) {
            return serverApi.getSensors().doOnNext { cachedSensors = it }
        }
        else {
            return Observable.just(cachedSensors)
                .mergeWith(serverApi.getSensors())
                .doOnNext { cachedSensors = it }
        }
    }
}