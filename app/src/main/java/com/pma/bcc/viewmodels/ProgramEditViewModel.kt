package com.pma.bcc.viewmodels

import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import com.pma.bcc.R
import com.pma.bcc.model.*
import com.pma.bcc.utils.TemperatureFormatter
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import mu.KLogging
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ProgramEditViewModel : BaseViewModel {
    companion object : KLogging()

    private val programRepository: ProgramsRepository
    private val resourceProvider: ResourceProvider
    val programName = MutableLiveData<String>()
    val maxTemp = MutableLiveData<String>()
    val minTemp = MutableLiveData<String>()
    private var programIsActive = MutableLiveData<Boolean>()
    val selectedSensorPosition = MutableLiveData<Int>()
    var sensors: LiveData<List<ThermSensor>>
    private var programHeatingRelay = MutableLiveData<String>()
    private var programCoolingRelay = MutableLiveData<String>()
    private var programSaveInProgress = MutableLiveData<Boolean>()
    private val programUpdateError = MutableLiveData<String>()

    @Inject constructor(programRepository: ProgramsRepository, resourceProvider: ResourceProvider) : super()  {
        this.programRepository = programRepository
        this.resourceProvider = resourceProvider

        sensors = LiveDataReactiveStreams.fromPublisher(
            programRepository.getThermSensors().toFlowable(BackpressureStrategy.BUFFER))
    }

    fun setProgram(program: Program) {
        programName.value = program.name
        maxTemp.value = TemperatureFormatter.format(program.maxTemp)
        minTemp.value = TemperatureFormatter.format(program.minTemp)

        selectedSensorPosition.value = 1
        //TODO: Initialize others
    }

    fun getProgramIsActive(): LiveData<Boolean> {
        return programIsActive
    }

    fun getHeatingRelay(): LiveData<String> {
        return programHeatingRelay
    }

    fun getCoolingRelay(): LiveData<String> {
        return programCoolingRelay
    }

    fun getProgramSaveInProgress(): LiveData<Boolean> {
        return programSaveInProgress
    }

    fun toggleProgramActive() {

    }

    fun onClickBack(view: View){
        navigateBack()
    }

    fun onClickSave(view: View){
        logger.info("Save program: '${programName.value}' temp=[${minTemp.value}, ${maxTemp.value}]")
    }

    override fun onCleared() {
        super.onCleared()
    }
}