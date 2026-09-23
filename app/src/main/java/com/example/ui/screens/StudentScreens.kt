package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SmartBusViewModel

@Composable
fun StudentMainScreen(
    viewModel: SmartBusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val allBuses by viewModel.allBuses.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    val allRoutes by viewModel.allRoutes.collectAsState()
    val allStops by viewModel.allStops.collectAsState()
    val arrivalPrompt by viewModel.studentStopArrivalPrompt.collectAsState()
    val todayAttendanceRecords by viewModel.todayAttendanceRecords.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("My Bus", "Live Map", "QR Pass", "Attendance", "Route")

    // Match student entity
    val student = allStudents.find { it.registerNumber == currentUser.registerNumber || it.id == "STU-01" } ?: allStudents.first()
    val assignedBus = allBuses.find { it.id == student.assignedBusId } ?: allBuses.first()
    val routeStops = allStops.filter { it.routeId == student.assignedRouteId }.sortedBy { it.sequenceNumber }

    Column(modifier = modifier.fillMaxSize().testTag("student_main_screen")) {
        // Geofence Stop Arrival Notification Banner / Modal
        if (arrivalPrompt != null) {
            val (bus, stop) = arrivalPrompt!!
            Surface(
                color = BrandBluePrimary,
                modifier = Modifier.fillMaxWidth().testTag("bus_arrival_banner")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = BrandCyanLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BUS ARRIVED AT ${stop.stopName.uppercase()}",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Attendance window is now OPEN (Next 5 mins). Please confirm your transit status below:",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.studentSelfConfirmBoarding(student.id) },
                            modifier = Modifier.weight(1f).testTag("confirm_boarding_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = StatusRunningGreen)
                        ) {
                            Text("I AM BOARDING", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                        }
                        Button(
                            onClick = { viewModel.studentSelfConfirmDropOff(student.id) },
                            modifier = Modifier.weight(1f).testTag("confirm_dropoff_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCyan)
                        ) {
                            Text("I AM GETTING DOWN", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                        }
                        IconButton(
                            onClick = { viewModel.dismissArrivalPrompt() }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Student Sub Navigation Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 12.dp,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = (selectedTab == index),
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> StudentHomeTab(
                    student = student,
                    bus = assignedBus,
                    onTrackBusClick = { selectedTab = 1 },
                    onViewQrClick = { selectedTab = 2 },
                    onSosClick = { reason -> viewModel.triggerEmergency(reason, assignedBus.id) },
                    onCallCoordinator = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${assignedBus.coordinatorPhone}"))
                        context.startActivity(intent)
                    }
                )
                1 -> InteractiveMapCanvas(
                    buses = allBuses,
                    routes = allRoutes,
                    stops = allStops,
                    selectedBusId = assignedBus.id,
                    onSelectBus = {},
                    onSelectStop = {},
                    isSimulating = true,
                    simSpeed = 1f,
                    onToggleSimulation = { viewModel.toggleSimulation() },
                    onChangeSimSpeed = { viewModel.setSimSpeed(it) },
                    highlightedBusId = assignedBus.id,
                    modifier = Modifier.fillMaxSize()
                )
                2 -> Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    StudentQrBusPassCard(student = student, busNumber = assignedBus.busNumber)
                }
                3 -> StudentAttendanceHistoryTab(student = student, records = todayAttendanceRecords)
                4 -> StudentRouteStopsTab(bus = assignedBus, stops = routeStops)
            }
        }
    }
}

@Composable
fun StudentHomeTab(
    student: StudentEntity,
    bus: BusEntity,
    onTrackBusClick: () -> Unit,
    onViewQrClick: () -> Unit,
    onSosClick: (reason: String) -> Unit,
    onCallCoordinator: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // "Where is my bus?" Big Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("student_hero_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BrandBluePrimary)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "WHERE IS MY BUS?",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandCyanLight,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = bus.busNumber,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = bus.routeName,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                        BusStatusBadge(status = bus.status)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Next Stop & ETA Badge
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.6f),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Current Stop Location:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = bus.currentStopName ?: "Perur Patteeswarar",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Approaching: ${bus.nextStopName ?: "Vedapatti Junction"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BrandCyanLight
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${bus.etaMinutes} min",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandCyanLight
                                )
                                Text(
                                    text = "Est. Arrival",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onTrackBusClick,
                            modifier = Modifier.weight(1f).testTag("track_bus_live_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCyan),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Track Bus", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = onViewQrClick,
                            modifier = Modifier.weight(1f).testTag("view_qr_pass_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("QR Pass", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Student's Assigned Stop & Personal Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Boarding Details",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        AttendanceStatusBadge(status = student.todayAttendanceStatus)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Assigned Stop: ${student.boardingStopName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Drop-off Destination: ${student.dropOffStopName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Occupancy: ${bus.currentPassengers} / ${bus.capacity} seats taken",
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandCyanLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Contact Crew Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Assigned Bus Staff",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Coordinator: ${bus.coordinatorName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(text = "Driver: ${bus.driverName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(
                            onClick = onCallCoordinator,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(BrandCyan)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call Coordinator", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Student SOS
        item {
            SosEmergencyButton(
                onTriggerEmergency = onSosClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun StudentAttendanceHistoryTab(
    student: StudentEntity,
    records: List<AttendanceRecordEntity>
) {
    val studentRecords = records.filter { it.studentId == student.id }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "My Transit Attendance Records",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Official digital timestamp logs for Karunya University transport",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (studentRecords.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FactCheck, contentDescription = null, tint = BrandCyanLight, modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Today's Travel Status: ${student.todayAttendanceStatus.label}", fontWeight = FontWeight.Bold)
                    Text("Boarding stop: ${student.boardingStopName}", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(studentRecords) { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(StatusRunningGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = StatusRunningGreen)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = record.dateString, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "Boarded: ${record.boardingTime ?: "07:35 AM"} at ${record.boardingStopName}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Verified via ${record.verificationMethod.label} (${record.verifiedBy})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            AttendanceStatusBadge(status = record.status)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentRouteStopsTab(
    bus: BusEntity,
    stops: List<StopEntity>
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "${bus.routeName} Schedule",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Estimated stop timings and sequence along transit path",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(stops) { stop ->
                val isCurrent = (stop.id == bus.currentStopId)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) BrandCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isCurrent) BrandCyanLight else Color(0xFF334155)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${stop.sequenceNumber}",
                                color = if (isCurrent) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stop.stopName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(
                                text = "Expected Arrival: ${stop.expectedArrival}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (isCurrent) {
                            Surface(shape = RoundedCornerShape(6.dp), color = BrandCyan) {
                                Text("BUS HERE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
