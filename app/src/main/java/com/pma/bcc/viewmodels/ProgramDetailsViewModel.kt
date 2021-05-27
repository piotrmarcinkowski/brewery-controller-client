package com.pma.bcc.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pma.bcc.R
import com.pma.bcc.model.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import mu.KLogging
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val UPDATE_INTERVAL = 4000L

class ProgramDetailsViewModel : BaseViewModel {
    companion object : KLogging()

    private lateinit var programId: String
    private val programRepository: ProgramsRepository
    private val resourceProvider: ResourceProvider
    private var program = MutableLiveData<Program>()
    private var programState = MutableLiveData<ProgramState>()
    private var programIsActiveText = MutableLiveData<String>()
    private var programIsActive = MutableLiveData<Boolean>()
    private val programIsActiveUpdating = MutableLiveData<Boolean>()
    private var programSensor = MutableLiveData<String>()
    private var programHeatingRelay = MutableLiveData<String>()
    private var programCoolingRelay = MutableLiveData<String>()
    private val programUpdateError = MutableLiveData<String>()
    private var periodicSubscribeDisposable: Disposable? = null

    @Inject constructor(programRepository: ProgramsRepository, resourceProvider: ResourceProvider) : super()  {
        this.programRepository = programRepository
        this.resourceProvider = resourceProvider
    }

    fun getProgram(): LiveData<Program> {
        return program
    }

    fun getProgramState(): LiveData<ProgramState> {
        return programState
    }

    fun getProgramIsActive(): LiveData<Boolean> {
        return programIsActive
    }

    fun getProgramIsActiveText(): LiveData<String> {
        return programIsActiveText
    }

    fun getProgramIsActiveUpdating(): LiveData<Boolean> {
        return programIsActiveUpdating
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

    fun setProgram(program: Program) {
        onProgramUpdated(program)
        resume()
    }

    private fun onProgramUpdated(program: Program) {
        logger.info("onProgramUpdated: $program")
        this.programId = program.id
        this.program.value = program
        refreshViews()
    }

    private fun refreshViews() {
        val program = this.program.value
        if (program != null) {
            refreshProgramIsActiveView(program)
            refreshSensorView(program)
            refreshRelaysView(program)
        }
    }

    private fun onProgramUpdateError(program: Program, error: Throwable) {
        logger.info("onProgramUpdateError program: $program error: $error")
        // TODO add displaying toast here
        refreshViews()
    }

    private fun refreshProgramIsActiveView(program: Program) {
        this.programIsActive.value = program.active
        this.programIsActiveText.value =
            if (program.active) resourceProvider.getString(R.string.program_details_program_active)
            else resourceProvider.getString(R.string.program_details_program_inactive)
        this.programIsActiveUpdating.value = false
    }

    private fun refreshSensorView(program: Program) {
        this.programSensor.value = program.sensorId
    }

    private fun refreshRelaysView(program: Program) {
        this.programHeatingRelay.value =
            if (program.heatingRelayIndex >= 0) program.heatingRelayIndex.toString()
            else resourceProvider.getString(R.string.program_details_heating_relay_not_available)
        this.programCoolingRelay.value =
            if (program.coolingRelayIndex >= 0) program.coolingRelayIndex.toString()
            else resourceProvider.getString(R.string.program_details_cooling_relay_not_available)
    }

    fun toggleProgramActive() {
        logger.info("toggleProgramActive")
        val currentProgram = program.value
        if (currentProgram != null) {
            programIsActiveUpdating.value = true
            val newActive = !currentProgram.active
            logger.info("toggleProgramActive program:${currentProgram.id} activated:$newActive")
            val updatedProgram = Program.Builder(currentProgram).active(newActive).build()
            programRepository.updateProgram(updatedProgram)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {program -> onProgramUpdated(program)},
                    {error -> onProgramUpdateError(updatedProgram, error)}
                )
        }
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

        periodicSubscribeDisposable = Observable.interval(0, interval,
            TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ loadProgramState() },
                { error -> logger.warn("startUpdatingProgramState() onError: $error") })
    }

    private fun loadProgramState() {
        programRepository
            .getProgramState(programId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { state ->
                    logger.info("loadProgramState(): $state")
                    programState.value = state
                },
                { error ->
                    logger.warn("loadProgramState(): $error")
                    programState.value = null
                }
            )
    }

    private fun stopUpdatingProgramState() {
        periodicSubscribeDisposable?.dispose()
    }

    fun onClickBack(view: View){
        navigateBack()
    }

    fun onClickEdit(view: View){
        navigateTo(NavigationTarget(TargetId.ProgramEdit)
            .addArg(TargetArgumentKey.ProgramEditProgram, program.value!!))
    }

    fun onClickDeleteProgram() {
        val currentProgram = program.value
        if (currentProgram != null) {
            programRepository
                .deleteProgram(currentProgram)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        logger.info("Program deleted: $currentProgram")
                        showNotification(Notification(resourceProvider.getString(R.string.program_details_program_deleted_successfully)))
                        navigateBack()
                    },
                    { error ->
                        logger.warn("Error while deleting program: ${error.message}")
                        var errorHandler = ErrorResponseHandler(resourceProvider)
                        showNotification(errorHandler.createNotification(resourceProvider.getString(R.string.program_details_program_update_error), error))
                    }
                )
        }
    }
}