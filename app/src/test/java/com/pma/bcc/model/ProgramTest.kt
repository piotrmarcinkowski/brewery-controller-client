package com.pma.bcc.model

import com.google.gson.GsonBuilder
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

class ProgramTest {
    @Test
    fun whenDeserializingFromJson_itShouldReturnCorrectObject() {
        val json = """{"id": "fake_id_1", "name": "Fake Program 1", "crc": "1755700963", "sensor_id": "fake_sensor_2", "heating_relay_index": -1, "cooling_relay_index": 1, "min_temp": 18.0, "max_temp": 19.0, "active": true}"""

        val gson = GsonBuilder()
            .registerTypeAdapter(Temperature::class.java, TemperatureTypeAdapter())
            .create()

        val program: Program = gson.fromJson(json, Program::class.java)

        assertEquals(BigDecimal(18.0), program?.minTemp.toBigDecimal())
        assertEquals(BigDecimal(19.0), program?.maxTemp.toBigDecimal())
    }
}