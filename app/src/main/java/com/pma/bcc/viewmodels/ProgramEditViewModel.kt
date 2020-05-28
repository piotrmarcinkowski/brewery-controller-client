package com.pma.bcc.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import com.pma.bcc.R
import com.pma.bcc.model.*
import com.pma.bcc.utils.TemperatureFormatter
import io.reactivex.BackpressureStrategy
import mu.KLogging
import javax.inject.Inject

class ProgramEditViewModel : BaseViewModel {
    companion object : KLogging()

    private val programRepository: ProgramsRepository
    private val resourceProvider: ResourceProvider
    private val appProperties: AppProperties
    val programName = MutableLiveData<String>()
    val maxTemp = MutableLiveData<String>()
    val minTemp = MutableLiveData<String>()
    private var programIsActive = MutableLiveData<Boolean>()
    val selectedSensorPosition = MutableLiveData<Int>()
    val selectedCoolingRelayPosition = MutableLiveData<Int>()
    val selectedHeatingRelayPosition = MutableLiveData<Int>()
    var sensors: LiveData<List<ThermSensor>>? = null
    val relays = MutableLiveData<List<String>>()
    private var programHeatingRelay = MutableLiveData<String>()
    private var programCoolingRelay = MutableLiveData<String>()
    private var programSaveInProgress = MutableLiveData<Boolean>()
    private val programUpdateError = MutableLiveData<String>()

    @Inject constructor(programRepository: ProgramsRepository, resourceProvider: ResourceProvider, appProperties: AppProperties) : super()  {
        this.programRepository = programRepository
        this.resourceProvider = resourceProvider
        this.appProperties = appProperties

        initAvailableSensors()
        initAvailableRelays()
    }

    private fun initAvailableSensors() {
        sensors = LiveDataReactiveStreams.fromPublisher(
            programRepository.getThermSensors().toFlowable(BackpressureStrategy.BUFFER)
        )
    }

    private fun initAvailableRelays() {
        val relays = mutableListOf<String>()
        for (i in 0..appProperties.numberOfRelays) {
            relays.add(resourceProvider.getString(R.string.relay_display_string, i))
        }
        this.relays.value = relays
    }

    fun setProgram(program: Program) {
        programName.value = program.name
        maxTemp.value = TemperatureFormatter.format(program.maxTemp)
        minTemp.value = TemperatureFormatter.format(program.minTemp)

        selectedSensorPosition.value = 1
        selectedCoolingRelayPosition.value = 1
        selectedHeatingRelayPosition.value = 1
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