package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.FirebaseAuthManager
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.SmartBusViewModel

@Composable
fun AuthScreen(
    viewModel: SmartBusViewModel,
    authManager: FirebaseAuthManager,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("samuel.cs@karunya.edu.in") }
    var password by remember { mutableStateOf("Karunya@123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val detectedRole = remember(email) {
        authManager.determineRole(email)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(20.dp)
            .testTag("auth_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Logo & Header
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(BrandCyan),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Karunya SmartBus",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "KITS Coimbatore • Transit Authentication",
                style = MaterialTheme.typography.bodyMedium,
                color = BrandCyanLight
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Domain Role Matching Information Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Karunya Domain Role Rules:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BrandCyanLight,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Student: @karunya.edu.in\n• Coordinator: @karunya.edu\n• Bus Driver: any @gmail.com\n• Admin: admin@karunya.edu / transport@",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Login Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Sign In to Your Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("University / Driver Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = BrandCyanLight)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Dynamic Detected Role Badge
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = when (detectedRole) {
                            UserRole.STUDENT -> BrandCyan.copy(alpha = 0.2f)
                            UserRole.COORDINATOR -> BrandAccent.copy(alpha = 0.2f)
                            UserRole.DRIVER -> StatusRunningGreen.copy(alpha = 0.2f)
                            UserRole.TRANSPORT_ADMIN, UserRole.SUPER_ADMIN -> StatusDelayedAmber.copy(alpha = 0.2f)
                            else -> Color(0xFF334155)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (detectedRole) {
                                    UserRole.STUDENT -> Icons.Default.School
                                    UserRole.COORDINATOR -> Icons.Default.FactCheck
                                    UserRole.DRIVER -> Icons.Default.SportsMotorsports
                                    UserRole.TRANSPORT_ADMIN, UserRole.SUPER_ADMIN -> Icons.Default.AdminPanelSettings
                                    else -> Icons.Default.Person
                                },
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = when (detectedRole) {
                                    UserRole.STUDENT -> BrandCyanLight
                                    UserRole.COORDINATOR -> Color(0xFFA78BFA)
                                    UserRole.DRIVER -> StatusRunningGreen
                                    UserRole.TRANSPORT_ADMIN, UserRole.SUPER_ADMIN -> StatusDelayedAmber
                                    else -> Color.White
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Detected Role: ${detectedRole.label}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = BrandCyanLight)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input"),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage!!,
                            color = StatusEmergencyRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Sign In Button
                    Button(
                        onClick = {
                            if (email.isBlank()) {
                                errorMessage = "Please enter your email."
                                return@Button
                            }
                            isLoading = true
                            errorMessage = null
                            viewModel.signInWithEmail(email, password) { success, error ->
                                isLoading = false
                                if (success) {
                                    onAuthSuccess()
                                } else {
                                    errorMessage = error ?: "Authentication failed."
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("sign_in_submit_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCyan),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "SIGN IN AS ${detectedRole.label.uppercase()}",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Continue with Google Button
                    OutlinedButton(
                        onClick = {
                            isLoading = true
                            errorMessage = null
                            viewModel.signInWithGoogle(context) { success, error ->
                                isLoading = false
                                if (success) {
                                    onAuthSuccess()
                                } else {
                                    errorMessage = error ?: "Google Sign-In failed."
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("sign_in_google_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sign in with Google",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Quick Role Preset Selectors (for demo and rapid evaluation)
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "⚡ QUICK DEMO CREDENTIALS (Tap to Autofill):",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Student preset
                    OutlinedButton(
                        onClick = {
                            email = "samuel.cs@karunya.edu.in"
                            password = "StudentPass@123"
                        },
                        modifier = Modifier.weight(1f).testTag("quick_fill_student"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎓 Student", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("@karunya.edu.in", fontSize = 9.sp, color = BrandCyanLight)
                        }
                    }

                    // Coordinator preset
                    OutlinedButton(
                        onClick = {
                            email = "johnpeter@karunya.edu"
                            password = "CoordPass@123"
                        },
                        modifier = Modifier.weight(1f).testTag("quick_fill_coordinator"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📋 Coordinator", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("@karunya.edu", fontSize = 9.sp, color = BrandAccent)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Driver preset
                    OutlinedButton(
                        onClick = {
                            email = "murugan.driver@gmail.com"
                            password = "DriverPass@123"
                        },
                        modifier = Modifier.weight(1f).testTag("quick_fill_driver"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🚌 Driver", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("any @gmail.com", fontSize = 9.sp, color = StatusRunningGreen)
                        }
                    }

                    // Admin preset
                    OutlinedButton(
                        onClick = {
                            email = "transport@karunya.edu"
                            password = "AdminPass@123"
                        },
                        modifier = Modifier.weight(1f).testTag("quick_fill_admin"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🛡️ Admin", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("transport@", fontSize = 9.sp, color = StatusDelayedAmber)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
