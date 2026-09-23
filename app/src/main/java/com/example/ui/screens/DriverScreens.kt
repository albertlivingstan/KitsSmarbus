package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusEntity
import com.example.data.model.BusStatus
import com.example.data.model.StopEntity
import com.example.ui.components.BusStatusBadge
import com.example.ui.components.SosEmergencyButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.SmartBusViewModel

@Composable
fun DriverMainScreen(
    viewModel: SmartBusViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allBuses by viewModel.allBuses.collectAsState()
    val allStops by viewModel.allStops.collectAsState()

    val assignedBus = allBuses.find { it.id == currentUser.assignedBusId } ?: allBuses.firstOrNull()
    val routeStops = allStops
        .filter { it.routeId == (assignedBus?.routeId ?: "ROUTE-01") }
        .sortedBy { it.sequenceNumber }

    val isTripActive = assignedBus?.status == BusStatus.RUNNING || assignedBus?.status == BusStatus.AT_STOP

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("driver_main_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Driver Cockpit Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BrandBluePrimary)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DRIVER COCKPIT HUD",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandCyanLight,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = assignedBus?.busNumber ?: "Bus #12",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "${assignedBus?.registrationNumber} • ${assignedBus?.routeName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                        if (assignedBus != null) {
                            BusStatusBadge(status = assignedBus.status)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Speedometer & Telemetry
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A))
                                    .border(3.dp, if (isTripActive) StatusRunningGreen else StatusOfflineGray, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${assignedBus?.speedKmh?.toInt() ?: 0}",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text("KM/H", fontSize = 9.sp, color = BrandCyanLight)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Speed", style = MaterialTheme.typography.labelSmall, color = Color(0xFFCBD5E1))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${assignedBus?.currentPassengers ?: 0} / ${assignedBus?.capacity ?: 50}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text("Onboard Pax", style = MaterialTheme.typography.labelSmall, color = Color(0xFFCBD5E1))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${assignedBus?.etaMinutes ?: 0} min",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandCyanLight
                            )
                            Text("Next Stop ETA", style = MaterialTheme.typography.labelSmall, color = Color(0xFFCBD5E1))
                        }
                    }
                }
            }
        }

        // High-Contrast Primary Trip Actions (Huge buttons for driver safety)
        item {
            if (!isTripActive) {
                Button(
                    onClick = {
                        if (assignedBus != null) {
                            viewModel.startTrip(assignedBus.id)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .testTag("driver_start_trip_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusRunningGreen),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(28.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "START MORNING TRIP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            } else {
                Button(
                    onClick = {
                        if (assignedBus != null) {
                            viewModel.endTrip(assignedBus.id)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .testTag("driver_end_trip_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandAccent),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(28.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "COMPLETE & END TRIP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Emergency SOS
        item {
            SosEmergencyButton(
                onTriggerEmergency = { reason ->
                    viewModel.triggerEmergency(reason, assignedBus?.id)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Next Stop & Navigation Guidance
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Navigation, contentDescription = null, tint = BrandCyanLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Next Scheduled Stop:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = assignedBus?.nextStopName ?: "Karunya Nagar Main Gate",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Estimated Arrival in ${assignedBus?.etaMinutes ?: 5} minutes • Geofence armed",
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandCyanLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Stops Timeline
        item {
            Text(
                text = "Route Waypoint Schedule (${routeStops.size} stops)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(routeStops) { stop ->
            val isCurrent = (stop.id == assignedBus?.currentStopId)
            val isNext = (stop.id == assignedBus?.nextStopId)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isCurrent -> BrandBluePrimary.copy(alpha = 0.35f)
                        isNext -> BrandCyan.copy(alpha = 0.25f)
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
                border = if (isNext) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BrandCyanLight)) else CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isNext) BrandCyanLight else Color(0xFF334155)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${stop.sequenceNumber}",
                            color = if (isNext) Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stop.stopName,
                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Scheduled: ${stop.expectedArrival}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (isNext) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandCyan
                        ) {
                            Text(
                                text = "NEXT",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
