package pmarcinkowski.bcc.model

import com.pma.bcc.model.ThermSensor
import com.pma.bcc.model.ThermSensorRepository
import io.reactivex.Observable
import org.junit.Test
import org.mockito.Mockito
import com.pma.bcc.server.ServerApi


class ThermSensorRepositoryTest {

    @Test
    fun whenSensorsWereAvailableOnServer_thenShouldBeReturnedFromRepository() {
        val sensors = ArrayList<ThermSensor>()
        sensors.add(ThermSensor("123", "sensor_1"))
        val serviceApiMock = Mockito.mock(ServerApi::class.java)
        Mockito.`when`(serviceApiMock.getSensors()).thenReturn(Observable.just(sensors))
        val repository = ThermSensorRepository(serviceApiMock)

        repository.getThermSensors().test()
            .assertResult(sensors)
            .dispose()
    }

    @Test
    fun whenSensorsWereCachedInRepositoryAndUpdatedOnServer_thenTheUpdatedOnesShouldBeReturnedFromRepository() {
        val sensors = ArrayList<ThermSensor>()
        sensors.add(ThermSensor("123", "sensor_1"))
        val serviceApiMock = Mockito.mock(ServerApi::class.java)
        Mockito.`when`(serviceApiMock.getSensors()).thenReturn(Observable.just(sensors))
        val repository = ThermSensorRepository(serviceApiMock)

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