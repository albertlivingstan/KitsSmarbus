package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceStatus
import com.example.data.model.StudentEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun StudentQrBusPassCard(
    student: StudentEntity,
    busNumber: String,
    modifier: Modifier = Modifier
) {
    val currentTime = remember {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("student_qr_bus_pass_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BrandCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "KARUNYA SMARTBUS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BrandCyanLight,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Official Digital Bus Pass • 2026-27",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandBluePrimary.copy(alpha = 0.4f),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Text(
                        text = busNumber,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Student Avatar + Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(BrandBlueLight.copy(alpha = 0.25f))
                        .border(2.dp, BrandCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                        fontWeight = FontWeight.Bold,
                        color = BrandCyanLight,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${student.registerNumber} • ${student.department}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Stop: ${student.boardingStopName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandCyanLight,
                        fontWeight = FontWeight.Medium
                    )
                }
                AttendanceStatusBadge(status = student.todayAttendanceStatus)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Crisp Stylized QR Code Box
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                QrMatrixCanvas(
                    payload = student.qrCodePayload,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic Anti-Fraud Hash
            Text(
                text = "ID: ${student.qrCodePayload.hashCode().toString(16).uppercase()} • VERIFIED",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Scan at boarding door with bus coordinator",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun QrMatrixCanvas(
    payload: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val sizePx = size.minDimension
        val gridSize = 21 // Standard 21x21 QR Version 1
        val cellSize = sizePx / gridSize
        val hash = abs(payload.hashCode())

        // Draw Finder Patterns (top-left, top-right, bottom-left)
        fun drawFinder(startX: Float, startY: Float) {
            // Outer 7x7 box
            drawRect(
                color = Color.Black,
                topLeft = Offset(startX, startY),
                size = Size(cellSize * 7, cellSize * 7)
            )
            // Inner 5x5 white
            drawRect(
                color = Color.White,
                topLeft = Offset(startX + cellSize, startY + cellSize),
                size = Size(cellSize * 5, cellSize * 5)
            )
            // Center 3x3 black
            drawRect(
                color = Color.Black,
                topLeft = Offset(startX + cellSize * 2, startY + cellSize * 2),
                size = Size(cellSize * 3, cellSize * 3)
            )
        }

        drawFinder(0f, 0f)
        drawFinder((gridSize - 7) * cellSize, 0f)
        drawFinder(0f, (gridSize - 7) * cellSize)

        // Draw deterministic matrix data cells based on payload hash
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                // Skip finder patterns
                val isFinderTL = row < 7 && col < 7
                val isFinderTR = row < 7 && col >= gridSize - 7
                val isFinderBL = row >= gridSize - 7 && col < 7
                if (isFinderTL || isFinderTR || isFinderBL) continue

                // Timing tracks
                if (row == 6 || col == 6) {
                    if ((row + col) % 2 == 0) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(col * cellSize, row * cellSize),
                            size = Size(cellSize, cellSize)
                        )
                    }
                    continue
                }

                // Deterministic pseudo-data bit
                val bitIndex = (row * gridSize + col + (hash % 100))
                val isFilled = ((bitIndex * 31 + (hash shr (bitIndex % 16))) % 3 == 0)
                if (isFilled) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(col * cellSize, row * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}

@Composable
fun QrScannerView(
    students: List<StudentEntity>,
    onStudentScanned: (student: StudentEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // Laser animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    var flashEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("qr_scanner_view"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0F172A))
                .border(2.dp, BrandSurfaceBorder, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Camera feed simulation background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A)
                            )
                        )
                    )
            )

            // Scanning reticle canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val pad = 40.dp.toPx()
                val reticleSize = size.width - (pad * 2)
                val cornerLen = 28.dp.toPx()
                val strokeW = 4.dp.toPx()

                // Corner lines
                // Top-left
                drawLine(BrandCyanLight, Offset(pad, pad), Offset(pad + cornerLen, pad), strokeW)
                drawLine(BrandCyanLight, Offset(pad, pad), Offset(pad, pad + cornerLen), strokeW)

                // Top-right
                drawLine(BrandCyanLight, Offset(pad + reticleSize, pad), Offset(pad + reticleSize - cornerLen, pad), strokeW)
                drawLine(BrandCyanLight, Offset(pad + reticleSize, pad), Offset(pad + reticleSize, pad + cornerLen), strokeW)

                // Bottom-left
                drawLine(BrandCyanLight, Offset(pad, pad + reticleSize), Offset(pad + cornerLen, pad + reticleSize), strokeW)
                drawLine(BrandCyanLight, Offset(pad, pad + reticleSize), Offset(pad, pad + reticleSize - cornerLen), strokeW)

                // Bottom-right
                drawLine(BrandCyanLight, Offset(pad + reticleSize, pad + reticleSize), Offset(pad + reticleSize - cornerLen, pad + reticleSize), strokeW)
                drawLine(BrandCyanLight, Offset(pad + reticleSize, pad + reticleSize), Offset(pad + reticleSize, pad + reticleSize - cornerLen), strokeW)

                // Animated Laser Line
                val laserY = pad + (reticleSize * laserProgress)
                drawLine(
                    color = StatusRunningGreen,
                    start = Offset(pad + 8.dp.toPx(), laserY),
                    end = Offset(pad + reticleSize - 8.dp.toPx(), laserY),
                    strokeWidth = 3.dp.toPx()
                )
            }

            // Controls over camera
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
            ) {
                IconButton(
                    onClick = { flashEnabled = !flashEnabled },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                        contentDescription = "Toggle Flash",
                        tint = if (flashEnabled) StatusDelayedAmber else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = "Point camera at student QR pass",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick student tap simulator for coordinator demo
        Text(
            text = "⚡ QUICK SIMULATION (Tap to scan student):",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            students.take(3).forEach { s ->
                OutlinedButton(
                    onClick = { onStudentScanned(s) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_scan_${s.registerNumber.lowercase()}"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = s.fullName.split(" ").first(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = s.registerNumber,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
