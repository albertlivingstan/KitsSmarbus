package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.UserRole
import com.example.ui.components.AppTopBar
import com.example.ui.screens.*
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SmartBusViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SmartBusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(darkTheme = true) {
                val currentUser by viewModel.currentUser.collectAsState()
                val notifications by viewModel.allNotifications.collectAsState()
                val isAuthenticated by viewModel.isAuthenticated.collectAsState()

                if (!isAuthenticated) {
                    AuthScreen(
                        viewModel = viewModel,
                        authManager = viewModel.authManager,
                        onAuthSuccess = {}
                    )
                } else {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("app_root_scaffold"),
                        topBar = {
                            AppTopBar(
                                currentUser = currentUser,
                                onRoleSelected = { viewModel.switchUserRole(it) },
                                notifications = notifications,
                                onNotificationClick = { viewModel.markNotificationAsRead(it) },
                                onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
                                onSignOut = { viewModel.signOut() }
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                modifier = Modifier.testTag("role_bottom_nav"),
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp
                            ) {
                                val items = listOf(
                                    Triple(UserRole.TRANSPORT_ADMIN, "Admin", Icons.Default.AdminPanelSettings),
                                    Triple(UserRole.COORDINATOR, "Coord", Icons.Default.FactCheck),
                                    Triple(UserRole.DRIVER, "Driver", Icons.Default.SportsMotorsports),
                                    Triple(UserRole.STUDENT, "Student", Icons.Default.School),
                                    Triple(UserRole.PARENT, "Parent", Icons.Default.FamilyRestroom)
                                )

                                items.forEach { (role, label, icon) ->
                                    val isSelected = (currentUser.role == role)
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { viewModel.switchUserRole(role) },
                                        icon = {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = label,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        label = { Text(label) },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = BrandCyan.copy(alpha = 0.25f)
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentUser.role) {
                                UserRole.SUPER_ADMIN, UserRole.TRANSPORT_ADMIN -> {
                                    AdminMainScreen(viewModel = viewModel)
                                }
                                UserRole.COORDINATOR -> {
                                    CoordinatorMainScreen(viewModel = viewModel)
                                }
                                UserRole.DRIVER -> {
                                    DriverMainScreen(viewModel = viewModel)
                                }
                                UserRole.STUDENT -> {
                                    StudentMainScreen(viewModel = viewModel)
                                }
                                UserRole.PARENT -> {
                                    ParentMainScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
