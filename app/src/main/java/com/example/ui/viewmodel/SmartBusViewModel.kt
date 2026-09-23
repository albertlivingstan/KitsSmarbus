package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.FirebaseAuthManager
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.DemoDataSeed
import com.example.data.repository.SmartBusRepository
import com.example.data.simulator.GpsSimulatorEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SmartBusViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = SmartBusRepository(db)
    val simulator = GpsSimulatorEngine(repository, viewModelScope)
    val authManager = FirebaseAuthManager()

    // Authentication state
    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    // Current active user / role
    private val _currentUser = MutableStateFlow<UserEntity>(DemoDataSeed.users[0]) // Default Transport Admin
    val currentUser: StateFlow<UserEntity> = _currentUser.asStateFlow()

    // Navigation and Selection state
    private val _selectedBus = MutableStateFlow<BusEntity?>(null)
    val selectedBus: StateFlow<BusEntity?> = _selectedBus.asStateFlow()

    private val _selectedStop = MutableStateFlow<StopEntity?>(null)
    val selectedStop: StateFlow<StopEntity?> = _selectedStop.asStateFlow()

    // Student Self-Confirmation State (when bus enters student's stop geofence)
    private val _studentStopArrivalPrompt = MutableStateFlow<Pair<BusEntity, StopEntity>?>(null)
    val studentStopArrivalPrompt: StateFlow<Pair<BusEntity, StopEntity>?> = _studentStopArrivalPrompt.asStateFlow()

    // Filters & Search
    private val _studentSearchQuery = MutableStateFlow("")
    val studentSearchQuery: StateFlow<String> = _studentSearchQuery.asStateFlow()

    private val _busFilterQuery = MutableStateFlow("")
    val busFilterQuery: StateFlow<String> = _busFilterQuery.asStateFlow()

    // Simulation states
    val isSimulating = simulator.isSimulating
    val simSpeed = simulator.simSpeedMultiplier

    // Reactive streams from repository
    val allBuses: StateFlow<List<BusEntity>> = repository.allBuses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRoutes: StateFlow<List<RouteEntity>> = repository.allRoutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStops: StateFlow<List<StopEntity>> = repository.allStops
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDrivers: StateFlow<List<DriverEntity>> = repository.allDrivers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCoordinators: StateFlow<List<CoordinatorEntity>> = repository.allCoordinators
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeIncidents: StateFlow<List<EmergencyIncidentEntity>> = repository.activeIncidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allIncidents: StateFlow<List<EmergencyIncidentEntity>> = repository.allIncidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentAuditLogs: StateFlow<List<AuditLogEntity>> = repository.recentAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTrips: StateFlow<List<TripEntity>> = repository.allTrips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayAttendanceRecords: StateFlow<List<AttendanceRecordEntity>> = repository
        .getAttendanceForDate(SmartBusRepository.getTodayDateString())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Automatically check if bus arrives near student's stop for self-confirmation prompt
        viewModelScope.launch {
            combine(allBuses, _currentUser, allStops) { buses, user, stops ->
                if (user.role == UserRole.STUDENT && user.assignedBusId != null) {
                    val bus = buses.find { it.id == user.assignedBusId }
                    if (bus != null && bus.status == BusStatus.AT_STOP) {
                        val currentStop = stops.find { it.id == bus.currentStopId }
                        if (currentStop != null) {
                            Pair(bus, currentStop)
                        } else null
                    } else null
                } else null
            }.collect { prompt ->
                _studentStopArrivalPrompt.value = prompt
            }
        }
    }

    // Role switcher
    fun switchUserRole(role: UserRole) {
        val user = DemoDataSeed.users.find { it.role == role } ?: DemoDataSeed.users[0]
        _currentUser.value = user
    }

    fun selectBus(bus: BusEntity?) {
        _selectedBus.value = bus
    }

    fun selectStop(stop: StopEntity?) {
        _selectedStop.value = stop
    }

    fun setStudentSearchQuery(q: String) {
        _studentSearchQuery.value = q
    }

    fun setBusFilterQuery(q: String) {
        _busFilterQuery.value = q
    }

    fun toggleSimulation() {
        simulator.toggleSimulation()
    }

    fun setSimSpeed(speed: Float) {
        simulator.setSpeedMultiplier(speed)
    }

    // Attendance actions
    fun markAttendance(
        studentId: String,
        status: AttendanceStatus,
        method: VerificationMethod = VerificationMethod.MANUAL_COORDINATOR,
        reason: String? = null
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.markAttendance(
                studentId = studentId,
                newStatus = status,
                method = method,
                performedByName = user.name,
                performedByRole = user.role,
                reason = reason
            )
        }
    }

    fun studentSelfConfirmBoarding(studentId: String) {
        markAttendance(
            studentId = studentId,
            status = AttendanceStatus.ON_BUS,
            method = VerificationMethod.STUDENT_CONFIRM,
            reason = "Student confirmed boarding at stop during arrival window"
        )
        _studentStopArrivalPrompt.value = null
    }

    fun studentSelfConfirmDropOff(studentId: String) {
        markAttendance(
            studentId = studentId,
            status = AttendanceStatus.DROPPED_OFF,
            method = VerificationMethod.STUDENT_CONFIRM,
            reason = "Student confirmed drop-off at campus destination"
        )
        _studentStopArrivalPrompt.value = null
    }

    fun dismissArrivalPrompt() {
        _studentStopArrivalPrompt.value = null
    }

    // Driver actions
    fun startTrip(busId: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.startTrip(busId, user.name)
        }
    }

    fun endTrip(busId: String) {
        viewModelScope.launch {
            repository.endTrip(busId)
        }
    }

    // Emergency actions
    fun triggerEmergency(reason: String, busId: String? = null) {
        viewModelScope.launch {
            val user = _currentUser.value
            val targetBusId = busId ?: user.assignedBusId ?: "BUS-01"
            repository.raiseEmergency(
                busId = targetBusId,
                reason = reason,
                reportedByRole = user.role,
                reporterName = user.name,
                contactNumber = user.phone
            )
        }
    }

    fun resolveEmergency(incidentId: String, notes: String) {
        viewModelScope.launch {
            val user = _currentUser.value
            repository.resolveEmergency(incidentId, notes, user.name)
        }
    }

    // Notifications
    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    // Admin Bus Management
    fun saveBus(bus: BusEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateBus(bus)
        }
    }

    fun deleteBus(busId: String) {
        viewModelScope.launch {
            repository.deleteBus(busId)
        }
    }

    fun saveRoute(route: RouteEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateRoute(route)
        }
    }

    fun saveStop(stop: StopEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateStop(stop)
        }
    }

    fun saveStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateStudent(student)
        }
    }

    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            repository.deleteStudent(studentId)
        }
    }

    // Export CSV
    suspend fun getAttendanceCsv(busId: String? = null): String {
        return repository.generateAttendanceCsv(busId)
    }

    // Firebase Authentication
    fun signInWithEmail(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authManager.signInWithEmailPassword(email, pass)
            result.onSuccess { user ->
                _currentUser.value = user
                _isAuthenticated.value = true
                onResult(true, null)
            }.onFailure { error ->
                onResult(false, error.message)
            }
        }
    }

    fun signInWithGoogle(context: Context, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = authManager.signInWithGoogle(context)
            result.onSuccess { user ->
                _currentUser.value = user
                _isAuthenticated.value = true
                onResult(true, null)
            }.onFailure { error ->
                onResult(false, error.message)
            }
        }
    }

    fun signOut() {
        authManager.signOut()
        _isAuthenticated.value = false
    }
}
