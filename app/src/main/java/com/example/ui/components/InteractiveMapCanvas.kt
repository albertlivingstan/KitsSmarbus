package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusEntity
import com.example.data.model.BusStatus
import com.example.data.model.RouteEntity
import com.example.data.model.StopEntity
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun InteractiveMapCanvas(
    buses: List<BusEntity>,
    routes: List<RouteEntity>,
    stops: List<StopEntity>,
    selectedBusId: String?,
    onSelectBus: (BusEntity) -> Unit,
    onSelectStop: (StopEntity) -> Unit,
    isSimulating: Boolean,
    simSpeed: Float,
    onToggleSimulation: () -> Unit,
    onChangeSimSpeed: (Float) -> Unit,
    modifier: Modifier = Modifier,
    highlightedBusId: String? = null
) {
    // Pan and zoom states
    var scale by remember { mutableFloatStateOf(1.0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val textMeasurer = rememberTextMeasurer()

    // Bounding box for Coimbatore-Karunya Region
    // Lat: 10.60 to 11.12, Lng: 76.70 to 77.08
    val minLat = 10.60
    val maxLat = 11.12
    val minLng = 76.70
    val maxLng = 77.08

    fun project(lat: Double, lng: Double, width: Float, height: Float): Offset {
        val normX = ((lng - minLng) / (maxLng - minLng)).toFloat().coerceIn(0f, 1f)
        // Invert Y because latitude increases northward while canvas Y increases downward
        val normY = (1f - ((lat - minLat) / (maxLat - minLat)).toFloat()).coerceIn(0f, 1f)

        val px = normX * width
        val py = normY * height

        // Apply pan and zoom relative to center
        val centerX = width / 2f
        val centerY = height / 2f
        val zoomedX = (px - centerX) * scale + centerX + offsetX
        val zoomedY = (py - centerY) * scale + centerY + offsetY

        return Offset(zoomedX, zoomedY)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0A1128))
            .border(1.dp, BrandSurfaceBorder, RoundedCornerShape(16.dp))
            .testTag("interactive_map_canvas")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.6f, 4.0f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
                .pointerInput(buses, stops, scale, offsetX, offsetY) {
                    detectTapGestures { tapOffset ->
                        val w = size.width.toFloat()
                        val h = size.height.toFloat()

                        // Check if tap is near any bus (hit radius 35dp)
                        var hitBus: BusEntity? = null
                        for (bus in buses) {
                            val pos = project(bus.currentLat, bus.currentLng, w, h)
                            val dist = hypot(pos.x - tapOffset.x, pos.y - tapOffset.y)
                            if (dist < 50f) {
                                hitBus = bus
                                break
                            }
                        }
                        if (hitBus != null) {
                            onSelectBus(hitBus)
                            return@detectTapGestures
                        }

                        // Check if tap is near any stop
                        for (stop in stops) {
                            val pos = project(stop.latitude, stop.longitude, w, h)
                            val dist = hypot(pos.x - tapOffset.x, pos.y - tapOffset.y)
                            if (dist < 40f) {
                                onSelectStop(stop)
                                return@detectTapGestures
                            }
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // 1. Draw Map Base Grid & Stylized Terrain
            drawRect(color = Color(0xFF091026))

            // Western Ghats foothills on western side
            val hillsPath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.22f, 0f)
                cubicTo(w * 0.25f, h * 0.35f, w * 0.18f, h * 0.7f, w * 0.24f, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = hillsPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF0F231D), Color(0xFF0D1E2D))
                )
            )

            // Siruvani River Curve
            val riverPath = Path().apply {
                val start = project(10.9700, 76.7100, w, h)
                val mid1 = project(10.9450, 76.7450, w, h)
                val mid2 = project(10.9320, 76.7580, w, h)
                val end = project(10.9100, 76.7900, w, h)
                moveTo(start.x, start.y)
                quadraticBezierTo(mid1.x, mid1.y, mid2.x, mid2.y)
                lineTo(end.x, end.y)
            }
            drawPath(
                path = riverPath,
                color = Color(0xFF0369A1).copy(alpha = 0.45f),
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )

            // 2. Karunya University Campus Perimeter highlight
            val karunyaPos = project(10.9360, 76.7440, w, h)
            drawCircle(
                color = BrandCyan.copy(alpha = 0.15f),
                radius = 42f * scale,
                center = karunyaPos
            )
            drawCircle(
                color = BrandCyanLight,
                radius = 42f * scale,
                center = karunyaPos,
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f))
            )
            drawCircle(
                color = BrandCyanLight,
                radius = 8f,
                center = karunyaPos
            )

            // Campus Label
            val campusLayout = textMeasurer.measure(
                text = AnnotatedString("🎓 KARUNYA CAMPUS"),
                style = TextStyle(
                    color = BrandCyanLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            drawText(
                textLayoutResult = campusLayout,
                topLeft = Offset(karunyaPos.x - (campusLayout.size.width / 2f), karunyaPos.y + 12f)
            )

            // 3. Draw Route Polylines
            val routeColors = mapOf(
                "ROUTE-01" to Color(0xFF38BDF8),
                "ROUTE-02" to Color(0xFF34D399),
                "ROUTE-03" to Color(0xFFA78BFA),
                "ROUTE-04" to Color(0xFFFBBF24),
                "ROUTE-05" to Color(0xFFF472B6)
            )

            routes.forEach { r ->
                val rStops = stops.filter { it.routeId == r.id }.sortedBy { it.sequenceNumber }
                if (rStops.size >= 2) {
                    val path = Path()
                    val firstPos = project(rStops.first().latitude, rStops.first().longitude, w, h)
                    path.moveTo(firstPos.x, firstPos.y)

                    for (i in 1 until rStops.size) {
                        val pt = project(rStops[i].latitude, rStops[i].longitude, w, h)
                        path.lineTo(pt.x, pt.y)
                    }

                    val color = routeColors[r.id] ?: BrandCyan
                    drawPath(
                        path = path,
                        color = color.copy(alpha = 0.65f),
                        style = Stroke(
                            width = 5f * scale.coerceIn(0.8f, 1.8f),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            // 4. Draw Stops along the routes
            stops.forEach { stop ->
                val pos = project(stop.latitude, stop.longitude, w, h)

                // Geofence circle
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = 22f * scale,
                    center = pos
                )
                // Stop pin center
                drawCircle(
                    color = Color.White,
                    radius = 5f * scale.coerceIn(0.8f, 1.5f),
                    center = pos
                )
                drawCircle(
                    color = Color(0xFF1E293B),
                    radius = 3f * scale.coerceIn(0.8f, 1.5f),
                    center = pos
                )

                // Draw label if zoomed in or scale > 1.2
                if (scale > 1.25f) {
                    val stopLayout = textMeasurer.measure(
                        text = AnnotatedString(stop.stopName),
                        style = TextStyle(color = Color(0xFFCBD5E1), fontSize = 9.sp)
                    )
                    drawText(
                        textLayoutResult = stopLayout,
                        topLeft = Offset(pos.x + 8f, pos.y - 12f)
                    )
                }
            }

            // 5. Draw Buses
            buses.forEach { bus ->
                val pos = project(bus.currentLat, bus.currentLng, w, h)
                val isSelected = (bus.id == selectedBusId || bus.id == highlightedBusId)

                val statusColor = when (bus.status) {
                    BusStatus.RUNNING -> StatusRunningGreen
                    BusStatus.AT_STOP -> StatusAtStopCyan
                    BusStatus.DELAYED -> StatusDelayedAmber
                    BusStatus.EMERGENCY -> StatusEmergencyRed
                    else -> StatusOfflineGray
                }

                // Selection glow halo
                if (isSelected) {
                    drawCircle(
                        color = statusColor.copy(alpha = 0.35f),
                        radius = 28f * scale,
                        center = pos
                    )
                    drawCircle(
                        color = statusColor,
                        radius = 28f * scale,
                        center = pos,
                        style = Stroke(width = 2.5f)
                    )
                }

                // Bus Marker Body
                drawCircle(
                    color = Color(0xFF0F172A),
                    radius = 16f * scale.coerceIn(0.8f, 1.6f),
                    center = pos
                )
                drawCircle(
                    color = statusColor,
                    radius = 16f * scale.coerceIn(0.8f, 1.6f),
                    center = pos,
                    style = Stroke(width = 3f)
                )

                // Heading arrow tip
                val rad = Math.toRadians(bus.headingDegrees.toDouble() - 90)
                val arrowTip = Offset(
                    pos.x + (22f * scale * cos(rad)).toFloat(),
                    pos.y + (22f * scale * sin(rad)).toFloat()
                )
                drawCircle(
                    color = statusColor,
                    radius = 4f,
                    center = arrowTip
                )

                // Bus Number text
                val busNumText = bus.busNumber.replace("Bus #", "#")
                val busLayout = textMeasurer.measure(
                    text = AnnotatedString(busNumText),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                drawText(
                    textLayoutResult = busLayout,
                    topLeft = Offset(pos.x - (busLayout.size.width / 2f), pos.y - (busLayout.size.height / 2f))
                )
            }
        }

        // Overlay Controls
        // Top HUD: Status & Simulator Toggle
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.75f),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isSimulating) StatusRunningGreen else StatusOfflineGray)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSimulating) "LIVE GPS DEMO (${simSpeed.toInt()}x)" else "GPS PAUSED",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Sim speed button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier.clickable {
                        val nextSpeed = when (simSpeed) {
                            1f -> 2f
                            2f -> 4f
                            else -> 1f
                        }
                        onChangeSimSpeed(nextSpeed)
                    }
                ) {
                    Text(
                        text = "${simSpeed.toInt()}x SPEED",
                        color = BrandCyanLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }

                // Pause/play simulation
                IconButton(
                    onClick = onToggleSimulation,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.75f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isSimulating) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Toggle Simulation",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Bottom right zoom buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(
                onClick = { scale = (scale * 1.25f).coerceAtMost(4f) },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.75f), CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color.White)
            }
            IconButton(
                onClick = { scale = (scale / 1.25f).coerceAtLeast(0.6f) },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.75f), CircleShape)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.White)
            }
            IconButton(
                onClick = {
                    scale = 1.0f
                    offsetX = 0f
                    offsetY = 0f
                },
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.75f), CircleShape)
            ) {
                Icon(Icons.Default.CenterFocusStrong, contentDescription = "Recenter", tint = BrandCyanLight)
            }
        }
    }
}
