package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SmartBusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoordinatorMainScreen(
    viewModel: SmartBusViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allBuses by viewModel.allBuses.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()

    val assignedBus = allBuses.find { it.id == currentUser.assignedBusId } ?: allBuses.firstOrNull()
    val busStudents = allStudents.filter { it.assignedBusId == (assignedBus?.id ?: "BUS-01") }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showScannerDialog by remember { mutableStateOf(false) }
    var studentForManualEdit by remember { mutableStateOf<StudentEntity?>(null) }
    var filterStatus by remember { mutableStateOf<AttendanceStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val onboardCount = busStudents.count { it.todayAttendanceStatus == AttendanceStatus.ON_BUS }
    val presentCount = busStudents.count { it.todayAttendanceStatus == AttendanceStatus.PRESENT || it.todayAttendanceStatus == AttendanceStatus.ON_BUS }
    val absentCount = busStudents.count { it.todayAttendanceStatus == AttendanceStatus.ABSENT }
    val droppedCount = busStudents.count { it.todayAttendanceStatus == AttendanceStatus.DROPPED_OFF }

    Column(modifier = modifier.fillMaxSize().testTag("coordinator_main_screen")) {
        // Coordinator Header Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BrandBluePrimary)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BUS COORDINATOR ROSTER",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandCyanLight,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = assignedBus?.busNumber ?: "Bus #12",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = assignedBus?.routeName ?: "Gandhipuram - Karunya",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1)
                        )
                    }
                    SosEmergencyButton(
                        onTriggerEmergency = { reason -> viewModel.triggerEmergency(reason, assignedBus?.id) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricMini("Total", "${busStudents.size}", Color.White)
                    MetricMini("On Board", "$onboardCount", StatusRunningGreen)
                    MetricMini("Dropped", "$droppedCount", BrandCyanLight)
                    MetricMini("Absent", "$absentCount", StatusEmergencyRed)
                }
            }
        }

        // Action Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { showScannerDialog = true },
                modifier = Modifier.weight(1f).testTag("coordinator_scan_qr_button"),
                colors = ButtonDefaults.buttonColors(containerColor = BrandCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SCAN QR PASS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    // Quick batch mark absent for remaining
                    busStudents.filter { it.todayAttendanceStatus == AttendanceStatus.NOT_MARKED }.forEach {
                        viewModel.markAttendance(it.id, AttendanceStatus.ABSENT, VerificationMethod.MANUAL_COORDINATOR, "End of boarding cutoff")
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Mark Rest Absent", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search & Filters
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search student name or register no...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = (filterStatus == null),
                    onClick = { filterStatus = null },
                    label = { Text("All (${busStudents.size})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = (filterStatus == AttendanceStatus.ON_BUS),
                    onClick = { filterStatus = AttendanceStatus.ON_BUS },
                    label = { Text("On Bus ($onboardCount)", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = (filterStatus == AttendanceStatus.ABSENT),
                    onClick = { filterStatus = AttendanceStatus.ABSENT },
                    label = { Text("Absent ($absentCount)", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = (filterStatus == AttendanceStatus.NOT_MARKED),
                    onClick = { filterStatus = AttendanceStatus.NOT_MARKED },
                    label = { Text("Pending", fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Student List
        val filteredList = busStudents.filter { s ->
            val matchQuery = s.fullName.contains(searchQuery, ignoreCase = true) ||
                    s.registerNumber.contains(searchQuery, ignoreCase = true)
            val matchFilter = (filterStatus == null || s.todayAttendanceStatus == filterStatus)
            matchQuery && matchFilter
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredList) { student ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { studentForManualEdit = student },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BrandCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.fullName.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = BrandCyanLight,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = student.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${student.registerNumber} • Stop: ${student.boardingStopName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            AttendanceStatusBadge(status = student.todayAttendanceStatus)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap to update",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    // QR Scanner Dialog
    if (showScannerDialog) {
        AlertDialog(
            onDismissRequest = { showScannerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = BrandCyanLight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Coordinator QR Scanner")
                }
            },
            text = {
                QrScannerView(
                    students = busStudents,
                    onStudentScanned = { scannedStudent ->
                        // If already on bus, mark drop off; else mark on bus
                        val nextStatus = if (scannedStudent.todayAttendanceStatus == AttendanceStatus.ON_BUS) {
                            AttendanceStatus.DROPPED_OFF
                        } else {
                            AttendanceStatus.ON_BUS
                        }
                        viewModel.markAttendance(
                            studentId = scannedStudent.id,
                            status = nextStatus,
                            method = VerificationMethod.QR_SCAN,
                            reason = "Live QR scan verified by coordinator"
                        )
                        showScannerDialog = false
                    }
                )
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { showScannerDialog = false }) {
                    Text("Close Scanner")
                }
            }
        )
    }

    // Manual Attendance Change Dialog
    if (studentForManualEdit != null) {
        val s = studentForManualEdit!!
        AlertDialog(
            onDismissRequest = { studentForManualEdit = null },
            title = { Text("Update Attendance: ${s.fullName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Register No: ${s.registerNumber}\nBoarding Stop: ${s.boardingStopName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Select Status:", fontWeight = FontWeight.Bold)

                    listOf(
                        AttendanceStatus.ON_BUS to "Mark Boarded / On Bus",
                        AttendanceStatus.DROPPED_OFF to "Mark Dropped Off (Arrived)",
                        AttendanceStatus.ABSENT to "Mark Absent",
                        AttendanceStatus.PRESENT to "Mark Present (General)"
                    ).forEach { (status, label) ->
                        Button(
                            onClick = {
                                viewModel.markAttendance(
                                    studentId = s.id,
                                    status = status,
                                    method = VerificationMethod.MANUAL_COORDINATOR,
                                    reason = "Coordinator manual change"
                                )
                                studentForManualEdit = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (status) {
                                    AttendanceStatus.ON_BUS -> StatusRunningGreen
                                    AttendanceStatus.ABSENT -> StatusEmergencyRed
                                    AttendanceStatus.DROPPED_OFF -> BrandAccent
                                    else -> BrandCyan
                                }
                            )
                        ) {
                            Text(label, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { studentForManualEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MetricMini(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color(0xFFCBD5E1))
    }
}
