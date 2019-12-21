package com.pma.bcc.utils

class TemperatureFormatter {
    companion object {
        fun format(temp: Double) = "%.1f".format(temp)
    }
}