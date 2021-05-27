package com.pma.bcc.model

import com.pma.bcc.net.ServerApiFactory
import io.reactivex.Observable
import javax.inject.Singleton

@Singleton
open class ProgramsRepositoryImpl(private val serverApiFactory : ServerApiFactory) :
    ProgramsRepository {
    private var cachedSensors = emptyList<ThermSensor>()
    private var cachedPrograms = emptyList<Program>()
    private var cachedProgramStates: HashMap<String, ProgramState>? = null

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

    override fun updateProgram(program: Program): Observable<Program> {
        return serverApiFactory.create()
            .updateProgram(program.id, program)
            .doOnNext {
                cachedPrograms = cachedPrograms.map { if (it.id == program.id) program else it }
            }
    }

    override fun createProgram(program: Program): Observable<Program> {
        return serverApiFactory.create()
            .createProgram(program)
            .doOnNext {
                cachedPrograms = cachedPrograms.plus(it)
            }
    }

    override fun deleteProgram(program: Program): Observable<Program> {
        return serverApiFactory.create()
            .deleteProgram(program.id)
            .doOnNext {
                cachedPrograms = cachedPrograms.minus(program)
            }
    }

    override fun getProgramStates(): Observable<Map<String, ProgramState>> {
        if (cachedProgramStates == null) {
            return fetchProgramStates()
                .doOnNext {
                    cachedProgramStates = HashMap()
                    cachedProgramStates!!.putAll(it)}
        }
        else {
            return Observable.just(cachedProgramStates as Map<String, ProgramState>)
                .mergeWith(fetchProgramStates())
                .doOnNext {
                    val hashMap = cachedProgramStates as HashMap<String, ProgramState>
                    hashMap.putAll(it)}
        }
    }

    private fun fetchProgramStates(): Observable<Map<String, ProgramState>> {
        return serverApiFactory.create()
            .getProgramStates()
            .flatMapIterable { it }
            .toMap({ it.programId }, { it })
            .toObservable()
    }

    override fun getProgramState(programId: String): Observable<ProgramState?> {
        if (cachedProgramStates == null) {
            return serverApiFactory.create()
                .getProgramState(programId)
        }
        else {
            return Observable.just(cachedProgramStates!![programId])
                .mergeWith(serverApiFactory.create().getProgramState(programId))
                .doOnNext {
                    if (it != null) cachedProgramStates!![programId] = it
                }
        }
    }
}