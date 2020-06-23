package com.pma.bcc.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.pma.bcc.R
import com.pma.bcc.model.*
import com.pma.bcc.utils.TemperatureFormatter
import io.reactivex.BackpressureStrategy
import io.reactivex.schedulers.Schedulers
import mu.KLogging
import javax.inject.Inject

class ProgramEditViewModel : BaseViewModel {
    companion object : KLogging() {
        class Relay(private val relayIndex : Int, private val resourceProvider: ResourceProvider) {
            override fun toString(): String {
                return when (relayIndex) {
                    Program.NO_RELAY -> resourceProvider.getString(R.string.program_edit_no_relay_used)
                    else -> resourceProvider.getString(R.string.program_edit_relay_display_string, relayIndex)
                }
            }
        }
    }

    private val programRepository: ProgramsRepository
    private val resourceProvider: ResourceProvider
    private val appProperties: AppProperties
    private lateinit var program : Program
    val programName = MutableLiveData<String>()
    val maxTemp = MutableLiveData<String>()
    val minTemp = MutableLiveData<String>()
    val programIsActive = MutableLiveData<Boolean>(true)
    val selectedSensorPosition = MutableLiveData<Int>(0)
    val selectedCoolingRelayPosition = MutableLiveData<Int>(0)
    val selectedHeatingRelayPosition = MutableLiveData<Int>(0)
    val sensorsLoaded = MutableLiveData<Boolean>()
    val sensors = MediatorLiveData<List<ThermSensor>>()
    val relays = MutableLiveData<List<Relay>>()
    val programHeatingRelay = MutableLiveData<String>()
    val programCoolingRelay = MutableLiveData<String>()
    private val programSaveInProgress = MutableLiveData<Boolean>()
    private val programUpdateError = MutableLiveData<String>()

    @Inject constructor(programRepository: ProgramsRepository, resourceProvider: ResourceProvider, appProperties: AppProperties) : super()  {
        this.programRepository = programRepository
        this.resourceProvider = resourceProvider
        this.appProperties = appProperties

        initAvailableSensors()
        initAvailableRelays()
    }

    private fun initAvailableSensors() {
        logger.debug("Loading sensors")
        val source = LiveDataReactiveStreams.fromPublisher(
            programRepository.getThermSensors().toFlowable(BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
        )
        sensors.addSource(source) { sensorList ->
            run {
                logger.debug("Sensors loaded $sensorList")
                sensors.value = sensorList
                sensors.removeSource(sensors)
                updateSelectedSensor()
            }
        }
    }

    private fun initAvailableRelays() {
        val relays = mutableListOf<Relay>()
        relays.add(Relay(Program.NO_RELAY, resourceProvider))
        for (i in 0..appProperties.numberOfRelays) {
            relays.add(Relay(i, resourceProvider))
        }
        this.relays.value = relays
    }

    private fun updateSelectedSensor() {
        logger.info("updateSelectedSensor program:$program sensors:${sensors.value}")
        if (program != null) {
            if (sensors.value != null) {
                for ((index, sensor) in sensors.value!!.iterator().withIndex()) {
                    if (sensor.id == program.sensorId) {
                        selectedSensorPosition.value = index
                        return
                    }
                }
                logger.warn("updateSelectedSensor Sensor used in program not found on sensors list")
            }
            else {
                logger.debug("updateSelectedSensor Sensors not yet loaded")
            }
        }
        selectedSensorPosition.value = 0
    }

    fun setProgram(program: Program) {
        this.program = program
        programName.value = program.name
        maxTemp.value = TemperatureFormatter.format(program.maxTemp)
        minTemp.value = TemperatureFormatter.format(program.minTemp)

        updateSelectedSensor()
        selectedCoolingRelayPosition.value = 0
        selectedHeatingRelayPosition.value = 0
        //TODO: Initialize others
    }

    fun getProgramSaveInProgress(): LiveData<Boolean> {
        return programSaveInProgress
    }

    fun onClickBack(view: View){
        navigateBack()
    }

    fun onClickSave(view: View){
        val program = Program.Builder(program)
            .name(programName.value!!)
            .sensorId(getSelectedSensor())
            .build()

        logger.info("Save program $program")
    }

    private fun getSelectedSensor(): String {
        if (sensors.value != null) {
            return sensors.value!![selectedSensorPosition.value!!].id
        }
        return Program.DEFAULT_SENSOR_ID
    }

    override fun onCleared()
    {
        super.onCleared()
    }
}