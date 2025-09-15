/*
 *     CoinDepo Unofficial Mobile App for Android and iOS
 *     Copyright (C) 2025  Martin Leon Bouchet
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.coindepo.app

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

var appStateNotifier: AppStateNotifier? = null

interface AppStateNotifier {
    val inBackground: StateFlow<Boolean>
    val topMostActivity: Activity?
}

class AppStateNotifierImpl(app: Application) : AppStateNotifier, Application.ActivityLifecycleCallbacks {
    private val startedActivities: MutableList<Activity> = CopyOnWriteArrayList()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var reportingJob: Job? = null
    override val inBackground: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val topMostActivity: Activity?
        get() = startedActivities.lastOrNull()

    init {
        app.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityDestroyed(p0: Activity) {

    }

    override fun onActivityPaused(p0: Activity) {

    }

    override fun onActivityResumed(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityStarted(activity: Activity) {
        startedActivities.add(activity)
        reportingJob?.cancel()
        reportingJob = scope.launch {
            delay(5000)
            if (isActive) evaluateStatus()
        }
    }

    override fun onActivityStopped(activity: Activity) {
        startedActivities.remove(activity)
        reportingJob?.cancel()
        reportingJob = scope.launch {
            delay(5000)
            if (isActive) evaluateStatus()
        }
    }

    private fun evaluateStatus() {
        when {
            startedActivities.isEmpty() && !inBackground.value -> {
                inBackground.value = true
            }
            startedActivities.isNotEmpty() && inBackground.value -> {
                inBackground.value = false
            }
        }
    }
}