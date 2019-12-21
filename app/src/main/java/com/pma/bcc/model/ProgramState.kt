package com.pma.bcc.model

data class ProgramState (
    val programId : String,
    val programCrc : String,
    val currentTemp : Double,
    val heatingActivated : Boolean,
    val coolingActivated : Boolean
)
