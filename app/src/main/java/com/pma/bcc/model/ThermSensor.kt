package com.pma.bcc.model

data class ThermSensor(
    val id: String,
    val name: String
) {
    override fun toString(): String {
        return if (name.isNotEmpty()) "$name ($id)" else "$id"
    }
}