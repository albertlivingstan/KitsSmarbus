package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceStatus
import com.example.data.model.BusStatus
import com.example.ui.theme.*

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .testTag("metric_card_${title.lowercase().replace(" ", "_")}")
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
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
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun BusStatusBadge(status: BusStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, icon) = when (status) {
        BusStatus.RUNNING -> Triple(StatusRunningGreen.copy(alpha = 0.18f), StatusRunningGreen, Icons.Default.DirectionsBus)
        BusStatus.AT_STOP -> Triple(StatusAtStopCyan.copy(alpha = 0.18f), StatusAtStopCyan, Icons.Default.Place)
        BusStatus.DELAYED -> Triple(StatusDelayedAmber.copy(alpha = 0.18f), StatusDelayedAmber, Icons.Default.Warning)
        BusStatus.EMERGENCY -> Triple(StatusEmergencyRed.copy(alpha = 0.25f), StatusEmergencyRed, Icons.Default.CrisisAlert)
        BusStatus.SCHEDULED -> Triple(StatusScheduledPurple.copy(alpha = 0.18f), StatusScheduledPurple, Icons.Default.Schedule)
        BusStatus.COMPLETED -> Triple(StatusOfflineGray.copy(alpha = 0.18f), StatusOfflineGray, Icons.Default.CheckCircle)
        BusStatus.OFFLINE, BusStatus.NOT_STARTED -> Triple(StatusOfflineGray.copy(alpha = 0.15f), StatusOfflineGray, Icons.Default.PauseCircle)
    }

    Surface(
        modifier = modifier.testTag("bus_status_${status.name.lowercase()}"),
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AttendanceStatusBadge(status: AttendanceStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        AttendanceStatus.ON_BUS -> Pair(StatusRunningGreen.copy(alpha = 0.18f), StatusRunningGreen)
        AttendanceStatus.PRESENT -> Pair(BrandCyan.copy(alpha = 0.18f), BrandCyan)
        AttendanceStatus.DROPPED_OFF -> Pair(BrandAccent.copy(alpha = 0.18f), BrandAccent)
        AttendanceStatus.ABSENT -> Pair(StatusEmergencyRed.copy(alpha = 0.18f), StatusEmergencyRed)
        AttendanceStatus.NOT_MARKED -> Pair(StatusOfflineGray.copy(alpha = 0.15f), StatusOfflineGray)
    }

    Surface(
        modifier = modifier.testTag("attendance_status_${status.name.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Text(
            text = status.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SosEmergencyButton(
    onTriggerEmergency: (reason: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf("Breakdown / Mechanical Issue") }

    val reasons = listOf(
        "Breakdown / Mechanical Issue",
        "Medical Emergency / Accident",
        "Road Block / Severe Traffic Jam",
        "Student Safety Incident",
        "Weather Hazard / Flooding"
    )

    Button(
        onClick = { showDialog = true },
        modifier = modifier
            .testTag("sos_emergency_button")
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = StatusEmergencyRed),
        shape = RoundedCornerShape(14.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CrisisAlert,
            contentDescription = "SOS Emergency Alert",
            tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "SOS EMERGENCY",
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = 1.sp
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = StatusEmergencyRed,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Broadcast Emergency SOS?",
                    fontWeight = FontWeight.Bold,
                    color = StatusEmergencyRed
                )
            },
            text = {
                Column {
                    Text(
                        text = "This will instantly alert Karunya Transport Administration, Campus Security, and show on the live central dispatch map.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Select Incident Nature:",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    reasons.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = r }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (selectedReason == r),
                                onClick = { selectedReason = r },
                                colors = RadioButtonDefaults.colors(selectedColor = StatusEmergencyRed)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = r, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onTriggerEmergency(selectedReason)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusEmergencyRed)
                ) {
                    Text("TRIGGER SOS NOW", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
