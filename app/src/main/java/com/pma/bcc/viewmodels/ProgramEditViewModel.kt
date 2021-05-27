package com.pma.bcc.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.pma.bcc.R
import com.pma.bcc.model.*
import com.pma.bcc.utils.TemperatureFormatter
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mu.KLogging
import javax.inject.Inject

class Relay(private val relayIndex : Int, private val resourceProvider: ResourceProvider) {
    override fun toString(): String {
        return when (relayIndex) {
            Program.NO_RELAY -> resourceProvider.getString(R.string.program_edit_no_relay_used)
            else -> resourceProvider.getString(R.string.program_edit_relay_display_string, relayIndex)
        }
    }
}

class ProgramEditViewModel : BaseViewModel {
    companion object : KLogging()

    private val programRepository: ProgramsRepository
    private val resourceProvider: ResourceProvider
    private val appProperties: AppProperties
    private var program : Program? = null
    val programName = MutableLiveData<String>(Program.DEFAULT_NAME)
    val maxTemp = MutableLiveData<String>(TemperatureFormatter.format(Program.DEFAULT_TEMP))
    val minTemp = MutableLiveData<String>(TemperatureFormatter.format(Program.DEFAULT_TEMP))
    val programIsActive = MutableLiveData<Boolean>(Program.DEFAULT_ACTIVE)
    val selectedSensorPosition = MutableLiveData<Int>(0)
    val selectedCoolingRelayPosition = MutableLiveData<Int>(0)
    val selectedHeatingRelayPosition = MutableLiveData<Int>(0)
    val sensorsLoaded = MutableLiveData<Boolean>(false)
    val sensors = MediatorLiveData<List<ThermSensor>>()
    val relays = MutableLiveData<List<Relay>>()
    private val programSaveInProgress = MutableLiveData<Boolean>()
    private val programUpdate = MutableLiveData<String>()
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
            programRepository.getThermSensors()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn {
                    var errorHandler = ErrorResponseHandler(resourceProvider)
                    showNotification(errorHandler.createNotification(resourceProvider.getString(R.string.program_details_sensors_fetch_error), it))
                    emptyList<ThermSensor>()
                }
                .toFlowable(BackpressureStrategy.BUFFER)
        )
        sensors.addSource(source) { sensorList ->
            run {
                logger.debug("Sensors loaded $sensorList")
                sensorsLoaded.value = true
                sensors.value = sensorList
                sensors.removeSource(sensors)
                updateSelectedSensor(program)
            }
        }
    }

    private fun initAvailableRelays() {
        val relays = mutableListOf<Relay>()
        relays.add(Relay(Program.NO_RELAY, resourceProvider))
        val maxRelayIndex = appProperties.numberOfRelays - 1
        for (i in 0..maxRelayIndex) {
            relays.add(Relay(i, resourceProvider))
        }
        this.relays.value = relays
    }

    private fun updateSelectedSensor(program: Program?) {
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

    private fun updateSelectedRelays(program: Program?) {
        logger.info("updateSelectedRelays program:$program")
        if (program != null) {
            selectedCoolingRelayPosition.value =
                if (program.coolingRelayIndex == Program.NO_RELAY) 0 else program.coolingRelayIndex + 1
            selectedHeatingRelayPosition.value =
                if (program.heatingRelayIndex == Program.NO_RELAY) 0 else program.heatingRelayIndex + 1
        }
        else {
            selectedCoolingRelayPosition.value = 0
            selectedHeatingRelayPosition.value = 0
        }
    }

    fun setProgram(program: Program) {
        this.program = program
        programName.value = program.name
        maxTemp.value = TemperatureFormatter.format(program.maxTemp)
        minTemp.value = TemperatureFormatter.format(program.minTemp)
        programIsActive.value = program.active

        updateSelectedSensor(program)
        updateSelectedRelays(program)
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
            .minTemp(minTemp.value!!.toDouble())
            .maxTemp(maxTemp.value!!.toDouble())
            .coolingRelayIndex(if (selectedCoolingRelayPosition.value!! > 0) selectedCoolingRelayPosition.value!! - 1 else Program.NO_RELAY)
            .heatingRelayIndex(if (selectedHeatingRelayPosition.value!! > 0) selectedHeatingRelayPosition.value!! - 1 else Program.NO_RELAY)
            .sensorId(getSelectedSensor())
            .active(programIsActive.value!!)
            .build()

        logger.info("Save program $program")
        programSaveInProgress.value = true
        var programSaveObservable: Observable<Program>? = null
        if (program.id == Program.ID_UNKNOWN) {
            programSaveObservable = programRepository.createProgram(program)
        }
        else {
            programSaveObservable = programRepository.updateProgram(program)
        }
        programSaveObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    programSaveInProgress.value = false
                    showNotification(Notification(resourceProvider.getString(R.string.program_details_program_saved_successfully)))
                    navigateBack()
                },
                { error ->
                    programSaveInProgress.value = false
                    var errorHandler = ErrorResponseHandler(resourceProvider)
                    showNotification(errorHandler.createNotification(resourceProvider.getString(R.string.program_details_program_update_error), error))
                }
            )
    }

    private fun getSelectedSensor(): String {
        if (sensors.value != null && selectedSensorPosition.value!! < sensors.value!!.size) {
            return sensors.value!![selectedSensorPosition.value!!].id
        }
        return Program.DEFAULT_SENSOR_ID
    }

    override fun onCleared()
    {
        super.onCleared()
    }
}