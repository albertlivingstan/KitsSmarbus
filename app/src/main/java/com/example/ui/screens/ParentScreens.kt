package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.AttendanceStatus
import com.example.ui.components.AttendanceStatusBadge
import com.example.ui.components.BusStatusBadge
import com.example.ui.components.InteractiveMapCanvas
import com.example.ui.theme.*
import com.example.ui.viewmodel.SmartBusViewModel

@Composable
fun ParentMainScreen(
    viewModel: SmartBusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allStudents by viewModel.allStudents.collectAsState()
    val allBuses by viewModel.allBuses.collectAsState()
    val allRoutes by viewModel.allRoutes.collectAsState()
    val allStops by viewModel.allStops.collectAsState()

    val ward = allStudents.firstOrNull() ?: return
    val bus = allBuses.find { it.id == ward.assignedBusId } ?: allBuses.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("parent_main_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ward Safe Transit Status Card
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
                                text = "STUDENT TRANSIT TRACKER",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandCyanLight,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = ward.fullName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "${ward.registerNumber} • ${ward.department}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                        AttendanceStatusBadge(status = ward.todayAttendanceStatus)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A).copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = StatusRunningGreen)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = when (ward.todayAttendanceStatus) {
                                    AttendanceStatus.ON_BUS -> "Safely onboard ${bus.busNumber} en route to Karunya Campus."
                                    AttendanceStatus.DROPPED_OFF -> "Alighted safely inside Karunya Nagar Campus Gate."
                                    AttendanceStatus.ABSENT -> "Marked absent for morning transit."
                                    else -> "Scheduled for boarding at ${ward.boardingStopName}."
                                },
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Live Bus Map for Ward
        item {
            Text(
                text = "Live Bus Location (${bus.busNumber})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                InteractiveMapCanvas(
                    buses = listOf(bus),
                    routes = allRoutes.filter { it.id == bus.routeId },
                    stops = allStops.filter { it.routeId == bus.routeId },
                    selectedBusId = bus.id,
                    onSelectBus = {},
                    onSelectStop = {},
                    isSimulating = true,
                    simSpeed = 1f,
                    onToggleSimulation = {},
                    onChangeSimSpeed = {},
                    highlightedBusId = bus.id,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Bus Crew & Emergency Contact
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Assigned Crew & Helpline",
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
                            Text(text = "Campus Transit Helpline: +91 422 261 4000", fontSize = 11.sp, color = BrandCyanLight)
                        }
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${bus.coordinatorPhone}"))
                                context.startActivity(intent)
                            },
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
    }
}
