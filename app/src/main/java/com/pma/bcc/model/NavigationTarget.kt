package com.pma.bcc.model

import java.util.*
import kotlin.collections.HashMap

enum class TargetId {
    Back,
    Programs,
    ProgramDetails,
    ProgramEdit,
    ProgramAdd,
    Settings
}

enum class TargetArgumentKey {
    ProgramDetailsProgram,
    ProgramEditProgram
}

open class NavigationTarget(val targetId: TargetId) {

    private val arguments: HashMap<TargetArgumentKey, Any> = HashMap()

    fun addArg(key: TargetArgumentKey, arg: Any) : NavigationTarget {
        arguments[key] = arg
        return this
    }

    fun getArgs(): Map<TargetArgumentKey, Any> {
        return Collections.unmodifiableMap(arguments)
    }

    override fun toString(): String {
        return "[$targetId]"
    }
}


