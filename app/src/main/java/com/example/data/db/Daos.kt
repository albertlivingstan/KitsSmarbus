package com.example.data.db

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BusDao {
    @Query("SELECT * FROM buses ORDER BY busNumber ASC")
    fun getAllBuses(): Flow<List<BusEntity>>

    @Query("SELECT * FROM buses WHERE id = :busId")
    fun getBusById(busId: String): Flow<BusEntity?>

    @Query("SELECT * FROM buses WHERE id = :busId")
    suspend fun getBusByIdOnce(busId: String): BusEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBuses(buses: List<BusEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBus(bus: BusEntity)

    @Query("UPDATE buses SET currentLat = :lat, currentLng = :lng, speedKmh = :speed, headingDegrees = :heading, lastUpdated = :timestamp, status = :status, currentPassengers = :passengers, currentStopId = :currentStopId, currentStopName = :currentStopName, nextStopId = :nextStopId, nextStopName = :nextStopName, etaMinutes = :eta WHERE id = :busId")
    suspend fun updateBusTelemetry(
        busId: String,
        lat: Double,
        lng: Double,
        speed: Float,
        heading: Float,
        timestamp: Long,
        status: BusStatus,
        passengers: Int,
        currentStopId: String?,
        currentStopName: String?,
        nextStopId: String?,
        nextStopName: String?,
        eta: Int
    )

    @Query("UPDATE buses SET status = :status WHERE id = :busId")
    suspend fun updateBusStatus(busId: String, status: BusStatus)

    @Query("DELETE FROM buses WHERE id = :busId")
    suspend fun deleteBus(busId: String)
}

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes ORDER BY routeNumber ASC")
    fun getAllRoutes(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE id = :routeId")
    suspend fun getRouteById(routeId: String): RouteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRoutes(routes: List<RouteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRoute(route: RouteEntity)

    @Query("DELETE FROM routes WHERE id = :routeId")
    suspend fun deleteRoute(routeId: String)
}

@Dao
interface StopDao {
    @Query("SELECT * FROM stops WHERE routeId = :routeId ORDER BY sequenceNumber ASC")
    fun getStopsForRoute(routeId: String): Flow<List<StopEntity>>

    @Query("SELECT * FROM stops WHERE routeId = :routeId ORDER BY sequenceNumber ASC")
    suspend fun getStopsForRouteOnce(routeId: String): List<StopEntity>

    @Query("SELECT * FROM stops ORDER BY sequenceNumber ASC")
    fun getAllStops(): Flow<List<StopEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStops(stops: List<StopEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStop(stop: StopEntity)

    @Query("DELETE FROM stops WHERE id = :stopId")
    suspend fun deleteStop(stopId: String)
}

@Dao
interface DriverDao {
    @Query("SELECT * FROM drivers ORDER BY fullName ASC")
    fun getAllDrivers(): Flow<List<DriverEntity>>

    @Query("SELECT * FROM drivers WHERE id = :driverId")
    suspend fun getDriverById(driverId: String): DriverEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDrivers(drivers: List<DriverEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDriver(driver: DriverEntity)

    @Query("DELETE FROM drivers WHERE id = :driverId")
    suspend fun deleteDriver(driverId: String)
}

@Dao
interface CoordinatorDao {
    @Query("SELECT * FROM coordinators ORDER BY fullName ASC")
    fun getAllCoordinators(): Flow<List<CoordinatorEntity>>

    @Query("SELECT * FROM coordinators WHERE id = :coordinatorId")
    suspend fun getCoordinatorById(coordinatorId: String): CoordinatorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCoordinators(coordinators: List<CoordinatorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCoordinator(coordinator: CoordinatorEntity)

    @Query("DELETE FROM coordinators WHERE id = :coordinatorId")
    suspend fun deleteCoordinator(coordinatorId: String)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY fullName ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE assignedBusId = :busId ORDER BY fullName ASC")
    fun getStudentsForBus(busId: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE assignedBusId = :busId ORDER BY fullName ASC")
    suspend fun getStudentsForBusOnce(busId: String): List<StudentEntity>

    @Query("SELECT * FROM students WHERE id = :studentId")
    fun getStudentById(studentId: String): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE registerNumber = :regNumber LIMIT 1")
    suspend fun getStudentByRegisterNumber(regNumber: String): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStudents(students: List<StudentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStudent(student: StudentEntity)

    @Query("UPDATE students SET todayAttendanceStatus = :status, boardingTimestamp = :boardTime, dropOffTimestamp = :dropTime WHERE id = :studentId")
    suspend fun updateStudentAttendance(
        studentId: String,
        status: AttendanceStatus,
        boardTime: Long?,
        dropTime: Long?
    )

    @Query("DELETE FROM students WHERE id = :studentId")
    suspend fun deleteStudent(studentId: String)
}

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records WHERE dateString = :date ORDER BY timestamp DESC")
    fun getRecordsForDate(date: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records WHERE busId = :busId AND dateString = :date ORDER BY timestamp DESC")
    fun getRecordsForBusAndDate(busId: String, date: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getRecordsForStudent(studentId: String): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AttendanceRecordEntity): Long

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId AND dateString = :date LIMIT 1")
    suspend fun getTodayRecordForStudent(studentId: String, date: String): AttendanceRecordEntity?

    @Update
    suspend fun updateRecord(record: AttendanceRecordEntity)

    @Query("DELETE FROM attendance_records")
    suspend fun clearAll()
}

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY date DESC, id DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE busId = :busId ORDER BY date DESC LIMIT 1")
    suspend fun getLatestTripForBus(busId: String): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTrip(trip: TripEntity)

    @Query("UPDATE trips SET status = :status, endTime = :endTime, totalBoarded = :boarded, totalDropped = :dropped WHERE id = :tripId")
    suspend fun updateTripStatus(tripId: String, status: String, endTime: String?, boarded: Int, dropped: Int)
}

@Dao
interface EmergencyDao {
    @Query("SELECT * FROM emergency_incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<EmergencyIncidentEntity>>

    @Query("SELECT * FROM emergency_incidents WHERE status = 'ACTIVE' ORDER BY timestamp DESC")
    fun getActiveIncidents(): Flow<List<EmergencyIncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: EmergencyIncidentEntity)

    @Query("UPDATE emergency_incidents SET status = 'RESOLVED', resolutionNotes = :notes WHERE id = :incidentId")
    suspend fun resolveIncident(incidentId: String, notes: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 200")
    fun getRecentAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)
}
