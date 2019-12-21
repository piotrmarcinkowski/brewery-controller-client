package com.pma.bcc.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.pma.bcc.R
import com.pma.bcc.TrampolineSchedulerRule
import com.pma.bcc.argCaptor
import com.pma.bcc.mock
import com.pma.bcc.model.NavigationTarget
import com.pma.bcc.model.Program
import com.pma.bcc.model.ProgramState
import com.pma.bcc.model.TargetId
import com.pma.bcc.net.ServerApi
import com.pma.bcc.net.ServerApiFactory
import io.reactivex.Observable
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.io.IOException

class ProgramsViewModelTest {
    private lateinit var viewModel: ProgramsViewModel
    private val serverApiFactory: ServerApiFactory = mock()
    private val serverApi: ServerApi = mock()
    private val programsObserver: Observer<List<Program>> = mock()
    private val programsLoadConnectionErrorObserver: Observer<ConnectionError> = mock()
    private val programsStateObserver: Observer<Map<String, ProgramState>> = mock()
    private val programsLoadInProgressObserver: Observer<Boolean> = mock()
    private val navigationEventsObserver: Observer<NavigationTarget> = mock()
    private lateinit var programsArgumentCaptor: ArgumentCaptor<List<Program>>
    private lateinit var errorArgumentCaptor: ArgumentCaptor<ConnectionError>
    private lateinit var navigationArgumentCaptor: ArgumentCaptor<NavigationTarget>

    @get:Rule
    val liveDataExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val rxSchedulersRule = TrampolineSchedulerRule()

    @Before
    fun before() {
        `when`(serverApiFactory.create()).thenReturn(serverApi)
        `when`(serverApi.getPrograms()).thenReturn(Observable.just(fakePrograms()))
        `when`(serverApi.getProgramStates()).thenReturn(Observable.just(fakeProgramStates()))
        viewModel = ProgramsViewModel(serverApiFactory)

        programsArgumentCaptor = argCaptor()
        errorArgumentCaptor = argCaptor()
        navigationArgumentCaptor = argCaptor()
    }

    private fun fakePrograms() : List<Program> {
        val programs = ArrayList<Program>()
        programs.add(Program.Builder(null).id("1").name("Aipa").active(true).build())
        programs.add(Program.Builder(null).id("2").name("Apa").active(true).build())
        programs.add(Program.Builder(null).id("3").name("Lager 1").active(true).build())
        programs.add(Program.Builder(null).id("4").name("Lager 2").active(true).build())
        programs.add(Program.Builder(null).id("5").name("Pils").active(false).build())
        return programs
    }

    private fun fakeProgramStates() : List<ProgramState> {
        val data = ArrayList<ProgramState>()
        data.add(ProgramState("1", "", 18.6, true, false))
        data.add(ProgramState("2", "", 18.2, false, false))
        data.add(ProgramState("3", "", 19.2, false, false))
        return data
    }

    private fun observeViewModel() {
        viewModel.programsLoadInProgress().observeForever(programsLoadInProgressObserver)
        viewModel.programs().observeForever(programsObserver)
        viewModel.programStates().observeForever(programsStateObserver)
        viewModel.programsLoadError().observeForever(programsLoadConnectionErrorObserver)
        viewModel.navigationEvents().observeForever(navigationEventsObserver)
    }

    @Test
    fun whenProgramsWereNotYetRequested_thenProgressShouldBeReportedAsHidden() {
        viewModel.programsLoadInProgress().observeForever(programsLoadInProgressObserver)

        verify(programsLoadInProgressObserver).onChanged(false)
    }

    @Test
    fun whenProgramsLoadWasRequested_thenProgressShouldBeShownFirstAndDisappearOnceProgramsAreLoaded() {
        observeViewModel()

        val inOrder= inOrder(programsObserver, programsLoadInProgressObserver)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsLoadInProgressObserver).onChanged(true)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsObserver).onChanged(any<List<Program>>())
    }

    @Test
    fun whenProgramsAndStatesWereRequested_thenTheyShouldBeDeliveredInCorrectOrder() {
        observeViewModel()

        val inOrder= inOrder(programsObserver, programsStateObserver, programsLoadInProgressObserver)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsLoadInProgressObserver).onChanged(true)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsObserver).onChanged(any<List<Program>>())
        inOrder.verify(programsStateObserver).onChanged(any<Map<String, ProgramState>>())
    }

    @Test
    fun whenStatesAndProgramsWereRequested_thenTheyShouldBeDeliveredInCorrectOrder() {
        viewModel.programsLoadInProgress().observeForever(programsLoadInProgressObserver)
        viewModel.programStates().observeForever(programsStateObserver)
        viewModel.programs().observeForever(programsObserver)

        val inOrder= inOrder(programsObserver, programsStateObserver, programsLoadInProgressObserver)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsStateObserver).onChanged(any<Map<String, ProgramState>>())
        inOrder.verify(programsLoadInProgressObserver).onChanged(true)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsObserver).onChanged(programsArgumentCaptor.capture())
        inOrder.verifyNoMoreInteractions()

        assertEquals(fakePrograms(), programsArgumentCaptor.value)
    }

    @Test
    fun whenConnectionSetupHasNotBeenDone_thenDisplayInformationToEnterConnectionSettingsBeforeAttemptingToConnect() {
        `when`(serverApiFactory.create()).thenThrow(ServerApiFactory.InvalidConnectionSettingsException())

        observeViewModel()

        verify(navigationEventsObserver, never()).onChanged(any(NavigationTarget::class.java))
        verify(programsObserver, never()).onChanged(any<List<Program>>())
        verify(programsStateObserver, never()).onChanged(any<Map<String, ProgramState>>())

        val errorArgCaptor = ArgumentCaptor.forClass(ConnectionError::class.java)
        val inOrder = inOrder(programsLoadInProgressObserver, programsLoadConnectionErrorObserver)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsLoadInProgressObserver).onChanged(true)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsLoadConnectionErrorObserver).onChanged(errorArgCaptor.capture())

        val error = errorArgCaptor.value
        assertEquals(R.string.programs_error_title_cant_connect_server, error.messageTitleResId)
        assertEquals(R.string.programs_error_message_connection_setup_missing, error.messageResId)
        assertFalse("Retry option should not be available", error.retryActionAvailable)
        assertTrue("Navigation to settings option should be available", error.extraActionAvailable)
    }

    @Test
    fun whenErrorWasReportedWithRetryAvailableButErrorCauseIsStillValid_thenRetryAttemptShouldGenerateTheErrorAgain() {
        `when`(serverApi.getPrograms()).thenReturn(object: Observable<List<Program>>() {
            override fun subscribeActual(observer: io.reactivex.Observer<in List<Program>>) {
                observer.onError(IOException())
            }
        })
        observeViewModel()

        viewModel.retry()

        verify(programsLoadConnectionErrorObserver, times(3)).onChanged(errorArgumentCaptor.capture())
        assertNotNull(errorArgumentCaptor.allValues[0])
        assertNull(errorArgumentCaptor.allValues[1])
        assertEquals(errorArgumentCaptor.allValues[0], errorArgumentCaptor.allValues[2])
        assertTrue(errorArgumentCaptor.value.retryActionAvailable)
    }

    @Test
    fun whenErrorWasReportedWithRetryAvailableAndErrorCauseWasRemoved_thenRetryAttemptShouldClearTheErrorAndRepeatFailedOperation() {
        `when`(serverApi.getPrograms()).thenReturn(object: Observable<List<Program>>() {
            override fun subscribeActual(observer: io.reactivex.Observer<in List<Program>>) {
                observer.onError(IOException())
            }
        })
        observeViewModel()

        reset(serverApi)
        `when`(serverApi.getPrograms()).thenReturn(Observable.just(fakePrograms()))

        viewModel.retry()

        val inOrder = inOrder(programsObserver, programsLoadInProgressObserver, programsLoadConnectionErrorObserver)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsLoadInProgressObserver).onChanged(true)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsLoadConnectionErrorObserver, times(2)).onChanged(errorArgumentCaptor.capture())
        inOrder.verify(programsLoadInProgressObserver).onChanged(true)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsObserver).onChanged(programsArgumentCaptor.capture())

        assertEquals(fakePrograms(), programsArgumentCaptor.value)
        assertNull(errorArgumentCaptor.allValues[1])
        assertTrue(errorArgumentCaptor.allValues[0].retryActionAvailable)
    }

    @Test
    fun whenErrorWasReportedWithRetryUnavailable_thenRetryAttemptShouldBeIgnored() {
        `when`(serverApiFactory.create()).thenThrow(ServerApiFactory.InvalidConnectionSettingsException())

        observeViewModel()

        reset(serverApiFactory)
        `when`(serverApiFactory.create()).thenReturn(serverApi)
        viewModel.retry()

        val inOrder = inOrder(programsObserver, programsLoadInProgressObserver, programsLoadConnectionErrorObserver)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsLoadInProgressObserver).onChanged(true)
        inOrder.verify(programsLoadInProgressObserver).onChanged(false)
        inOrder.verify(programsLoadConnectionErrorObserver).onChanged(any<ConnectionError>())
        // after retry
        inOrder.verify(programsLoadInProgressObserver, never()).onChanged(true)
        inOrder.verify(programsObserver, never()).onChanged(any<List<Program>>())
    }

    @Test
    fun whenErrorWasReportedWithExtraAction_thenChoosingExtraActionShouldTriggerProperBehavior() {
        `when`(serverApiFactory.create()).thenThrow(ServerApiFactory.InvalidConnectionSettingsException())

        observeViewModel()

        viewModel.extraAction()

        verify(navigationEventsObserver).onChanged(navigationArgumentCaptor.capture())
        assertEquals(TargetId.ConnectionSettings, navigationArgumentCaptor.value.targetId)
    }
}