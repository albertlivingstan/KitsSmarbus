package com.example.ui.screens

import android.content.Intent
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    viewModel: SmartBusViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dashboard", "Live Map", "Fleet", "Routes", "Students", "Analytics", "Reports")

    val buses by viewModel.allBuses.collectAsState()
    val routes by viewModel.allRoutes.collectAsState()
    val stops by viewModel.allStops.collectAsState()
    val students by viewModel.allStudents.collectAsState()
    val activeIncidents by viewModel.activeIncidents.collectAsState()
    val selectedBus by viewModel.selectedBus.collectAsState()
    val isSimulating by viewModel.isSimulating.collectAsState()
    val simSpeed by viewModel.simSpeed.collectAsState()

    Column(modifier = modifier.fillMaxSize().testTag("admin_main_screen")) {
        // Active Emergency Alert Banner if any
        if (activeIncidents.isNotEmpty()) {
            val incident = activeIncidents.first()
            Surface(
                color = StatusEmergencyRed,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CrisisAlert, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACTIVE SOS: ${incident.busNumber} (${incident.reason})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Driver: ${incident.driverName} • Contact: ${incident.contactNumber}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { viewModel.resolveEmergency(incident.id, "Resolved by Transport Admin") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("RESOLVE", color = StatusEmergencyRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Secondary Scrollable Tab Row
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
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> AdminDashboardOverview(viewModel = viewModel, onOpenLiveMap = { selectedTab = 1 })
                1 -> AdminLiveMapTab(
                    viewModel = viewModel,
                    buses = buses,
                    routes = routes,
                    stops = stops,
                    selectedBus = selectedBus,
                    isSimulating = isSimulating,
                    simSpeed = simSpeed
                )
                2 -> AdminFleetManagementTab(viewModel = viewModel, buses = buses)
                3 -> AdminRoutesTab(viewModel = viewModel, routes = routes, stops = stops)
                4 -> AdminStudentsTab(viewModel = viewModel, students = students)
                5 -> AdminAnalyticsTab(buses = buses, students = students)
                6 -> AdminReportsTab(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AdminDashboardOverview(
    viewModel: SmartBusViewModel,
    onOpenLiveMap: () -> Unit
) {
    val buses by viewModel.allBuses.collectAsState()
    val students by viewModel.allStudents.collectAsState()
    val drivers by viewModel.allDrivers.collectAsState()
    val activeIncidents by viewModel.activeIncidents.collectAsState()

    val runningCount = buses.count { it.status == BusStatus.RUNNING }
    val onBusCount = students.count { it.todayAttendanceStatus == AttendanceStatus.ON_BUS }
    val presentCount = students.count { it.todayAttendanceStatus == AttendanceStatus.PRESENT || it.todayAttendanceStatus == AttendanceStatus.ON_BUS }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Status Card
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
                                text = "KITS TRANSIT OPERATIONS",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandCyanLight,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Campus Fleet Overview",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        IconButton(
                            onClick = onOpenLiveMap,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(BrandCyan)
                        ) {
                            Icon(Icons.Default.Map, contentDescription = "Open Map", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "10 Active Morning Pickup Routes across Coimbatore district servicing 100+ registered scholars and faculty.",
                        color = Color(0xFFE2E8F0),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Metrics Grid (2 columns)
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "Total Buses",
                    value = "${buses.size}",
                    subtitle = "$runningCount Currently Running",
                    icon = Icons.Default.DirectionsBus,
                    iconTint = BrandCyanLight,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Travelling Now",
                    value = "$onBusCount",
                    subtitle = "of ${students.size} Students",
                    icon = Icons.Default.School,
                    iconTint = StatusRunningGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "Attendance Rate",
                    value = if (students.isNotEmpty()) "${(presentCount * 100) / students.size}%" else "0%",
                    subtitle = "$presentCount Verified Present",
                    icon = Icons.Default.FactCheck,
                    iconTint = BrandAccent,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Emergency Alerts",
                    value = "${activeIncidents.size}",
                    subtitle = if (activeIncidents.isEmpty()) "Fleet All Clear" else "Attention Required",
                    icon = Icons.Default.CrisisAlert,
                    iconTint = if (activeIncidents.isEmpty()) StatusRunningGreen else StatusEmergencyRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Active Buses Live List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Buses (${buses.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onOpenLiveMap) {
                    Text("View on Map", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        items(buses) { bus ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.selectBus(bus)
                        onOpenLiveMap()
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(BrandCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = bus.busNumber.replace("Bus #", "#"),
                            fontWeight = FontWeight.Bold,
                            color = BrandCyanLight,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = bus.busNumber,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${bus.registrationNumber}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = bus.routeName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Next: ${bus.nextStopName ?: "Campus Gate"} (ETA ${bus.etaMinutes}m)",
                            style = MaterialTheme.typography.bodySmall,
                            color = BrandCyanLight,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        BusStatusBadge(status = bus.status)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${bus.currentPassengers}/${bus.capacity} seats",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminLiveMapTab(
    viewModel: SmartBusViewModel,
    buses: List<BusEntity>,
    routes: List<RouteEntity>,
    stops: List<StopEntity>,
    selectedBus: BusEntity?,
    isSimulating: Boolean,
    simSpeed: Float
) {
    var selectedStop by remember { mutableStateOf<StopEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Map viewport
        Box(modifier = Modifier.weight(1f)) {
            InteractiveMapCanvas(
                buses = buses,
                routes = routes,
                stops = stops,
                selectedBusId = selectedBus?.id,
                onSelectBus = { viewModel.selectBus(it) },
                onSelectStop = { selectedStop = it },
                isSimulating = isSimulating,
                simSpeed = simSpeed,
                onToggleSimulation = { viewModel.toggleSimulation() },
                onChangeSimSpeed = { viewModel.setSimSpeed(it) },
                modifier = Modifier.fillMaxSize()
            )

            // Bus detail floating card if a bus is selected
            if (selectedBus != null) {
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${selectedBus.busNumber} • ${selectedBus.registrationNumber}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = selectedBus.routeName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { viewModel.selectBus(null) }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Status", style = MaterialTheme.typography.labelSmall)
                                BusStatusBadge(status = selectedBus.status)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Occupancy", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${selectedBus.currentPassengers} / ${selectedBus.capacity}",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Speed", style = MaterialTheme.typography.labelSmall)
                                Text("${selectedBus.speedKmh.toInt()} km/h", fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ETA", style = MaterialTheme.typography.labelSmall)
                                Text("${selectedBus.etaMinutes} mins", fontWeight = FontWeight.Bold, color = BrandCyanLight)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Driver: ${selectedBus.driverName} (${selectedBus.driverPhone}) • Coord: ${selectedBus.coordinatorName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminFleetManagementTab(
    viewModel: SmartBusViewModel,
    buses: List<BusEntity>
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var busToEdit by remember { mutableStateOf<BusEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Bus Fleet Inventory",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Manage bus vehicles, drivers, and maintenance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Bus")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(buses) { bus ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(BrandCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = BrandCyanLight)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = bus.busNumber, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = bus.registrationNumber,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            BusStatusBadge(status = bus.status)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Route: ${bus.routeName}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Driver: ${bus.driverName} • Coordinator: ${bus.coordinatorName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Capacity: ${bus.capacity} seats • Maintenance: ${bus.maintenanceStatus}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { busToEdit = bus }) {
                                Text("Edit / Reassign")
                            }
                            TextButton(
                                onClick = { viewModel.deleteBus(bus.id) },
                                colors = ButtonDefaults.textButtonColors(contentColor = StatusEmergencyRed)
                            ) {
                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog || busToEdit != null) {
        val editing = busToEdit
        var busNum by remember { mutableStateOf(editing?.busNumber ?: "Bus #15") }
        var regNum by remember { mutableStateOf(editing?.registrationNumber ?: "TN 37 BK 1015") }
        var capacityText by remember { mutableStateOf((editing?.capacity ?: 50).toString()) }
        var routeName by remember { mutableStateOf(editing?.routeName ?: "Gandhipuram - Karunya") }
        var driverName by remember { mutableStateOf(editing?.driverName ?: "Murugan Velusamy") }

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                busToEdit = null
            },
            title = { Text(if (editing != null) "Edit Bus" else "Add New Campus Bus") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = busNum,
                        onValueChange = { busNum = it },
                        label = { Text("Bus Number (e.g. Bus #15)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = regNum,
                        onValueChange = { regNum = it },
                        label = { Text("Registration Number (e.g. TN 37 BK 1015)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = capacityText,
                        onValueChange = { capacityText = it },
                        label = { Text("Seating Capacity") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = routeName,
                        onValueChange = { routeName = it },
                        label = { Text("Assigned Route") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val busId = editing?.id ?: "BUS-${System.currentTimeMillis()}"
                        val newBus = BusEntity(
                            id = busId,
                            busNumber = busNum,
                            registrationNumber = regNum,
                            routeId = editing?.routeId ?: "ROUTE-01",
                            routeName = routeName,
                            driverId = editing?.driverId ?: "DRV-01",
                            driverName = driverName,
                            driverPhone = editing?.driverPhone ?: "+91 94871 10001",
                            coordinatorId = editing?.coordinatorId ?: "CRD-01",
                            coordinatorName = editing?.coordinatorName ?: "Dr. John Peter",
                            coordinatorPhone = editing?.coordinatorPhone ?: "+91 98430 20001",
                            capacity = capacityText.toIntOrNull() ?: 50,
                            currentPassengers = editing?.currentPassengers ?: 0,
                            status = editing?.status ?: BusStatus.SCHEDULED,
                            currentLat = editing?.currentLat ?: 11.0168,
                            currentLng = editing?.currentLng ?: 76.9558,
                            isDemoGps = true
                        )
                        viewModel.saveBus(newBus)
                        showAddDialog = false
                        busToEdit = null
                    }
                ) {
                    Text("Save Bus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showAddDialog = false
                    busToEdit = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AdminRoutesTab(
    viewModel: SmartBusViewModel,
    routes: List<RouteEntity>,
    stops: List<StopEntity>
) {
    var selectedRouteId by remember { mutableStateOf(routes.firstOrNull()?.id ?: "") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Campus Transit Routes & Stops",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Stops, arrival schedules, and geofence coverage",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Route selector chips
        ScrollableTabRow(
            selectedTabIndex = routes.indexOfFirst { it.id == selectedRouteId }.coerceAtLeast(0),
            containerColor = Color.Transparent,
            divider = {}
        ) {
            routes.forEach { r ->
                Tab(
                    selected = (r.id == selectedRouteId),
                    onClick = { selectedRouteId = r.id },
                    text = { Text("${r.routeNumber}: ${r.startLocation.split(" ").first()}") }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        val currentRoute = routes.find { it.id == selectedRouteId }
        val routeStops = stops.filter { it.routeId == selectedRouteId }.sortedBy { it.sequenceNumber }

        if (currentRoute != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = currentRoute.routeName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Distance: ${currentRoute.totalDistanceKm} km • Est. Duration: ${currentRoute.estimatedDurationMinutes} mins",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Morning Departure: ${currentRoute.morningDepartureTime} • Evening Return: ${currentRoute.eveningReturnTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandCyanLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Route Stops (${routeStops.size})",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(routeStops) { stop ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(BrandCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${stop.sequenceNumber}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = stop.stopName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(
                                    text = "Scheduled Arrival: ${stop.expectedArrival} • Geofence: ${stop.geofenceRadiusMeters}m",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Default.Place, contentDescription = null, tint = BrandCyanLight, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStudentsTab(
    viewModel: SmartBusViewModel,
    students: List<StudentEntity>
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredStudents = students.filter {
        it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.registerNumber.contains(searchQuery, ignoreCase = true) ||
                it.department.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Enrolled Students Directory",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Bus assignments, stops, parent contacts, and live travel status",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, reg number, department...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredStudents) { student ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BrandBlueLight.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.fullName.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = BrandCyanLight
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = student.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = "${student.registerNumber} • ${student.department} (${student.year})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Boarding: ${student.boardingStopName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = BrandCyanLight
                            )
                        }
                        AttendanceStatusBadge(status = student.todayAttendanceStatus)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAnalyticsTab(
    buses: List<BusEntity>,
    students: List<StudentEntity>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Transit Analytics & Utilization",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Peak travel volume, attendance distribution, and route efficiency",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Bus Utilization Bar Chart Simulation
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bus Capacity Utilization (%)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    buses.take(6).forEach { bus ->
                        val pct = if (bus.capacity > 0) ((bus.currentPassengers.toFloat() / bus.capacity) * 100).toInt() else 0
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = bus.busNumber, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "$pct% (${bus.currentPassengers}/${bus.capacity})", fontSize = 12.sp, color = BrandCyanLight)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (pct / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                                color = if (pct > 85) StatusDelayedAmber else StatusRunningGreen,
                                trackColor = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }
            }
        }

        // Weekly Attendance Trend Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Weekly Student Transit Volume",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val days = listOf("Mon" to 92, "Tue" to 95, "Wed" to 88, "Thu" to 94, "Fri" to 91, "Today" to 96)
                    Row(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEach { (day, count) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height((count * 0.75).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(if (day == "Today") BrandCyan else BrandBlueLight)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = day, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminReportsTab(viewModel: SmartBusViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var exportedCsv by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Reports & Regulatory Exports",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Generate attendance logs, route performance, and driver trip audits",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        val reports = listOf(
            "Daily Morning Transit Attendance Report",
            "Bus Fleet Utilization & Mileage Summary",
            "Driver Duty & Safety Trip Log",
            "Emergency Incident & SOS Audit Trail"
        )

        reports.forEach { rep ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = rep, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "Format: CSV / Excel Compatible",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                exportedCsv = viewModel.getAttendanceCsv()
                                showDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export CSV")
                    }
                }
            }
        }
    }

    if (showDialog && exportedCsv != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("CSV Export Generated") },
            text = {
                Column {
                    Text("Report successfully generated with official Karunya SmartBus headers:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)
                    ) {
                        Text(
                            text = exportedCsv!!,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, exportedCsv)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Transit Report CSV"))
                        showDialog = false
                    }
                ) {
                    Text("Share / Save")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
