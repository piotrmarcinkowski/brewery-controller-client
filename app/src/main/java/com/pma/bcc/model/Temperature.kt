package com.pma.bcc.model

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.math.BigDecimal
import java.math.RoundingMode

data class Temperature (val value : BigDecimal) {
    constructor(valueString: String) : this(BigDecimal(valueString))
    constructor(valueDouble: Double) : this(BigDecimal(valueDouble))
    fun displayString() = value.setScale(1, RoundingMode.HALF_EVEN).toString()
    fun toBigDecimal() = value

    operator fun inc() = Temperature(value + BigDecimal(0.5))
    operator fun dec() = Temperature(value - BigDecimal(0.5))
    operator fun plus(temperature2: Temperature) : Temperature = Temperature(value + temperature2.value)
    operator fun minus(temperature2: Temperature) : Temperature = Temperature(value - temperature2.value)
}

class TemperatureTypeAdapter : TypeAdapter<Temperature>() {
    override fun write(writer: JsonWriter?, temperature: Temperature?) {
        writer?.value(temperature?.toBigDecimal())
    }

    override fun read(reader: JsonReader?): Temperature {
        return Temperature(reader?.nextDouble()!!)
    }

}