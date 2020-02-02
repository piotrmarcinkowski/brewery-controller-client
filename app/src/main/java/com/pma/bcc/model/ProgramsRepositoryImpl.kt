package com.pma.bcc.model

import com.pma.bcc.net.ServerApiFactory
import io.reactivex.Observable
import javax.inject.Singleton

@Singleton
open class ProgramsRepositoryImpl(private val serverApiFactory : ServerApiFactory) :
    ProgramsRepository {
    private var cachedSensors = emptyList<ThermSensor>()
    private var cachedPrograms = emptyList<Program>()
    private var cachedProgramStates = emptyMap<String, ProgramState>()

    override fun getThermSensors() : Observable<List<ThermSensor>> {
        if (cachedSensors.isEmpty()) {
            return serverApiFactory.create().getSensors().doOnNext { cachedSensors = it }
        }
        else {
            return Observable.just(cachedSensors)
                .mergeWith(serverApiFactory.create().getSensors())
                .doOnNext { cachedSensors = it }
        }
    }

    override fun getPrograms(): Observable<List<Program>> {
        if (cachedPrograms.isEmpty()) {
            return serverApiFactory.create()
                .getPrograms()
                .doOnNext { cachedPrograms = it }
        }
        else {
            return Observable.just(cachedPrograms)
                .mergeWith(serverApiFactory.create().getPrograms())
                .doOnNext { cachedPrograms = it }
        }
    }

    override fun getProgramStates(): Observable<Map<String, ProgramState>> {
        if (cachedProgramStates.isEmpty()) {
            return serverApiFactory.create()
                .getProgramStates()
                .flatMapIterable { it }
                .toMap({ it.programId }, { it })
                .toObservable()
                .doOnNext { cachedProgramStates = it }
        }
        else {
            return Observable.just(cachedProgramStates)
                .mergeWith(serverApiFactory.create().getProgramStates()
                .flatMapIterable { it }
                .toMap({ it.programId }, { it })
                .toObservable())
                .doOnNext { cachedProgramStates = it }
        }
    }
}