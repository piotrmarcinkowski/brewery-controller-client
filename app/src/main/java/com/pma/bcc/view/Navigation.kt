package com.pma.bcc.view

import android.os.Bundle
import androidx.navigation.NavController
import com.pma.bcc.R
import com.pma.bcc.model.NavigationTarget
import com.pma.bcc.model.TargetArgumentKey
import com.pma.bcc.model.TargetId
import mu.KLogging
import java.io.Serializable

class Navigation {
    companion object : KLogging() {

        //TODO: Refactor to method
        fun navigateTo(navController: NavController, target: NavigationTarget) {
            logger.info("navigateTo() $target")
            when(target.targetId) {
                TargetId.Back -> navController.popBackStack()
                TargetId.ProgramDetails -> navController.navigate(R.id.action_programsFragment_to_programDetailsFragment, createProgramDetailsArgsBundle(target.getArgs()))
                TargetId.ProgramEdit -> navController.navigate(R.id.action_programDetailsFragment_to_programEditFragment, createProgramEditArgsBundle(target.getArgs()))
                TargetId.ProgramAdd -> navController.navigate(R.id.action_programsFragment_to_programEditFragment)
                TargetId.Settings -> navController.navigate(R.id.action_programsFragment_to_settingsFragment)
                else -> logger.warn("navigateTo() unknown target: $target")
            }
        }

        private fun createProgramDetailsArgsBundle(args: Map<TargetArgumentKey, Any>) : Bundle {
            var bundle = Bundle(args.size)
            bundle.putSerializable(TargetArgumentKey.ProgramDetailsProgram.name, args[TargetArgumentKey.ProgramDetailsProgram] as Serializable)
            return bundle
        }

        private fun createProgramEditArgsBundle(args: Map<TargetArgumentKey, Any>) : Bundle {
            var bundle = Bundle(args.size)
            bundle.putSerializable(
                    TargetArgumentKey.ProgramEditProgram.name,
                    args[TargetArgumentKey.ProgramEditProgram] as Serializable)
            return bundle
        }
    }
}