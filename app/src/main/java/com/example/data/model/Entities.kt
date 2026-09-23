package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val label: String) {
    SUPER_ADMIN("Super Admin"),
    TRANSPORT_ADMIN("Transport Admin"),
    COORDINATOR("Bus Coordinator"),
    DRIVER("Bus Driver"),
    STUDENT("Student"),
    PARENT("Parent")
}

enum class BusStatus(val label: String) {
    NOT_STARTED("Not Started"),
    SCHEDULED("Scheduled"),
    RUNNING("Running"),
    AT_STOP("At Stop"),
    DELAYED("Delayed"),
    COMPLETED("Completed"),
    EMERGENCY("Emergency"),
    OFFLINE("Offline")
}

enum class AttendanceStatus(val label: String) {
    NOT_MARKED("Not Marked"),
    PRESENT("Present"),
    ABSENT("Absent"),
    ON_BUS("On Bus"),
    DROPPED_OFF("Dropped Off")
}

enum class VerificationMethod(val label: String) {
    QR_SCAN("QR Code"),
    GEOFENCE_AUTO("Geofence Detection"),
    MANUAL_COORDINATOR("Coordinator Manual"),
    STUDENT_CONFIRM("Student Self-Confirm")
}

enum class NotificationType {
    BUS_STARTED,
    APPROACHING_STOP,
    ARRIVED_AT_STOP,
    DELAYED,
    ROUTE_CHANGE,
    BUS_CANCELLED,
    EMERGENCY,
    STUDENT_BOARDED,
    STUDENT_DROPPED_OFF,
    GENERAL
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val phone: String,
    val registerNumber: String? = null,
    val department: String? = null,
    val assignedBusId: String? = null,
    val assignedRouteId: String? = null,
    val studentWardId: String? = null // For parents
)

@Entity(tableName = "buses")
data class BusEntity(
    @PrimaryKey val id: String, // e.g., "BUS-01"
    val busNumber: String, // e.g., "Bus #12"
    val registrationNumber: String, // e.g., "TN 37 BK 1001"
    val routeId: String,
    val routeName: String,
    val driverId: String,
    val driverName: String,
    val driverPhone: String,
    val coordinatorId: String,
    val coordinatorName: String,
    val coordinatorPhone: String,
    val capacity: Int = 50,
    val currentPassengers: Int = 0,
    val status: BusStatus = BusStatus.SCHEDULED,
    val currentLat: Double,
    val currentLng: Double,
    val speedKmh: Float = 0f,
    val headingDegrees: Float = 0f,
    val lastUpdated: Long = System.currentTimeMillis(),
    val currentStopId: String? = null,
    val currentStopName: String? = null,
    val nextStopId: String? = null,
    val nextStopName: String? = null,
    val etaMinutes: Int = 0,
    val maintenanceStatus: String = "Good",
    val emergencyContact: String = "+91 94878 12345",
    val isDemoGps: Boolean = true
)

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val id: String,
    val routeNumber: String, // "R-01"
    val routeName: String, // "Gandhipuram to Karunya Campus"
    val startLocation: String,
    val destination: String = "Karunya Nagar Campus",
    val totalDistanceKm: Float,
    val estimatedDurationMinutes: Int,
    val morningDepartureTime: String,
    val eveningReturnTime: String,
    val isActive: Boolean = true
)

@Entity(tableName = "stops")
data class StopEntity(
    @PrimaryKey val id: String,
    val routeId: String,
    val stopName: String,
    val latitude: Double,
    val longitude: Double,
    val sequenceNumber: Int,
    val expectedArrival: String, // "07:35 AM"
    val geofenceRadiusMeters: Int = 100
)

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val phone: String,
    val licenseNumber: String,
    val licenseExpiry: String,
    val assignedBusId: String,
    val assignedRouteName: String,
    val status: String = "Available",
    val experienceYears: Int = 8,
    val emergencyContact: String = "+91 98421 99999"
)

@Entity(tableName = "coordinators")
data class CoordinatorEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val employeeId: String,
    val department: String,
    val phone: String,
    val assignedBusId: String,
    val assignedRouteName: String,
    val email: String
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val registerNumber: String, // "URK22CS1045"
    val department: String, // "CSE"
    val year: String, // "3rd Year"
    val phone: String,
    val parentName: String,
    val parentPhone: String,
    val assignedBusId: String,
    val assignedRouteId: String,
    val boardingStopId: String,
    val boardingStopName: String,
    val dropOffStopId: String,
    val dropOffStopName: String,
    val qrCodePayload: String,
    val todayAttendanceStatus: AttendanceStatus = AttendanceStatus.NOT_MARKED,
    val boardingTimestamp: Long? = null,
    val dropOffTimestamp: Long? = null
)

@Entity(tableName = "attendance_records")
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val studentName: String,
    val registerNumber: String,
    val busId: String,
    val busNumber: String,
    val dateString: String, // "2026-09-22"
    val status: AttendanceStatus,
    val boardingTime: String? = null,
    val dropOffTime: String? = null,
    val boardingStopName: String? = null,
    val dropOffStopName: String? = null,
    val verifiedBy: String = "Coordinator",
    val verificationMethod: VerificationMethod = VerificationMethod.QR_SCAN,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey val id: String,
    val busId: String,
    val routeId: String,
    val routeName: String,
    val driverId: String,
    val coordinatorId: String,
    val tripType: String = "Morning Pickup", // or "Evening Drop"
    val date: String,
    val startTime: String? = null,
    val endTime: String? = null,
    val status: String = "Scheduled", // "In Progress", "Completed"
    val totalBoarded: Int = 0,
    val totalDropped: Int = 0
)

@Entity(tableName = "emergency_incidents")
data class EmergencyIncidentEntity(
    @PrimaryKey val id: String,
    val busId: String,
    val busNumber: String,
    val routeName: String,
    val driverName: String,
    val coordinatorName: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE", // "RESOLVED"
    val reason: String = "Panic SOS Pressed",
    val reportedByRole: UserRole = UserRole.DRIVER,
    val contactNumber: String,
    val resolutionNotes: String? = null
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val targetRole: UserRole? = null,
    val targetBusId: String? = null
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val performedBy: String,
    val userRole: UserRole,
    val entityType: String,
    val entityId: String,
    val previousValue: String,
    val newValue: String,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: String? = null
)
