package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmartBusRepository(private val db: AppDatabase) {

    private val busDao = db.busDao()
    private val routeDao = db.routeDao()
    private val stopDao = db.stopDao()
    private val driverDao = db.driverDao()
    private val coordinatorDao = db.coordinatorDao()
    private val studentDao = db.studentDao()
    private val attendanceDao = db.attendanceDao()
    private val tripDao = db.tripDao()
    private val emergencyDao = db.emergencyDao()
    private val notificationDao = db.notificationDao()
    private val auditDao = db.auditDao()
    private val userDao = db.userDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfEmpty()
        }
    }

    suspend fun seedDatabaseIfEmpty() {
        val existingBuses = busDao.getAllBuses().first()
        if (existingBuses.isEmpty()) {
            routeDao.insertOrUpdateRoutes(DemoDataSeed.routes)
            stopDao.insertOrUpdateStops(DemoDataSeed.stops)
            driverDao.insertOrUpdateDrivers(DemoDataSeed.drivers)
            coordinatorDao.insertOrUpdateCoordinators(DemoDataSeed.coordinators)
            busDao.insertOrUpdateBuses(DemoDataSeed.buses)
            studentDao.insertOrUpdateStudents(DemoDataSeed.students)
            userDao.insertOrUpdateUsers(DemoDataSeed.users)
            DemoDataSeed.notifications.forEach { notificationDao.insertNotification(it) }
            DemoDataSeed.sampleIncidents.forEach { emergencyDao.insertIncident(it) }
            DemoDataSeed.auditLogs.forEach { auditDao.insertAuditLog(it) }

            // Pre-seed some attendance records
            val todayStr = getTodayDateString()
            DemoDataSeed.students.take(8).forEach { student ->
                if (student.todayAttendanceStatus != AttendanceStatus.NOT_MARKED) {
                    attendanceDao.insertRecord(
                        AttendanceRecordEntity(
                            studentId = student.id,
                            studentName = student.fullName,
                            registerNumber = student.registerNumber,
                            busId = student.assignedBusId,
                            busNumber = "Bus #12",
                            dateString = todayStr,
                            status = student.todayAttendanceStatus,
                            boardingTime = if (student.todayAttendanceStatus == AttendanceStatus.ON_BUS) "07:35 AM" else null,
                            boardingStopName = student.boardingStopName,
                            verifiedBy = "Dr. John Peter",
                            verificationMethod = VerificationMethod.QR_SCAN
                        )
                    )
                }
            }
        }
    }

    // Flows
    val allBuses: Flow<List<BusEntity>> = busDao.getAllBuses()
    val allRoutes: Flow<List<RouteEntity>> = routeDao.getAllRoutes()
    val allStops: Flow<List<StopEntity>> = stopDao.getAllStops()
    val allDrivers: Flow<List<DriverEntity>> = driverDao.getAllDrivers()
    val allCoordinators: Flow<List<CoordinatorEntity>> = coordinatorDao.getAllCoordinators()
    val allStudents: Flow<List<StudentEntity>> = studentDao.getAllStudents()
    val allIncidents: Flow<List<EmergencyIncidentEntity>> = emergencyDao.getAllIncidents()
    val activeIncidents: Flow<List<EmergencyIncidentEntity>> = emergencyDao.getActiveIncidents()
    val allNotifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    val recentAuditLogs: Flow<List<AuditLogEntity>> = auditDao.getRecentAuditLogs()
    val allTrips: Flow<List<TripEntity>> = tripDao.getAllTrips()

    fun getBusById(busId: String): Flow<BusEntity?> = busDao.getBusById(busId)
    fun getStopsForRoute(routeId: String): Flow<List<StopEntity>> = stopDao.getStopsForRoute(routeId)
    suspend fun getStopsForRouteOnce(routeId: String): List<StopEntity> = stopDao.getStopsForRouteOnce(routeId)
    fun getStudentsForBus(busId: String): Flow<List<StudentEntity>> = studentDao.getStudentsForBus(busId)
    fun getStudentById(studentId: String): Flow<StudentEntity?> = studentDao.getStudentById(studentId)
    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecordEntity>> = attendanceDao.getRecordsForDate(date)
    fun getAttendanceForBus(busId: String, date: String): Flow<List<AttendanceRecordEntity>> = attendanceDao.getRecordsForBusAndDate(busId, date)
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceRecordEntity>> = attendanceDao.getRecordsForStudent(studentId)

    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email)
    suspend fun getUserById(userId: String): UserEntity? = userDao.getUserById(userId)

    suspend fun updateBusTelemetry(
        busId: String,
        lat: Double,
        lng: Double,
        speed: Float,
        heading: Float,
        status: BusStatus,
        passengers: Int,
        currentStopId: String?,
        currentStopName: String?,
        nextStopId: String?,
        nextStopName: String?,
        etaMinutes: Int
    ) = withContext(Dispatchers.IO) {
        busDao.updateBusTelemetry(
            busId = busId,
            lat = lat,
            lng = lng,
            speed = speed,
            heading = heading,
            timestamp = System.currentTimeMillis(),
            status = status,
            passengers = passengers,
            currentStopId = currentStopId,
            currentStopName = currentStopName,
            nextStopId = nextStopId,
            nextStopName = nextStopName,
            eta = etaMinutes
        )
    }

    suspend fun markAttendance(
        studentId: String,
        newStatus: AttendanceStatus,
        method: VerificationMethod,
        performedByName: String,
        performedByRole: UserRole,
        reason: String? = null
    ) = withContext(Dispatchers.IO) {
        val student = studentDao.getStudentById(studentId).first() ?: return@withContext
        val bus = busDao.getBusByIdOnce(student.assignedBusId)
        val prevStatus = student.todayAttendanceStatus

        val currentTimeMillis = System.currentTimeMillis()
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val timeString = timeFormat.format(Date(currentTimeMillis))
        val todayStr = getTodayDateString()

        val boardTime = if (newStatus == AttendanceStatus.ON_BUS) currentTimeMillis else student.boardingTimestamp
        val dropTime = if (newStatus == AttendanceStatus.DROPPED_OFF) currentTimeMillis else student.dropOffTimestamp

        studentDao.updateStudentAttendance(studentId, newStatus, boardTime, dropTime)

        // Save attendance record
        attendanceDao.insertRecord(
            AttendanceRecordEntity(
                studentId = student.id,
                studentName = student.fullName,
                registerNumber = student.registerNumber,
                busId = student.assignedBusId,
                busNumber = bus?.busNumber ?: "Bus",
                dateString = todayStr,
                status = newStatus,
                boardingTime = if (newStatus == AttendanceStatus.ON_BUS) timeString else null,
                dropOffTime = if (newStatus == AttendanceStatus.DROPPED_OFF) timeString else null,
                boardingStopName = student.boardingStopName,
                dropOffStopName = student.dropOffStopName,
                verifiedBy = performedByName,
                verificationMethod = method,
                timestamp = currentTimeMillis
            )
        )

        // Log audit
        auditDao.insertAuditLog(
            AuditLogEntity(
                action = "ATTENDANCE_STATUS_UPDATE",
                performedBy = performedByName,
                userRole = performedByRole,
                entityType = "Student",
                entityId = student.registerNumber,
                previousValue = prevStatus.name,
                newValue = newStatus.name,
                reason = reason ?: "Verified via ${method.label}"
            )
        )

        // Generate push notification for student / parent
        val notifTitle = when (newStatus) {
            AttendanceStatus.ON_BUS -> "${student.fullName} Boarded ${bus?.busNumber ?: "Bus"}"
            AttendanceStatus.DROPPED_OFF -> "${student.fullName} Reached Campus Safely"
            AttendanceStatus.ABSENT -> "Attendance Alert: ${student.fullName} Marked Absent"
            else -> "Attendance Update for ${student.fullName}"
        }
        val notifMsg = when (newStatus) {
            AttendanceStatus.ON_BUS -> "Boarded at ${student.boardingStopName} ($timeString) via ${method.label}."
            AttendanceStatus.DROPPED_OFF -> "Alighted at ${student.dropOffStopName} at $timeString."
            AttendanceStatus.ABSENT -> "Marked absent for morning transit by coordinator."
            else -> "Status changed to ${newStatus.label}."
        }
        notificationDao.insertNotification(
            NotificationEntity(
                id = "NOTIF-${System.currentTimeMillis()}-${(100..999).random()}",
                title = notifTitle,
                message = notifMsg,
                type = when (newStatus) {
                    AttendanceStatus.ON_BUS -> NotificationType.STUDENT_BOARDED
                    AttendanceStatus.DROPPED_OFF -> NotificationType.STUDENT_DROPPED_OFF
                    else -> NotificationType.GENERAL
                },
                targetBusId = student.assignedBusId
            )
        )

        // Update bus passenger count
        bus?.let { b ->
            val updatedStudents = studentDao.getStudentsForBusOnce(b.id)
            val currentOnboard = updatedStudents.count { it.todayAttendanceStatus == AttendanceStatus.ON_BUS }
            busDao.updateBusTelemetry(
                busId = b.id,
                lat = b.currentLat,
                lng = b.currentLng,
                speed = b.speedKmh,
                heading = b.headingDegrees,
                timestamp = System.currentTimeMillis(),
                status = b.status,
                passengers = currentOnboard,
                currentStopId = b.currentStopId,
                currentStopName = b.currentStopName,
                nextStopId = b.nextStopId,
                nextStopName = b.nextStopName,
                eta = b.etaMinutes
            )
        }
    }

    suspend fun raiseEmergency(
        busId: String,
        reason: String,
        reportedByRole: UserRole,
        reporterName: String,
        contactNumber: String
    ) = withContext(Dispatchers.IO) {
        val bus = busDao.getBusByIdOnce(busId)
        val incidentId = "INC-${System.currentTimeMillis()}"
        val incident = EmergencyIncidentEntity(
            id = incidentId,
            busId = busId,
            busNumber = bus?.busNumber ?: busId,
            routeName = bus?.routeName ?: "Transit Route",
            driverName = bus?.driverName ?: "Driver",
            coordinatorName = bus?.coordinatorName ?: "Coordinator",
            latitude = bus?.currentLat ?: 10.9360,
            longitude = bus?.currentLng ?: 76.7440,
            status = "ACTIVE",
            reason = reason,
            reportedByRole = reportedByRole,
            contactNumber = contactNumber
        )
        emergencyDao.insertIncident(incident)
        busDao.updateBusStatus(busId, BusStatus.EMERGENCY)

        notificationDao.insertNotification(
            NotificationEntity(
                id = "EMERG-${System.currentTimeMillis()}",
                title = "🚨 EMERGENCY SOS: ${bus?.busNumber ?: busId}",
                message = "Emergency reported by $reporterName ($reportedByRole): $reason. Location: Lat ${bus?.currentLat}, Lng ${bus?.currentLng}. Contact: $contactNumber",
                type = NotificationType.EMERGENCY,
                targetBusId = busId
            )
        )

        auditDao.insertAuditLog(
            AuditLogEntity(
                action = "EMERGENCY_TRIGGERED",
                performedBy = reporterName,
                userRole = reportedByRole,
                entityType = "EmergencyIncident",
                entityId = incidentId,
                previousValue = "NORMAL",
                newValue = "EMERGENCY",
                reason = reason
            )
        )
    }

    suspend fun resolveEmergency(incidentId: String, notes: String, adminName: String) = withContext(Dispatchers.IO) {
        emergencyDao.resolveIncident(incidentId, notes)
        auditDao.insertAuditLog(
            AuditLogEntity(
                action = "EMERGENCY_RESOLVED",
                performedBy = adminName,
                userRole = UserRole.TRANSPORT_ADMIN,
                entityType = "EmergencyIncident",
                entityId = incidentId,
                previousValue = "ACTIVE",
                newValue = "RESOLVED",
                reason = notes
            )
        )
    }

    suspend fun startTrip(busId: String, driverName: String) = withContext(Dispatchers.IO) {
        val bus = busDao.getBusByIdOnce(busId) ?: return@withContext
        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val tripId = "TRIP-${busId}-${System.currentTimeMillis()}"
        val trip = TripEntity(
            id = tripId,
            busId = busId,
            routeId = bus.routeId,
            routeName = bus.routeName,
            driverId = bus.driverId,
            coordinatorId = bus.coordinatorId,
            tripType = "Morning Campus Transit",
            date = getTodayDateString(),
            startTime = timeStr,
            status = "In Progress"
        )
        tripDao.insertOrUpdateTrip(trip)
        busDao.updateBusStatus(busId, BusStatus.RUNNING)

        notificationDao.insertNotification(
            NotificationEntity(
                id = "NOTIF-${System.currentTimeMillis()}",
                title = "${bus.busNumber} Trip Started",
                message = "${bus.busNumber} started its morning run along ${bus.routeName} at $timeStr.",
                type = NotificationType.BUS_STARTED,
                targetBusId = busId
            )
        )
    }

    suspend fun endTrip(busId: String) = withContext(Dispatchers.IO) {
        val bus = busDao.getBusByIdOnce(busId) ?: return@withContext
        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val latestTrip = tripDao.getLatestTripForBus(busId)
        if (latestTrip != null) {
            tripDao.updateTripStatus(
                latestTrip.id,
                "Completed",
                timeStr,
                boarded = bus.currentPassengers,
                dropped = bus.currentPassengers
            )
        }
        busDao.updateBusStatus(busId, BusStatus.COMPLETED)
    }

    suspend fun insertOrUpdateBus(bus: BusEntity) = withContext(Dispatchers.IO) {
        busDao.insertOrUpdateBus(bus)
    }

    suspend fun deleteBus(busId: String) = withContext(Dispatchers.IO) {
        busDao.deleteBus(busId)
    }

    suspend fun insertOrUpdateRoute(route: RouteEntity) = withContext(Dispatchers.IO) {
        routeDao.insertOrUpdateRoute(route)
    }

    suspend fun deleteRoute(routeId: String) = withContext(Dispatchers.IO) {
        routeDao.deleteRoute(routeId)
    }

    suspend fun insertOrUpdateStop(stop: StopEntity) = withContext(Dispatchers.IO) {
        stopDao.insertOrUpdateStop(stop)
    }

    suspend fun insertOrUpdateStudent(student: StudentEntity) = withContext(Dispatchers.IO) {
        studentDao.insertOrUpdateStudent(student)
    }

    suspend fun deleteStudent(studentId: String) = withContext(Dispatchers.IO) {
        studentDao.deleteStudent(studentId)
    }

    suspend fun insertOrUpdateDriver(driver: DriverEntity) = withContext(Dispatchers.IO) {
        driverDao.insertOrUpdateDriver(driver)
    }

    suspend fun insertOrUpdateCoordinator(coordinator: CoordinatorEntity) = withContext(Dispatchers.IO) {
        coordinatorDao.insertOrUpdateCoordinator(coordinator)
    }

    suspend fun markNotificationAsRead(id: String) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    suspend fun generateAttendanceCsv(busId: String? = null): String = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val records = if (busId != null) {
            attendanceDao.getRecordsForBusAndDate(busId, today).first()
        } else {
            attendanceDao.getRecordsForDate(today).first()
        }

        val sb = StringBuilder()
        sb.append("Record ID,Date,Register No,Student Name,Bus,Status,Boarding Time,Drop-off Time,Boarding Stop,Drop-off Stop,Verified By,Method\n")
        records.forEach { r ->
            sb.append("${r.id},\"${r.dateString}\",\"${r.registerNumber}\",\"${r.studentName}\",\"${r.busNumber}\",\"${r.status.label}\",\"${r.boardingTime ?: "--"}\",\"${r.dropOffTime ?: "--"}\",\"${r.boardingStopName ?: "--"}\",\"${r.dropOffStopName ?: "--"}\",\"${r.verifiedBy}\",\"${r.verificationMethod.label}\"\n")
        }
        sb.toString()
    }

    companion object {
        fun getTodayDateString(): String {
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        }
    }
}
