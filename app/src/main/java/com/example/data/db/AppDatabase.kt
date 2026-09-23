package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        BusEntity::class,
        RouteEntity::class,
        StopEntity::class,
        DriverEntity::class,
        CoordinatorEntity::class,
        StudentEntity::class,
        AttendanceRecordEntity::class,
        TripEntity::class,
        EmergencyIncidentEntity::class,
        NotificationEntity::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun busDao(): BusDao
    abstract fun routeDao(): RouteDao
    abstract fun stopDao(): StopDao
    abstract fun driverDao(): DriverDao
    abstract fun coordinatorDao(): CoordinatorDao
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun tripDao(): TripDao
    abstract fun emergencyDao(): EmergencyDao
    abstract fun notificationDao(): NotificationDao
    abstract fun auditDao(): AuditDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "karunya_smartbus.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
