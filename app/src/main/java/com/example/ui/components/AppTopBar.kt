package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationEntity
import com.example.data.model.UserEntity
import com.example.data.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentUser: UserEntity,
    onRoleSelected: (UserRole) -> Unit,
    notifications: List<NotificationEntity>,
    onNotificationClick: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onSignOut: () -> Unit
) {
    var showRoleMenu by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    val unreadCount = notifications.count { !it.isRead }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = "Karunya SmartBus",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Karunya SmartBus",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "KITS Coimbatore",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandCyanLight,
                        fontSize = 10.sp
                    )
                }
            }
        },
        actions = {
            // Role switcher pill button
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { showRoleMenu = true }
                    .testTag("role_switcher_button"),
                color = BrandBluePrimary.copy(alpha = 0.5f),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (currentUser.role) {
                            UserRole.SUPER_ADMIN, UserRole.TRANSPORT_ADMIN -> Icons.Default.AdminPanelSettings
                            UserRole.COORDINATOR -> Icons.Default.FactCheck
                            UserRole.DRIVER -> Icons.Default.SportsMotorsports
                            UserRole.STUDENT -> Icons.Default.School
                            UserRole.PARENT -> Icons.Default.FamilyRestroom
                        },
                        contentDescription = null,
                        tint = BrandCyanLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = currentUser.role.label.split(" ").take(2).joinToString(" "),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Role Dropdown Menu
            DropdownMenu(
                expanded = showRoleMenu,
                onDismissRequest = { showRoleMenu = false }
            ) {
                listOf(
                    UserRole.TRANSPORT_ADMIN,
                    UserRole.COORDINATOR,
                    UserRole.DRIVER,
                    UserRole.STUDENT,
                    UserRole.PARENT
                ).forEach { role ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(role.label, fontWeight = FontWeight.Bold)
                                Text(
                                    when (role) {
                                        UserRole.TRANSPORT_ADMIN -> "Fleet control, dispatch, routes & emergency center"
                                        UserRole.COORDINATOR -> "Live attendance, student roster, QR scan pass"
                                        UserRole.DRIVER -> "Cockpit HUD, start trip, speed, next stop"
                                        UserRole.STUDENT -> "Live tracking, ETA, digital QR pass, arrival check-in"
                                        UserRole.PARENT -> "Ward safe transit updates and live location"
                                        else -> ""
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            showRoleMenu = false
                            onRoleSelected(role)
                        }
                    )
                }
                HorizontalDivider()
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(currentUser.email, fontSize = 11.sp, color = BrandCyanLight)
                            Text("Sign Out", fontWeight = FontWeight.Bold, color = StatusEmergencyRed)
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = StatusEmergencyRed)
                    },
                    onClick = {
                        showRoleMenu = false
                        onSignOut()
                    }
                )
            }

            // Notifications Bell
            Box(modifier = Modifier.padding(start = 2.dp, end = 2.dp)) {
                IconButton(
                    onClick = { showNotificationDialog = true },
                    modifier = Modifier.testTag("notifications_bell_button")
                ) {
                    BadgedBox(badge = {
                        if (unreadCount > 0) {
                            Badge(containerColor = StatusEmergencyRed) {
                                Text("$unreadCount")
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                }
            }

            // Quick Logout button
            IconButton(
                onClick = onSignOut,
                modifier = Modifier.testTag("topbar_logout_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Sign Out",
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF091026),
            titleContentColor = Color.White
        )
    )

    // Notifications Dialog
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Notifications (${notifications.size})", fontWeight = FontWeight.Bold)
                    TextButton(onClick = onMarkAllRead) {
                        Text("Mark all read", fontSize = 11.sp)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    if (notifications.isEmpty()) {
                        Text("No recent notifications.")
                    } else {
                        notifications.forEach { notif ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onNotificationClick(notif.id) },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (!notif.isRead) BrandBluePrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = notif.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (!notif.isRead) BrandCyanLight else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = notif.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                OutlinedButton(onClick = { showNotificationDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
