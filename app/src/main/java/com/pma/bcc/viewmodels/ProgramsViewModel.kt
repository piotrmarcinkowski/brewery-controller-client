package com.pma.bcc.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pma.bcc.R
import com.pma.bcc.model.*
import com.pma.bcc.net.ServerApiFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import mu.KLogging
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

const val ERROR_ID_CONNECTION_SETTINGS: String = "ConnectionSettings"
const val ERROR_ID_CONNECTION_TIMEOUT: String = "ConnectionTimeout"

class ProgramsViewModel : BaseViewModel {
    companion object : KLogging()

    private var programsRepository: ProgramsRepository
    private val programs = MutableLiveData<List<Program>>()
    private val programStates = MutableLiveData<Map<String, ProgramState>>()
    private val programsLoadInProgress = MutableLiveData<Boolean>()
    private val programsLoadError = MutableLiveData<ConnectionError>()
    private val addProgramAvailable = MutableLiveData<Boolean>()
    private var periodicSubscribeDisposable: Disposable? = null
    private var initialProgramsLoadSuccessful = false

    @Inject constructor(programsRepository: ProgramsRepository) : super()  {
        this.programsRepository = programsRepository
        programsLoadInProgress.value = false
        addProgramAvailable.value = false
    }

    fun getPrograms(): LiveData<List<Program>> {
        logger.info("getPrograms()")
        if (programsLoadError.value != null) {
            logger.info("getPrograms() clearing error: ${programsLoadError.value}")
            programsLoadError.value = null
        }
        programsLoadInProgress.value = true
        addProgramAvailable.value = false

        try {
            loadPrograms()
        } catch (e: ServerApiFactory.InvalidConnectionSettingsException) {
            logger.warn("getPrograms() exception: $e")
            programsLoadInProgress.value = false
            setConnectionErrorInvalidConnectionSettings()
        }
        return programs
    }

    private fun loadPrograms() {
        programsRepository
            .getPrograms()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list ->
                    logger.warn("loadPrograms(): $list")
                    programsLoadInProgress.value = false
                    addProgramAvailable.value = true
                    programs.value = list
                    initialProgramsLoadSuccessful = true
                },
                { error ->
                    logger.warn("loadPrograms(): $error")
                    programsLoadInProgress.value = false
                    if (!initialProgramsLoadSuccessful) {
                        setConnectionErrorTimeout()
                    }
                }
            )
    }

    private fun setConnectionErrorInvalidConnectionSettings() {
        programsLoadError.value = ConnectionError(
            errorId = ERROR_ID_CONNECTION_SETTINGS,
            messageTitleResId = R.string.programs_error_title_cant_connect_server,
            messageResId = R.string.programs_error_message_connection_setup_missing,
            retryActionAvailable = false,
            extraActionAvailable = true,
            retryActionLabelResId = R.string.programs_error_retry_button,
            extraActionLabelResId = R.string.programs_error_navigation_button_connection_settings
        )
    }

    private fun setConnectionErrorTimeout() {
        programsLoadError.value = ConnectionError(
            errorId = ERROR_ID_CONNECTION_TIMEOUT,
            messageTitleResId = R.string.programs_error_title_cant_connect_server,
            messageResId = R.string.programs_error_message_connection_timeout,
            retryActionAvailable = true,
            extraActionAvailable = true,
            retryActionLabelResId = R.string.programs_error_retry_button,
            extraActionLabelResId = R.string.programs_error_navigation_button_connection_settings
        )
    }

    fun getProgramStates(): LiveData<Map<String, ProgramState>> {
        loadProgramStates()
        return programStates
    }

    private fun loadProgramStates() {
        logger.info("reloadProgramStates()")
        try {
            programsRepository
                .getProgramStates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { map -> programStates.value = map },
                    { error ->
                        run {
                            logger.warn("loadProgramStates(): $error")
                            programStates.value = Collections.emptyMap()
                        }
                    }
                )
        } catch (e: ServerApiFactory.InvalidConnectionSettingsException) {
            logger.warn("loadProgramStates() exception: $e")
            setConnectionErrorInvalidConnectionSettings()
        }
    }

    fun programsLoadInProgress(): LiveData<Boolean> {
        return programsLoadInProgress
    }

    fun programsLoadError(): LiveData<ConnectionError> {
        return programsLoadError
    }

    fun addProgramAvailable(): LiveData<Boolean> {
        return addProgramAvailable
    }

    fun showProgramDetails(program: Program) {
        navigateTo(NavigationTarget(TargetId.ProgramDetails)
            .addArg(TargetArgumentKey.ProgramDetailsProgram, program))
    }

    fun goToAddProgram() {
        navigateTo(NavigationTarget(TargetId.ProgramAdd))
    }

    fun goToSettings() {
        navigateTo(NavigationTarget(TargetId.Settings))
    }

    fun retry() {
        val error = programsLoadError.value

        if (error == null) {
            logger.warn("retry() called but no error found")
            return
        }
        logger.info("retry() after error: ${error.errorId}")
        if (!error.retryActionAvailable) {
            logger.warn("retry() error has no retry action, ignoring")
            return
        }
        when(error.errorId) {
            ERROR_ID_CONNECTION_SETTINGS -> getPrograms()
            ERROR_ID_CONNECTION_TIMEOUT -> getPrograms()
            else -> logger.warn("retry() no action for error: ${error.errorId}")
        }
    }

    fun extraAction() {
        val error = programsLoadError.value

        if (error == null) {
            logger.warn("extraAction() called but no error found")
            return
        }
        logger.info("extraAction() after error: ${error.errorId}")
        if (!error.extraActionAvailable) {
            logger.warn("extraAction() error has no extra action, ignoring")
            return
        }
        when(error.errorId) {
            ERROR_ID_CONNECTION_SETTINGS -> navigateTo(NavigationTarget(TargetId.Settings))
            ERROR_ID_CONNECTION_TIMEOUT -> navigateTo(NavigationTarget(TargetId.Settings))
            else -> logger.warn("extraAction() no action for error: ${error.errorId}")
        }
    }

    fun resume() {
        startUpdatingProgramStates()
    }

    fun pause() {
        stopUpdatingProgramStates()
    }

    private fun startUpdatingProgramStates() {
        startUpdatingProgramStates(UPDATE_INTERVAL)
    }

    private fun startUpdatingProgramStates(interval: Long) {
        if (periodicSubscribeDisposable != null && !periodicSubscribeDisposable!!.isDisposed) {
            return
        }

        periodicSubscribeDisposable = Observable.interval(0, interval,
            TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logger.info("Update program states")
                loadProgramStates() },
                { error -> ProgramDetailsViewModel.logger.warn("startUpdatingProgramStates() onError: $error") })
    }

    private fun stopUpdatingProgramStates() {
        periodicSubscribeDisposable?.dispose()
    }
}