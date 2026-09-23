package com.example.data.simulator

import com.example.data.model.BusStatus
import com.example.data.model.StopEntity
import com.example.data.repository.SmartBusRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlin.math.*

class GpsSimulatorEngine(
    private val repository: SmartBusRepository,
    private val scope: CoroutineScope
) {
    private var simulationJob: Job? = null

    private val _isSimulating = MutableStateFlow(true)
    val isSimulating: StateFlow<Boolean> = _isSimulating.asStateFlow()

    private val _simSpeedMultiplier = MutableStateFlow(1f) // 1x, 2x, 4x
    val simSpeedMultiplier: StateFlow<Float> = _simSpeedMultiplier.asStateFlow()

    // Map of busId to segment progress: Pair(currentStopIndex, progressRatio: 0f..1f)
    private val busProgressMap = mutableMapOf<String, Pair<Int, Float>>()

    init {
        startSimulation()
    }

    fun startSimulation() {
        if (simulationJob?.isActive == true) return
        _isSimulating.value = true
        simulationJob = scope.launch(Dispatchers.Default) {
            while (isActive && _isSimulating.value) {
                try {
                    tickSimulation()
                } catch (e: Exception) {
                    // Ignore transient errors
                }
                val delayTime = (3000L / _simSpeedMultiplier.value).toLong().coerceAtLeast(800L)
                delay(delayTime)
            }
        }
    }

    fun stopSimulation() {
        _isSimulating.value = false
        simulationJob?.cancel()
        simulationJob = null
    }

    fun toggleSimulation() {
        if (_isSimulating.value) stopSimulation() else startSimulation()
    }

    fun setSpeedMultiplier(multiplier: Float) {
        _simSpeedMultiplier.value = multiplier
    }

    private suspend fun tickSimulation() {
        val buses = repository.allBuses.first()
        val allStops = repository.allStops.first()

        for (bus in buses) {
            // Only simulate buses that are RUNNING or AT_STOP or DELAYED and marked as isDemoGps
            if (!bus.isDemoGps || bus.status == BusStatus.OFFLINE || bus.status == BusStatus.COMPLETED) {
                continue
            }

            val routeStops = allStops
                .filter { it.routeId == bus.routeId }
                .sortedBy { it.sequenceNumber }

            if (routeStops.size < 2) continue

            val currentProgress = busProgressMap[bus.id] ?: Pair(0, 0.1f)
            var stopIndex = currentProgress.first
            var ratio = currentProgress.second

            // Progress ratio step
            ratio += 0.08f * _simSpeedMultiplier.value

            if (ratio >= 1.0f) {
                ratio = 0f
                stopIndex++
                if (stopIndex >= routeStops.size - 1) {
                    // Reached end of line!
                    stopIndex = 0
                }
            }

            busProgressMap[bus.id] = Pair(stopIndex, ratio)

            val fromStop = routeStops[stopIndex]
            val toStop = routeStops[stopIndex + 1]

            // Interpolate coordinate
            val currentLat = fromStop.latitude + (toStop.latitude - fromStop.latitude) * ratio
            val currentLng = fromStop.longitude + (toStop.longitude - fromStop.longitude) * ratio

            // Calculate bearing & distance
            val heading = calculateBearing(fromStop.latitude, fromStop.longitude, toStop.latitude, toStop.longitude)
            val distToNextStopKm = calculateDistanceKm(currentLat, currentLng, toStop.latitude, toStop.longitude)
            val speed = if (ratio < 0.15f || ratio > 0.90f) 22f else 44f + (stopIndex * 2)

            val etaMinutes = ((distToNextStopKm / speed) * 60).roundToInt().coerceAtLeast(1)

            val status = when {
                ratio < 0.12f -> BusStatus.AT_STOP
                ratio > 0.85f && distToNextStopKm < 0.3 -> BusStatus.AT_STOP
                bus.status == BusStatus.DELAYED -> BusStatus.DELAYED
                bus.status == BusStatus.EMERGENCY -> BusStatus.EMERGENCY
                else -> BusStatus.RUNNING
            }

            repository.updateBusTelemetry(
                busId = bus.id,
                lat = currentLat,
                lng = currentLng,
                speed = if (status == BusStatus.AT_STOP) 0f else speed,
                heading = heading,
                status = status,
                passengers = bus.currentPassengers,
                currentStopId = fromStop.id,
                currentStopName = fromStop.stopName,
                nextStopId = toStop.id,
                nextStopName = toStop.stopName,
                etaMinutes = etaMinutes
            )
        }
    }

    private fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val φ1 = Math.toRadians(lat1)
        val φ2 = Math.toRadians(lat2)
        val Δλ = Math.toRadians(lon2 - lon1)
        val y = sin(Δλ) * cos(φ2)
        val x = cos(φ1) * sin(φ2) - sin(φ1) * cos(φ2) * cos(Δλ)
        val θ = atan2(y, x)
        return ((Math.toDegrees(θ) + 360) % 360).toFloat()
    }

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
