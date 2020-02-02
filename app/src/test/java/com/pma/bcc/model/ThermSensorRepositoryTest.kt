package pmarcinkowski.bcc.model

import com.pma.bcc.model.ThermSensor
import com.pma.bcc.model.ProgramsRepository
import com.pma.bcc.model.ProgramsRepositoryImpl
import io.reactivex.Observable
import org.junit.Test
import org.mockito.Mockito
import com.pma.bcc.net.ServerApi
import com.pma.bcc.net.ServerApiFactory
import org.junit.Before
import org.mockito.Mock


class ThermSensorRepositoryTest {

    private lateinit var repository: ProgramsRepository
    private lateinit var sensors: java.util.ArrayList<ThermSensor>
    private lateinit var serviceApiMock: ServerApi

    @Before
    fun setUp() {
        sensors = ArrayList()
        val serviceApiFactoryMock = Mockito.mock(ServerApiFactory::class.java)
        serviceApiMock = Mockito.mock(ServerApi::class.java)
        Mockito.`when`(serviceApiFactoryMock.create()).thenReturn(serviceApiMock)
        Mockito.`when`(serviceApiMock.getSensors()).thenReturn(Observable.just(sensors))
        repository = ProgramsRepositoryImpl(serviceApiFactoryMock)
    }

    @Test
    fun whenSensorsWereAvailableOnServer_thenShouldBeReturnedFromRepository() {
        sensors.add(ThermSensor("123", "sensor_1"))
        repository.getThermSensors().test()
            .assertResult(sensors)
            .dispose()
    }

    @Test
    fun whenSensorsWereCachedInRepositoryAndUpdatedOnServer_thenTheUpdatedOnesShouldBeReturnedFromRepository() {
        sensors.add(ThermSensor("123", "sensor_1"))

        // first call is to cache sensors
        repository.getThermSensors().test()
        val updatedSensors = ArrayList<ThermSensor>(sensors)
        updatedSensors.add(ThermSensor("234", "sensor_2"))
        Mockito.`when`(serviceApiMock.getSensors()).thenReturn(Observable.just(updatedSensors))

        repository.getThermSensors().test()
            .assertValueAt(0, sensors)
            .assertValueAt(1, updatedSensors)
            .assertNoErrors()
            .assertComplete()
            .dispose()
    }
}