package com.pma.bcc.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Program (
    @SerializedName("id")
    val id : String = ID_UNKNOWN,
    @SerializedName("name")
    val name : String = DEFAULT_NAME,
    @SerializedName("sensor_id")
    val sensorId : String = DEFAULT_SENSOR_ID,
    @SerializedName("heating_relay_index")
    val heatingRelayIndex : Int = NO_RELAY,
    @SerializedName("cooling_relay_index")
    val coolingRelayIndex : Int = NO_RELAY,
    @SerializedName("max_temp")
    val maxTemp : Double = DEFAULT_TEMP,
    @SerializedName("min_temp")
    val minTemp : Double = DEFAULT_TEMP,
    @SerializedName("active")
    val active : Boolean = DEFAULT_ACTIVE,
    @SerializedName("crc")
    val crc : String = CRC_UNKNOWN
) : Serializable {
    companion object {
        const val NO_RELAY = -1
        const val ID_UNKNOWN = ""
        const val DEFAULT_NAME = ""
        const val DEFAULT_SENSOR_ID = ""
        const val DEFAULT_TEMP = 0.0
        const val DEFAULT_ACTIVE = false
        const val CRC_UNKNOWN = ""
    }

    class Builder(program : Program? = null) {
        private var id : String = program?.id ?: ID_UNKNOWN
        private var name : String = program?.name ?: DEFAULT_NAME
        private var sensorId : String = program?.sensorId ?: DEFAULT_SENSOR_ID
        private var heatingRelayIndex : Int = program?.heatingRelayIndex ?: NO_RELAY
        private var coolingRelayIndex : Int = program?.coolingRelayIndex ?: NO_RELAY
        private var maxTemp : Double = program?.maxTemp ?: DEFAULT_TEMP
        private var minTemp : Double = program?.minTemp ?: DEFAULT_TEMP
        private var active : Boolean = program?.active ?: DEFAULT_ACTIVE
        private var crc : String = program?.crc ?: CRC_UNKNOWN

        fun id(id : String) = apply { this.id = id }
        fun name(name : String) = apply { this.name = name }
        fun sensorId(sensorId : String) = apply { this.sensorId = sensorId }
        fun heatingRelayIndex(heatingRelayIndex: Int) = apply { this.heatingRelayIndex = heatingRelayIndex }
        fun coolingRelayIndex(coolingRelayIndex: Int) = apply { this.coolingRelayIndex = coolingRelayIndex }
        fun maxTemp(maxTemp: Double) = apply { this.maxTemp = maxTemp }
        fun minTemp(minTemp: Double) = apply { this.minTemp = minTemp }
        fun active(active: Boolean) = apply { this.active = active }

        fun build() = Program(
            id, name, sensorId, heatingRelayIndex, coolingRelayIndex, maxTemp, minTemp, active, crc)
    }
}
