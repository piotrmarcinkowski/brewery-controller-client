package com.pma.bcc

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.mockito.ArgumentCaptor
import org.mockito.Mockito

inline fun <reified T> mock(): T = Mockito.mock(T::class.java)
inline fun <reified T> argCaptor(): ArgumentCaptor<T> = ArgumentCaptor.forClass(T::class.java)
//fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

class TrampolineSchedulerRule : TestRule {
    private val scheduler by lazy { Schedulers.trampoline() }

    override fun apply(base: Statement?, description: Description?): Statement =
        object : Statement() {
            override fun evaluate() {
                try {
                    RxJavaPlugins.setComputationSchedulerHandler { scheduler }
                    RxJavaPlugins.setIoSchedulerHandler { scheduler }
                    RxJavaPlugins.setNewThreadSchedulerHandler { scheduler }
                    RxJavaPlugins.setSingleSchedulerHandler { scheduler }
                    RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
                    base?.evaluate()
                } finally {
                    RxJavaPlugins.reset()
                    RxAndroidPlugins.reset()
                }
            }
        }
}