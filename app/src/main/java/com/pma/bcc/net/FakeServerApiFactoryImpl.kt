package com.pma.bcc.net

import com.pma.bcc.model.AppProperties
import com.pma.bcc.model.Program
import com.pma.bcc.model.ProgramState
import com.pma.bcc.model.ThermSensor
import io.reactivex.Observable
import io.reactivex.Observer
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

class FakeServerApiFactoryImpl : ServerApiFactory {

    private var times: Int = 0
    private var appProperties: AppProperties

    @Inject constructor (appProperties: AppProperties) : super() {
        this.appProperties = appProperties
    }

    override fun create(): ServerApi {
        if (times == 0) {
            times++
            //throw ServerApiFactory.InvalidConnectionSettingsException()
        }

        return object: ServerApi {
            override fun getSensors(): Observable<List<ThermSensor>> {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getPrograms(): Observable<List<Program>> {
                if (times == 1) {
                    times++
//                    return object: Observable<List<Program>>() {
//                        override fun subscribeActual(observer: Observer<in List<Program>>?) {
//                            observer?.onError(IOException())
//                        }
//                    }
                }
                return Observable.just(fakePrograms())
            }

            override fun getProgramStates(): Observable<List<ProgramState>> {
                return Observable.just(fakeProgramStates())
            }

            override fun getProgramState(programId: String): Observable<ProgramState> {
                return Observable.just(fakeProgramStates().first { state -> state.programId == programId })
            }
        }

//        return Retrofit.Builder()
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
//            .baseUrl("http://${appProperties.serverAddress}:${appProperties.serverPort}/${appProperties.apiBaseUrl}")
//            .build().create(ServerApi::class.java)
    }

    override fun invalidate() {

    }

    private fun fakePrograms() : List<Program> {
        val programs = ArrayList<Program>()
        programs.add(Program.Builder(null).id("1").name("Aipa Cascade Max").active(true).sensorId("fakeSensor1").coolingRelayIndex(1).build())
        programs.add(Program.Builder(null).id("2").name("Apa").active(true).build())
        programs.add(Program.Builder(null).id("3").name("Lager 1").active(true).build())
        programs.add(Program.Builder(null).id("4").name("Lager 2").active(true).build())
        programs.add(Program.Builder(null).id("5").name("Pils").active(false).build())
        programs.add(Program.Builder(null).id("6").name("Pils 2 Very very very very long name").active(false).build())
        return programs
    }

    private fun fakeProgramStates() : List<ProgramState> {
        val data = ArrayList<ProgramState>()
        data.add(ProgramState("1", "", 18.6, true, false))
        data.add(ProgramState("2", "", 18.2, false, false))
        data.add(ProgramState("3", "", 19.2, false, false))
        return data
    }
}
