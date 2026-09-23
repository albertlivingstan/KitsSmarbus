package com.example.data.repository

import com.example.data.model.*

object DemoDataSeed {

    val routes = listOf(
        RouteEntity(
            id = "ROUTE-01",
            routeNumber = "R-01",
            routeName = "Gandhipuram - Ukkadam - Karunya",
            startLocation = "Gandhipuram Central Bus Stand",
            destination = "Karunya Nagar Campus",
            totalDistanceKm = 32.5f,
            estimatedDurationMinutes = 65,
            morningDepartureTime = "07:15 AM",
            eveningReturnTime = "04:45 PM"
        ),
        RouteEntity(
            id = "ROUTE-02",
            routeNumber = "R-02",
            routeName = "Singanallur - RS Puram - Karunya",
            startLocation = "Singanallur Bus Terminal",
            destination = "Karunya Nagar Campus",
            totalDistanceKm = 35.8f,
            estimatedDurationMinutes = 70,
            morningDepartureTime = "07:10 AM",
            eveningReturnTime = "04:45 PM"
        ),
        RouteEntity(
            id = "ROUTE-03",
            routeNumber = "R-03",
            routeName = "Peelamedu - Vadavalli - Karunya",
            startLocation = "Peelamedu (Hope College)",
            destination = "Karunya Nagar Campus",
            totalDistanceKm = 34.0f,
            estimatedDurationMinutes = 68,
            morningDepartureTime = "07:15 AM",
            eveningReturnTime = "04:45 PM"
        ),
        RouteEntity(
            id = "ROUTE-04",
            routeNumber = "R-04",
            routeName = "Saravanampatti - Pooluvapatti - Karunya",
            startLocation = "Saravanampatti Junction",
            destination = "Karunya Nagar Campus",
            totalDistanceKm = 38.2f,
            estimatedDurationMinutes = 75,
            morningDepartureTime = "07:05 AM",
            eveningReturnTime = "04:45 PM"
        ),
        RouteEntity(
            id = "ROUTE-05",
            routeNumber = "R-05",
            routeName = "Pollachi - Eachanari - Karunya",
            startLocation = "Pollachi Bus Stand",
            destination = "Karunya Nagar Campus",
            totalDistanceKm = 42.0f,
            estimatedDurationMinutes = 80,
            morningDepartureTime = "07:00 AM",
            eveningReturnTime = "04:45 PM"
        )
    )

    val stops = listOf(
        // Route 1 Stops
        StopEntity("ST-101", "ROUTE-01", "Gandhipuram Central", 11.0168, 76.9558, 1, "07:15 AM", 100),
        StopEntity("ST-102", "ROUTE-01", "Town Hall Clock Tower", 10.9972, 76.9634, 2, "07:25 AM", 100),
        StopEntity("ST-103", "ROUTE-01", "Ukkadam Bus Stand", 10.9882, 76.9602, 3, "07:35 AM", 120),
        StopEntity("ST-104", "ROUTE-01", "Perur Patteeswarar Temple", 10.9705, 76.9189, 4, "07:48 AM", 100),
        StopEntity("ST-105", "ROUTE-01", "Vedapatti Junction", 10.9620, 76.8720, 5, "07:58 AM", 100),
        StopEntity("ST-106", "ROUTE-01", "Alandurai Checkpost", 10.9515, 76.8123, 6, "08:08 AM", 100),
        StopEntity("ST-107", "ROUTE-01", "Thondamuthur Bus Stop", 10.9856, 76.8312, 7, "08:18 AM", 100),
        StopEntity("ST-108", "ROUTE-01", "Karunya Nagar Main Gate", 10.9360, 76.7440, 8, "08:25 AM", 150),

        // Route 2 Stops
        StopEntity("ST-201", "ROUTE-02", "Singanallur Junction", 10.9992, 77.0264, 1, "07:10 AM", 100),
        StopEntity("ST-202", "ROUTE-02", "Ramanathapuram 80ft Rd", 10.9942, 76.9934, 2, "07:22 AM", 100),
        StopEntity("ST-203", "ROUTE-02", "Railway Station Main Gate", 10.9980, 76.9670, 3, "07:32 AM", 100),
        StopEntity("ST-204", "ROUTE-02", "RS Puram Post Office", 11.0084, 76.9470, 4, "07:42 AM", 100),
        StopEntity("ST-205", "ROUTE-02", "Kovaipudur Pirivu", 10.9312, 76.9372, 5, "07:55 AM", 100),
        StopEntity("ST-206", "ROUTE-02", "Madampatti Bypass", 10.9552, 76.8488, 6, "08:10 AM", 100),
        StopEntity("ST-207", "ROUTE-02", "Karunya Bethesda Center", 10.9340, 76.7460, 7, "08:23 AM", 120),
        StopEntity("ST-208", "ROUTE-02", "Karunya Nagar Main Gate", 10.9360, 76.7440, 8, "08:25 AM", 150),

        // Route 3 Stops
        StopEntity("ST-301", "ROUTE-03", "Peelamedu Hope College", 11.0315, 77.0142, 1, "07:15 AM", 100),
        StopEntity("ST-302", "ROUTE-03", "Lakshmi Mills", 11.0175, 76.9852, 2, "07:28 AM", 100),
        StopEntity("ST-303", "ROUTE-03", "Vadavalli Bus Stand", 11.0250, 76.9015, 3, "07:45 AM", 100),
        StopEntity("ST-304", "ROUTE-03", "Kalveerampalayam", 11.0020, 76.8720, 4, "07:58 AM", 100),
        StopEntity("ST-305", "ROUTE-03", "Iruttupallam Junction", 10.9430, 76.7620, 5, "08:15 AM", 100),
        StopEntity("ST-306", "ROUTE-03", "Karunya Nagar Main Gate", 10.9360, 76.7440, 6, "08:25 AM", 150),

        // Route 4 Stops
        StopEntity("ST-401", "ROUTE-04", "Saravanampatti", 11.0805, 76.9961, 1, "07:05 AM", 100),
        StopEntity("ST-402", "ROUTE-04", "Ganapathy Bus Stop", 11.0412, 76.9805, 2, "07:20 AM", 100),
        StopEntity("ST-403", "ROUTE-04", "Cross Cut Signal", 11.0195, 76.9620, 3, "07:35 AM", 100),
        StopEntity("ST-404", "ROUTE-04", "Pooluvapatti Arch", 10.9450, 76.7820, 4, "08:10 AM", 100),
        StopEntity("ST-405", "ROUTE-04", "Karunya Nagar Main Gate", 10.9360, 76.7440, 5, "08:25 AM", 150),

        // Route 5 Stops
        StopEntity("ST-501", "ROUTE-05", "Pollachi Main Bus Stand", 10.6580, 77.0090, 1, "07:00 AM", 120),
        StopEntity("ST-502", "ROUTE-05", "Kinathukadavu", 10.8210, 77.0190, 2, "07:25 AM", 100),
        StopEntity("ST-503", "ROUTE-05", "Eachanari Temple", 10.9310, 76.9690, 3, "07:45 AM", 100),
        StopEntity("ST-504", "ROUTE-05", "Sundarapuram", 10.9520, 76.9620, 4, "07:55 AM", 100),
        StopEntity("ST-505", "ROUTE-05", "Karunya Nagar Main Gate", 10.9360, 76.7440, 5, "08:25 AM", 150)
    )

    val drivers = listOf(
        DriverEntity("DRV-01", "Murugan Velusamy", "+91 94871 10001", "DL-TN37-2015-00124", "2031-10-15", "BUS-01", "Gandhipuram - Karunya", "On Duty", 12),
        DriverEntity("DRV-02", "Kaliappan Rajan", "+91 94871 10002", "DL-TN37-2016-00432", "2030-05-20", "BUS-02", "Singanallur - Karunya", "On Duty", 9),
        DriverEntity("DRV-03", "Anthony Xavier", "+91 94871 10003", "DL-TN37-2012-00891", "2029-08-11", "BUS-03", "Peelamedu - Karunya", "On Duty", 15),
        DriverEntity("DRV-04", "Chandran S.", "+91 94871 10004", "DL-TN37-2018-00511", "2032-02-14", "BUS-04", "Saravanampatti - Karunya", "On Duty", 7),
        DriverEntity("DRV-05", "Palanisamy K.", "+91 94871 10005", "DL-TN37-2014-00923", "2030-11-30", "BUS-05", "Pollachi - Karunya", "On Duty", 11),
        DriverEntity("DRV-06", "Senthil Kumar", "+91 94871 10006", "DL-TN37-2019-00192", "2033-04-18", "BUS-06", "Gandhipuram - Karunya", "Standby", 5),
        DriverEntity("DRV-07", "Dharmaraj M.", "+91 94871 10007", "DL-TN37-2011-00674", "2028-12-01", "BUS-07", "Singanallur - Karunya", "On Duty", 14),
        DriverEntity("DRV-08", "David Wilson", "+91 94871 10008", "DL-TN37-2017-00331", "2031-09-09", "BUS-08", "Peelamedu - Karunya", "On Duty", 8),
        DriverEntity("DRV-09", "Krishnamoorthy V.", "+91 94871 10009", "DL-TN37-2013-00782", "2029-06-25", "BUS-09", "Saravanampatti - Karunya", "On Duty", 13),
        DriverEntity("DRV-10", "Ganesan P.", "+91 94871 10010", "DL-TN37-2020-00445", "2034-01-20", "BUS-10", "Pollachi - Karunya", "On Duty", 6)
    )

    val coordinators = listOf(
        CoordinatorEntity("CRD-01", "Dr. John Peter", "EMP-2041", "CSE Department", "+91 98430 20001", "BUS-01", "Gandhipuram - Karunya", "johnpeter@karunya.edu"),
        CoordinatorEntity("CRD-02", "Prof. Mary Shanthi", "EMP-2052", "ECE Department", "+91 98430 20002", "BUS-02", "Singanallur - Karunya", "maryshanthi@karunya.edu"),
        CoordinatorEntity("CRD-03", "Dr. Stephen Samuel", "EMP-2063", "Mechanical Dept", "+91 98430 20003", "BUS-03", "Peelamedu - Karunya", "stephensamuel@karunya.edu"),
        CoordinatorEntity("CRD-04", "Prof. Grace Jebarani", "EMP-2074", "Biotechnology", "+91 98430 20004", "BUS-04", "Saravanampatti - Karunya", "gracejebarani@karunya.edu"),
        CoordinatorEntity("CRD-05", "Dr. Paulson Thomas", "EMP-2085", "AI & Data Science", "+91 98430 20005", "BUS-05", "Pollachi - Karunya", "paulson@karunya.edu"),
        CoordinatorEntity("CRD-06", "Prof. Andrews Jebaraj", "EMP-2096", "Civil Engineering", "+91 98430 20006", "BUS-06", "Gandhipuram - Karunya", "andrews@karunya.edu"),
        CoordinatorEntity("CRD-07", "Dr. Blessy Rachel", "EMP-2107", "Management Studies", "+91 98430 20007", "BUS-07", "Singanallur - Karunya", "blessy@karunya.edu"),
        CoordinatorEntity("CRD-08", "Prof. Victor Daniel", "EMP-2118", "Robotics & Automation", "+91 98430 20008", "BUS-08", "Peelamedu - Karunya", "victor@karunya.edu"),
        CoordinatorEntity("CRD-09", "Dr. Shiny Mathew", "EMP-2129", "Chemistry Dept", "+91 98430 20009", "BUS-09", "Saravanampatti - Karunya", "shinym@karunya.edu"),
        CoordinatorEntity("CRD-10", "Prof. Ronald Wilson", "EMP-2130", "Physics Dept", "+91 98430 20010", "BUS-10", "Pollachi - Karunya", "ronald@karunya.edu")
    )

    val buses = listOf(
        BusEntity(
            id = "BUS-01",
            busNumber = "Bus #12",
            registrationNumber = "TN 37 BK 1001",
            routeId = "ROUTE-01",
            routeName = "Gandhipuram - Karunya",
            driverId = "DRV-01",
            driverName = "Murugan Velusamy",
            driverPhone = "+91 94871 10001",
            coordinatorId = "CRD-01",
            coordinatorName = "Dr. John Peter",
            coordinatorPhone = "+91 98430 20001",
            capacity = 50,
            currentPassengers = 34,
            status = BusStatus.RUNNING,
            currentLat = 10.9705,
            currentLng = 76.9189, // At Perur Patteeswarar
            speedKmh = 42.5f,
            headingDegrees = 245f,
            lastUpdated = System.currentTimeMillis() - 15000,
            currentStopId = "ST-104",
            currentStopName = "Perur Patteeswarar Temple",
            nextStopId = "ST-105",
            nextStopName = "Vedapatti Junction",
            etaMinutes = 9,
            maintenanceStatus = "Good - Last Serviced Aug 2026",
            isDemoGps = true
        ),
        BusEntity(
            id = "BUS-02",
            busNumber = "Bus #07",
            registrationNumber = "TN 37 BK 1002",
            routeId = "ROUTE-02",
            routeName = "Singanallur - Karunya",
            driverId = "DRV-02",
            driverName = "Kaliappan Rajan",
            driverPhone = "+91 94871 10002",
            coordinatorId = "CRD-02",
            coordinatorName = "Prof. Mary Shanthi",
            coordinatorPhone = "+91 98430 20002",
            capacity = 48,
            currentPassengers = 28,
            status = BusStatus.RUNNING,
            currentLat = 10.9980,
            currentLng = 76.9670, // Railway Station
            speedKmh = 38.0f,
            headingDegrees = 260f,
            lastUpdated = System.currentTimeMillis() - 25000,
            currentStopId = "ST-203",
            currentStopName = "Railway Station Main Gate",
            nextStopId = "ST-204",
            nextStopName = "RS Puram Post Office",
            etaMinutes = 11,
            maintenanceStatus = "Good",
            isDemoGps = true
        ),
        BusEntity(
            id = "BUS-03",
            busNumber = "Bus #18",
            registrationNumber = "TN 37 BK 1003",
            routeId = "ROUTE-03",
            routeName = "Peelamedu - Karunya",
            driverId = "DRV-03",
            driverName = "Anthony Xavier",
            driverPhone = "+91 94871 10003",
            coordinatorId = "CRD-03",
            coordinatorName = "Dr. Stephen Samuel",
            coordinatorPhone = "+91 98430 20003",
            capacity = 52,
            currentPassengers = 41,
            status = BusStatus.AT_STOP,
            currentLat = 11.0250,
            currentLng = 76.9015, // Vadavalli
            speedKmh = 0f,
            headingDegrees = 220f,
            lastUpdated = System.currentTimeMillis() - 8000,
            currentStopId = "ST-303",
            currentStopName = "Vadavalli Bus Stand",
            nextStopId = "ST-304",
            nextStopName = "Kalveerampalayam",
            etaMinutes = 13,
            maintenanceStatus = "Good",
            isDemoGps = true
        ),
        BusEntity(
            id = "BUS-04",
            busNumber = "Bus #24",
            registrationNumber = "TN 37 BK 1004",
            routeId = "ROUTE-04",
            routeName = "Saravanampatti - Karunya",
            driverId = "DRV-04",
            driverName = "Chandran S.",
            driverPhone = "+91 94871 10004",
            coordinatorId = "CRD-04",
            coordinatorName = "Prof. Grace Jebarani",
            coordinatorPhone = "+91 98430 20004",
            capacity = 45,
            currentPassengers = 39,
            status = BusStatus.DELAYED,
            currentLat = 11.0195,
            currentLng = 76.9620, // Cross Cut Signal
            speedKmh = 14.0f,
            headingDegrees = 205f,
            lastUpdated = System.currentTimeMillis() - 40000,
            currentStopId = "ST-403",
            currentStopName = "Cross Cut Signal",
            nextStopId = "ST-404",
            nextStopName = "Pooluvapatti Arch",
            etaMinutes = 24,
            maintenanceStatus = "Good - AC Checked",
            isDemoGps = true
        ),
        BusEntity(
            id = "BUS-05",
            busNumber = "Bus #09",
            registrationNumber = "TN 37 BK 1005",
            routeId = "ROUTE-05",
            routeName = "Pollachi - Karunya",
            driverId = "DRV-05",
            driverName = "Palanisamy K.",
            driverPhone = "+91 94871 10005",
            coordinatorId = "CRD-05",
            coordinatorName = "Dr. Paulson Thomas",
            coordinatorPhone = "+91 98430 20005",
            capacity = 50,
            currentPassengers = 45,
            status = BusStatus.RUNNING,
            currentLat = 10.9310,
            currentLng = 76.9690, // Eachanari
            speedKmh = 48.0f,
            headingDegrees = 310f,
            lastUpdated = System.currentTimeMillis() - 12000,
            currentStopId = "ST-503",
            currentStopName = "Eachanari Temple",
            nextStopId = "ST-504",
            nextStopName = "Sundarapuram",
            etaMinutes = 10,
            maintenanceStatus = "Good",
            isDemoGps = true
        ),
        BusEntity(
            id = "BUS-06",
            busNumber = "Bus #05",
            registrationNumber = "TN 37 BK 1006",
            routeId = "ROUTE-01",
            routeName = "Gandhipuram - Karunya",
            driverId = "DRV-06",
            driverName = "Senthil Kumar",
            driverPhone = "+91 94871 10006",
            coordinatorId = "CRD-06",
            coordinatorName = "Prof. Andrews Jebaraj",
            coordinatorPhone = "+91 98430 20006",
            capacity = 50,
            currentPassengers = 0,
            status = BusStatus.SCHEDULED,
            currentLat = 11.0168,
            currentLng = 76.9558,
            speedKmh = 0f,
            headingDegrees = 0f,
            lastUpdated = System.currentTimeMillis() - 120000,
            currentStopId = "ST-101",
            currentStopName = "Gandhipuram Central",
            nextStopId = "ST-102",
            nextStopName = "Town Hall Clock Tower",
            etaMinutes = 0,
            maintenanceStatus = "Inspection Due Soon",
            isDemoGps = true
        ),
        BusEntity(
            id = "BUS-07",
            busNumber = "Bus #15",
            registrationNumber = "TN 37 BK 1007",
            routeId = "ROUTE-02",
            routeName = "Singanallur - Karunya",
            driverId = "DRV-07",
            driverName = "Dharmaraj M.",
            driverPhone = "+91 94871 10007",
            coordinatorId = "CRD-07",
            coordinatorName = "Dr. Blessy Rachel",
            coordinatorPhone = "+91 98430 20007",
            capacity = 50,
            currentPassengers = 31,
            status = BusStatus.RUNNING,
            currentLat = 10.9552,
            currentLng = 76.8488, // Madampatti Bypass
            speedKmh = 45.0f,
            headingDegrees = 250f,
            lastUpdated = System.currentTimeMillis() - 18000,
            currentStopId = "ST-206",
            currentStopName = "Madampatti Bypass",
            nextStopId = "ST-207",
            nextStopName = "Karunya Bethesda Center",
            etaMinutes = 8,
            maintenanceStatus = "Good",
            isDemoGps = true
        ),
        BusEntity(
            id = "BUS-08",
            busNumber = "Bus #22",
            registrationNumber = "TN 37 BK 1008",
            routeId = "ROUTE-03",
            routeName = "Peelamedu - Karunya",
            driverId = "DRV-08",
            driverName = "David Wilson",
            driverPhone = "+91 94871 10008",
            coordinatorId = "CRD-08",
            coordinatorName = "Prof. Victor Daniel",
            coordinatorPhone = "+91 98430 20008",
            capacity = 45,
            currentPassengers = 25,
            status = BusStatus.RUNNING,
            currentLat = 10.9430,
            currentLng = 76.7620, // Iruttupallam Junction
            speedKmh = 35.0f,
            headingDegrees = 265f,
            lastUpdated = System.currentTimeMillis() - 5000,
            currentStopId = "ST-305",
            currentStopName = "Iruttupallam Junction",
            nextStopId = "ST-306",
            nextStopName = "Karunya Nagar Main Gate",
            etaMinutes = 4,
            maintenanceStatus = "Good",
            isDemoGps = true
        ),
        BusEntity(
            id = "BUS-09",
            busNumber = "Bus #30",
            registrationNumber = "TN 37 BK 1009",
            routeId = "ROUTE-04",
            routeName = "Saravanampatti - Karunya",
            driverId = "DRV-09",
            driverName = "Krishnamoorthy V.",
            driverPhone = "+91 94871 10009",
            coordinatorId = "CRD-09",
            coordinatorName = "Dr. Shiny Mathew",
            coordinatorPhone = "+91 98430 20009",
            capacity = 55,
            currentPassengers = 0,
            status = BusStatus.COMPLETED,
            currentLat = 10.9360,
            currentLng = 76.7440,
            speedKmh = 0f,
            headingDegrees = 0f,
            lastUpdated = System.currentTimeMillis() - 3600000,
            currentStopId = "ST-405",
            currentStopName = "Karunya Nagar Main Gate",
            nextStopId = null,
            nextStopName = "Trip Completed",
            etaMinutes = 0,
            maintenanceStatus = "Good",
            isDemoGps = true
        ),
        BusEntity(
            id = "BUS-10",
            busNumber = "Bus #11",
            registrationNumber = "TN 37 BK 1010",
            routeId = "ROUTE-05",
            routeName = "Pollachi - Karunya",
            driverId = "DRV-10",
            driverName = "Ganesan P.",
            driverPhone = "+91 94871 10010",
            coordinatorId = "CRD-10",
            coordinatorName = "Prof. Ronald Wilson",
            coordinatorPhone = "+91 98430 20010",
            capacity = 48,
            currentPassengers = 0,
            status = BusStatus.OFFLINE,
            currentLat = 10.6580,
            currentLng = 77.0090,
            speedKmh = 0f,
            headingDegrees = 0f,
            lastUpdated = System.currentTimeMillis() - 7200000,
            currentStopId = "ST-501",
            currentStopName = "Pollachi Main Bus Stand",
            nextStopId = null,
            nextStopName = "Parked",
            etaMinutes = 0,
            maintenanceStatus = "Tire Replacement in Progress",
            isDemoGps = true
        )
    )

    val students = listOf(
        StudentEntity("STU-01", "Albert George", "URK22CS1045", "B.Tech CSE", "3rd Year", "+91 98401 54321", "Mr. George Joseph", "+91 94432 11001", "BUS-01", "ROUTE-01", "ST-103", "Ukkadam Bus Stand", "ST-108", "Karunya Nagar Main Gate", "KITS-QR-URK22CS1045-BUS12", AttendanceStatus.ON_BUS, System.currentTimeMillis() - 1800000, null),
        StudentEntity("STU-02", "Sneha K. Nair", "URK22AI1012", "B.Tech AI & DS", "3rd Year", "+91 98401 54322", "Mr. Krishnakumar", "+91 94432 11002", "BUS-01", "ROUTE-01", "ST-101", "Gandhipuram Central", "ST-108", "Karunya Nagar Main Gate", "KITS-QR-URK22AI1012-BUS12", AttendanceStatus.ON_BUS, System.currentTimeMillis() - 3600000, null),
        StudentEntity("STU-03", "John David", "URK23EC1088", "B.Tech ECE", "2nd Year", "+91 98401 54323", "Mrs. David Susan", "+91 94432 11003", "BUS-01", "ROUTE-01", "ST-104", "Perur Patteeswarar Temple", "ST-108", "Karunya Nagar Main Gate", "KITS-QR-URK23EC1088-BUS12", AttendanceStatus.ON_BUS, System.currentTimeMillis() - 900000, null),
        StudentEntity("STU-04", "Priya Darshini", "URK21BT1033", "B.Tech Biotech", "4th Year", "+91 98401 54324", "Mr. Sundar Raj", "+91 94432 11004", "BUS-01", "ROUTE-01", "ST-106", "Alandurai Checkpost", "ST-108", "Karunya Nagar Main Gate", "KITS-QR-URK21BT1033-BUS12", AttendanceStatus.NOT_MARKED, null, null),
        StudentEntity("STU-05", "Kevin Mathew", "URK22ME1019", "B.Tech Mech", "3rd Year", "+91 98401 54325", "Mr. Mathew Kurian", "+91 94432 11005", "BUS-01", "ROUTE-01", "ST-102", "Town Hall Clock Tower", "ST-108", "Karunya Nagar Main Gate", "KITS-QR-URK22ME1019-BUS12", AttendanceStatus.ABSENT, null, null),
        StudentEntity("STU-06", "Ananya Sharma", "URK23CS1102", "B.Tech CSE", "2nd Year", "+91 98401 54326", "Mr. Rajesh Sharma", "+91 94432 11006", "BUS-01", "ROUTE-01", "ST-107", "Thondamuthur Bus Stop", "ST-108", "Karunya Nagar Main Gate", "KITS-QR-URK23CS1102-BUS12", AttendanceStatus.NOT_MARKED, null, null),
        StudentEntity("STU-07", "Rohan Samuel", "URK24AI1004", "B.Tech AI & DS", "1st Year", "+91 98401 54327", "Mrs. Grace Samuel", "+91 94432 11007", "BUS-01", "ROUTE-01", "ST-103", "Ukkadam Bus Stand", "ST-108", "Karunya Nagar Main Gate", "KITS-QR-URK24AI1004-BUS12", AttendanceStatus.ON_BUS, System.currentTimeMillis() - 1750000, null),
        StudentEntity("STU-08", "Divya Elizabeth", "URK22CE1015", "B.Tech Civil", "3rd Year", "+91 98401 54328", "Mr. Jacob Varghese", "+91 94432 11008", "BUS-01", "ROUTE-01", "ST-101", "Gandhipuram Central", "ST-108", "Karunya Nagar Main Gate", "KITS-QR-URK22CE1015-BUS12", AttendanceStatus.ON_BUS, System.currentTimeMillis() - 3550000, null),
        StudentEntity("STU-09", "Ashwin Kumar", "URK23RA1009", "B.Tech Robotics", "2nd Year", "+91 98401 54329", "Mr. Kumaravel", "+91 94432 11009", "BUS-01", "ROUTE-01", "ST-104", "Perur Patteeswarar Temple", "ST-108", "Karunya Nagar Main Gate", "KITS-QR-URK23RA1009-BUS12", AttendanceStatus.ON_BUS, System.currentTimeMillis() - 880000, null),
        StudentEntity("STU-10", "Hannah Mercy", "URK21CS1090", "B.Tech CSE", "4th Year", "+91 98401 54330", "Rev. John Thomas", "+91 94432 11010", "BUS-01", "ROUTE-01", "ST-105", "Vedapatti Junction", "ST-108", "Karunya Nagar Main Gate", "KITS-QR-URK21CS1090-BUS12", AttendanceStatus.PRESENT, null, null),

        // Route 2 students
        StudentEntity("STU-11", "Karthik Raja", "URK22CS1050", "B.Tech CSE", "3rd Year", "+91 98401 54331", "Mr. Rajasekaran", "+91 94432 11011", "BUS-02", "ROUTE-02", "ST-201", "Singanallur Junction", "ST-208", "Karunya Nagar Main Gate", "KITS-QR-URK22CS1050-BUS07", AttendanceStatus.ON_BUS, System.currentTimeMillis() - 3200000, null),
        StudentEntity("STU-12", "Meera Krishnan", "URK23EC1040", "B.Tech ECE", "2nd Year", "+91 98401 54332", "Mr. Unnikrishnan", "+91 94432 11012", "BUS-02", "ROUTE-02", "ST-202", "Ramanathapuram 80ft Rd", "ST-208", "Karunya Nagar Main Gate", "KITS-QR-URK23EC1040-BUS07", AttendanceStatus.ON_BUS, System.currentTimeMillis() - 2500000, null),
        StudentEntity("STU-13", "Stephen Paul", "URK24ME1005", "B.Tech Mech", "1st Year", "+91 98401 54333", "Mr. Paulraj S.", "+91 94432 11013", "BUS-02", "ROUTE-02", "ST-203", "Railway Station Main Gate", "ST-208", "Karunya Nagar Main Gate", "KITS-QR-URK24ME1005-BUS07", AttendanceStatus.ON_BUS, System.currentTimeMillis() - 1500000, null),
        StudentEntity("STU-14", "Deepa Mohan", "URK21AI1008", "B.Tech AI & DS", "4th Year", "+91 98401 54334", "Mr. Mohan Babu", "+91 94432 11014", "BUS-02", "ROUTE-02", "ST-204", "RS Puram Post Office", "ST-208", "Karunya Nagar Main Gate", "KITS-QR-URK21AI1008-BUS07", AttendanceStatus.NOT_MARKED, null, null),

        // Route 3 students
        StudentEntity("STU-21", "Naveen Prakash", "URK22CS1080", "B.Tech CSE", "3rd Year", "+91 98401 54335", "Mr. Prakash M.", "+91 94432 11015", "BUS-03", "ROUTE-03", "ST-301", "Peelamedu Hope College", "ST-306", "Karunya Nagar Main Gate", "KITS-QR-URK22CS1080-BUS18", AttendanceStatus.ON_BUS, System.currentTimeMillis() - 3000000, null),
        StudentEntity("STU-22", "Grace Varghese", "URK23BT1025", "B.Tech Biotech", "2nd Year", "+91 98401 54336", "Mr. Varghese C.", "+91 94432 11016", "BUS-03", "ROUTE-03", "ST-303", "Vadavalli Bus Stand", "ST-306", "Karunya Nagar Main Gate", "KITS-QR-URK23BT1025-BUS18", AttendanceStatus.ON_BUS, System.currentTimeMillis() - 400000, null)
    )

    val users = listOf(
        UserEntity(
            id = "USR-001",
            name = "Dr. C. Daniel (Transport Admin)",
            email = "admin@karunya.edu",
            role = UserRole.TRANSPORT_ADMIN,
            phone = "+91 94878 12345"
        ),
        UserEntity(
            id = "USR-002",
            name = "Dr. John Peter",
            email = "johnpeter@karunya.edu",
            role = UserRole.COORDINATOR,
            phone = "+91 98430 20001",
            assignedBusId = "BUS-01",
            assignedRouteId = "ROUTE-01"
        ),
        UserEntity(
            id = "USR-003",
            name = "Murugan Velusamy",
            email = "driver12@karunya.edu",
            role = UserRole.DRIVER,
            phone = "+91 94871 10001",
            assignedBusId = "BUS-01",
            assignedRouteId = "ROUTE-01"
        ),
        UserEntity(
            id = "USR-004",
            name = "Albert George",
            email = "albert@karunya.edu",
            role = UserRole.STUDENT,
            phone = "+91 98401 54321",
            registerNumber = "URK22CS1045",
            department = "B.Tech CSE",
            assignedBusId = "BUS-01",
            assignedRouteId = "ROUTE-01"
        ),
        UserEntity(
            id = "USR-005",
            name = "Mr. George Joseph (Parent)",
            email = "parent.albert@gmail.com",
            role = UserRole.PARENT,
            phone = "+91 94432 11001",
            studentWardId = "STU-01"
        )
    )

    val notifications = listOf(
        NotificationEntity("NTF-01", "Bus #12 En Route", "Bus #12 departed Ukkadam and is approaching Perur Patteeswarar.", NotificationType.APPROACHING_STOP, System.currentTimeMillis() - 900000, false, targetBusId = "BUS-01"),
        NotificationEntity("NTF-02", "Albert Boarded Bus #12", "Albert George checked in via QR scan at Ukkadam Bus Stand at 07:35 AM.", NotificationType.STUDENT_BOARDED, System.currentTimeMillis() - 1800000, true, targetBusId = "BUS-01"),
        NotificationEntity("NTF-03", "Bus #24 Delay Alert", "Bus #24 delayed by 15 mins due to Cross Cut Road traffic signal congestion.", NotificationType.DELAYED, System.currentTimeMillis() - 2400000, false, targetBusId = "BUS-04"),
        NotificationEntity("NTF-04", "Morning Trips Started", "All 10 campus transport buses have commenced morning pickup routes.", NotificationType.BUS_STARTED, System.currentTimeMillis() - 4800000, true),
        NotificationEntity("NTF-05", "Bus #22 Arrived Early", "Bus #22 reached Iruttupallam Junction 3 minutes ahead of schedule.", NotificationType.ARRIVED_AT_STOP, System.currentTimeMillis() - 300000, false, targetBusId = "BUS-08")
    )

    val sampleIncidents = listOf(
        EmergencyIncidentEntity(
            id = "INC-1001",
            busId = "BUS-04",
            busNumber = "Bus #24",
            routeName = "Saravanampatti - Karunya",
            driverName = "Chandran S.",
            coordinatorName = "Prof. Grace Jebarani",
            latitude = 11.0195,
            longitude = 76.9620,
            timestamp = System.currentTimeMillis() - 2500000,
            status = "RESOLVED",
            reason = "Traffic Gridlock - Scheduled Delay Broadcasted",
            reportedByRole = UserRole.DRIVER,
            contactNumber = "+91 94871 10004",
            resolutionNotes = "Traffic cleared by Coimbatore City Traffic Police. Bus restarted safely."
        )
    )

    val auditLogs = listOf(
        AuditLogEntity(
            action = "STUDENT_ATTENDANCE_UPDATED",
            performedBy = "Dr. John Peter",
            userRole = UserRole.COORDINATOR,
            entityType = "StudentEntity",
            entityId = "STU-01",
            previousValue = "NOT_MARKED",
            newValue = "ON_BUS",
            timestamp = System.currentTimeMillis() - 1800000,
            reason = "QR Code Scanned successfully at Ukkadam stop"
        ),
        AuditLogEntity(
            action = "BUS_STATUS_CHANGED",
            performedBy = "Murugan Velusamy",
            userRole = UserRole.DRIVER,
            entityType = "BusEntity",
            entityId = "BUS-01",
            previousValue = "SCHEDULED",
            newValue = "RUNNING",
            timestamp = System.currentTimeMillis() - 4500000,
            reason = "Trip started at Gandhipuram Stand"
        ),
        AuditLogEntity(
            action = "STUDENT_MARKED_ABSENT",
            performedBy = "Dr. John Peter",
            userRole = UserRole.COORDINATOR,
            entityType = "StudentEntity",
            entityId = "STU-05",
            previousValue = "NOT_MARKED",
            newValue = "ABSENT",
            timestamp = System.currentTimeMillis() - 2200000,
            reason = "Student missed bus at Town Hall stop"
        )
    )
}
