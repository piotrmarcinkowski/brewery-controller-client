package com.pma.bcc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pma.bcc.R
import com.pma.bcc.model.Program
import com.pma.bcc.model.ProgramState
import com.pma.bcc.net.ServerApi
import com.pma.bcc.net.ServerApiFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import mu.KLogging
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val UPDATE_INTERVAL = 2000L

class ProgramDetailsViewModel : BaseViewModel {
    companion object : KLogging()

    private lateinit var programId: String
    private val serverApiFactory: ServerApiFactory
    private val resourceProvider: ResourceProvider
    private var program = MutableLiveData<Program>()
    private var programState = MutableLiveData<ProgramState>()
    private var programIsActiveText = MutableLiveData<String>()
    private var programIsActive = MutableLiveData<Boolean>()
    private var programSensor = MutableLiveData<String>()
    private var programHeatingRelay = MutableLiveData<String>()
    private var programCoolingRelay = MutableLiveData<String>()
    private val updateInProgress = MutableLiveData<Boolean>()
    private val updateError = MutableLiveData<Boolean>()
    private var periodicSubscribeDisposable: Disposable? = null

    @Inject constructor(serverApiFactory: ServerApiFactory, resourceProvider: ResourceProvider) : super()  {
        this.serverApiFactory = serverApiFactory
        this.resourceProvider = resourceProvider
        this.updateInProgress.value = false
    }

    fun setProgram(program: Program) {
        this.programId = program.id
        this.program.value = program
        this.programIsActive.value = program.active
        this.programIsActiveText.value =
            if (program.active) resourceProvider.getString(R.string.program_details_program_active)
            else resourceProvider.getString(R.string.program_details_program_inactive)
        this.programSensor.value = program.sensorId
        this.programHeatingRelay.value =
            if (program.heatingRelayIndex >= 0) program.heatingRelayIndex.toString()
            else resourceProvider.getString(R.string.program_details_heating_relay_not_available)
        this.programCoolingRelay.value =
            if (program.coolingRelayIndex >= 0) program.coolingRelayIndex.toString()
            else resourceProvider.getString(R.string.program_details_cooling_relay_not_available)

        resume()
    }

    fun resume() {
        startUpdatingProgramState()
    }

    fun pause() {
        stopUpdatingProgramState()
    }

    private fun startUpdatingProgramState() {
        startUpdatingProgramState(UPDATE_INTERVAL)
    }

    private fun startUpdatingProgramState(interval: Long) {
        if (periodicSubscribeDisposable != null && !periodicSubscribeDisposable!!.isDisposed) {
            return
        }

        updateError.value = false

        periodicSubscribeDisposable = Observable.interval(0, interval,
            TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ doUpdateProgramState() },
                { error -> logger.warn("startUpdatingProgramState() onError: $error") })
    }

    fun getProgram(): LiveData<Program> {
        return program
    }

    fun getProgramState(): LiveData<ProgramState> {
        return programState
    }

    fun getUpdateInProgress(): LiveData<Boolean> {
        return updateInProgress
    }

    fun getUpdateError(): LiveData<Boolean> {
        return updateError
    }

    fun getProgramIsActive(): LiveData<Boolean> {
        return programIsActive
    }

    fun getProgramIsActiveText(): LiveData<String> {
        return programIsActiveText
    }

    fun getSensor(): LiveData<String> {
        return programSensor
    }

    fun getHeatingRelay(): LiveData<String> {
        return programHeatingRelay
    }

    fun getCoolingRelay(): LiveData<String> {
        return programCoolingRelay
    }

    override fun onCleared() {
        stopUpdatingProgramState()
        super.onCleared()
    }

    private fun doUpdateProgramState() {
        serverApiFactory.create()
            .getProgramState(programId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { state ->
                    logger.warn("getProgramState(): $state")
                    updateInProgress.value = false
                    updateError.value = false
                    programState.value = state
                },
                { error ->
                    logger.warn("getProgramState(): $error")
                    updateInProgress.value = false
                    updateError.value = true
                }
            )
    }

    private fun createServerApi(): ServerApi? {
        try {
            return serverApiFactory.create()
        } catch (e: ServerApiFactory.InvalidConnectionSettingsException) {
            logger.warn("createServerApi() exception: $e")
        }
        return null
    }

    fun stopUpdatingProgramState() {
        periodicSubscribeDisposable?.dispose()
    }


}