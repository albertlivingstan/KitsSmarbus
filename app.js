// Karunya KitsSmarbus - Main Application Logic & Google Maps Engine

// Global State
let map = null;
let trafficLayer = null;
let isSatellite = false;
let isTrafficOn = false;
let currentRole = 'STUDENT';
let simSpeed = 1.0;
let simInterval = null;
let currentSelectedBusId = 'BUS-01';

// Routes & Stop Data (Coimbatore Region to Karunya Campus)
const ROUTES = [
  {
    id: 'ROUTE-01',
    name: 'Route 01: Town Hall ➔ Karunya Campus',
    color: '#38BDF8',
    busId: 'BUS-01',
    busNumber: 'Bus #01 (TN 37 CZ 1024)',
    driverName: 'R. Selvam',
    driverPhone: '+91 98430 11223',
    capacity: 50,
    boarded: 42,
    stops: [
      { id: 'S1-1', name: 'Town Hall Main Stop', lat: 10.9968, lng: 76.9618, seq: 1 },
      { id: 'S1-2', name: 'Ukkadam Bus Stand', lat: 10.9902, lng: 76.9605, seq: 2 },
      { id: 'S1-3', name: 'Perur Temple Junction', lat: 10.9708, lng: 76.9082, seq: 3 },
      { id: 'S1-4', name: 'Alandurai Checkpost', lat: 10.9482, lng: 76.8125, seq: 4 },
      { id: 'S1-5', name: 'Karunya Nagar Gate', lat: 10.9385, lng: 76.7490, seq: 5 },
      { id: 'S1-6', name: 'Karunya Main Campus', lat: 10.9360, lng: 76.7440, seq: 6 }
    ]
  },
  {
    id: 'ROUTE-02',
    name: 'Route 02: Gandhipuram ➔ Karunya Campus',
    color: '#34D399',
    busId: 'BUS-02',
    busNumber: 'Bus #02 (TN 38 AY 4088)',
    driverName: 'K. Murugan',
    driverPhone: '+91 94432 88900',
    capacity: 50,
    boarded: 38,
    stops: [
      { id: 'S2-1', name: 'Gandhipuram Central', lat: 11.0168, lng: 76.9658, seq: 1 },
      { id: 'S2-2', name: 'RS Puram Head Post', lat: 11.0062, lng: 76.9512, seq: 2 },
      { id: 'S2-3', name: 'Vadavalli Bus Stop', lat: 11.0185, lng: 76.9002, seq: 3 },
      { id: 'S2-4', name: 'Thondamuthur Arch', lat: 10.9982, lng: 76.8350, seq: 4 },
      { id: 'S2-5', name: 'Karunya Main Campus', lat: 10.9360, lng: 76.7440, seq: 5 }
    ]
  },
  {
    id: 'ROUTE-03',
    name: 'Route 03: Singanallur ➔ Karunya Campus',
    color: '#A855F7',
    busId: 'BUS-03',
    busNumber: 'Bus #03 (TN 37 BX 9912)',
    driverName: 'S. Prakash',
    driverPhone: '+91 97890 33441',
    capacity: 50,
    boarded: 45,
    stops: [
      { id: 'S3-1', name: 'Singanallur Signal', lat: 10.9995, lng: 77.0252, seq: 1 },
      { id: 'S3-2', name: 'Ramanathapuram', lat: 10.9962, lng: 76.9892, seq: 2 },
      { id: 'S3-3', name: 'Sungam Bypass', lat: 10.9912, lng: 76.9740, seq: 3 },
      { id: 'S3-4', name: 'Karunya Main Campus', lat: 10.9360, lng: 76.7440, seq: 4 }
    ]
  },
  {
    id: 'ROUTE-04',
    name: 'Route 04: Thudiyalur ➔ Karunya Campus',
    color: '#F59E0B',
    busId: 'BUS-04',
    busNumber: 'Bus #04 (TN 37 DB 7780)',
    driverName: 'M. Vijay',
    driverPhone: '+91 99441 55662',
    capacity: 50,
    boarded: 40,
    stops: [
      { id: 'S4-1', name: 'Thudiyalur Junction', lat: 11.0825, lng: 76.9425, seq: 1 },
      { id: 'S4-2', name: 'Saibaba Colony', lat: 11.0312, lng: 76.9460, seq: 2 },
      { id: 'S4-3', name: 'Lawley Road', lat: 11.0112, lng: 76.9362, seq: 3 },
      { id: 'S4-4', name: 'Karunya Main Campus', lat: 10.9360, lng: 76.7440, seq: 4 }
    ]
  },
  {
    id: 'ROUTE-05',
    name: 'Route 05: Peelamedu ➔ Karunya Campus',
    color: '#F43F5E',
    busId: 'BUS-05',
    busNumber: 'Bus #05 (TN 37 EA 5511)',
    driverName: 'A. Joseph',
    driverPhone: '+91 98940 77123',
    capacity: 50,
    boarded: 48,
    stops: [
      { id: 'S5-1', name: 'Peelamedu PSG Tech', lat: 11.0245, lng: 77.0025, seq: 1 },
      { id: 'S5-2', name: 'Hope College', lat: 11.0298, lng: 77.0180, seq: 2 },
      { id: 'S5-3', name: 'Lakshmi Mills', lat: 11.0102, lng: 76.9820, seq: 3 },
      { id: 'S5-4', name: 'Karunya Main Campus', lat: 10.9360, lng: 76.7440, seq: 4 }
    ]
  }
];

// Live Bus Entities State
let busesState = [
  {
    id: 'BUS-01',
    routeId: 'ROUTE-01',
    busNumber: 'Bus #01',
    lat: 10.9708,
    lng: 76.9082,
    heading: 245,
    speedKmH: 42,
    status: 'RUNNING',
    nextStopName: 'Alandurai Checkpost',
    etaMins: 14,
    progressIdx: 2,
    progressFraction: 0.3
  },
  {
    id: 'BUS-02',
    routeId: 'ROUTE-02',
    busNumber: 'Bus #02',
    lat: 11.0062,
    lng: 76.9512,
    heading: 210,
    speedKmH: 38,
    status: 'RUNNING',
    nextStopName: 'Vadavalli Bus Stop',
    etaMins: 22,
    progressIdx: 1,
    progressFraction: 0.5
  },
  {
    id: 'BUS-03',
    routeId: 'ROUTE-03',
    busNumber: 'Bus #03',
    lat: 10.9962,
    lng: 76.9892,
    heading: 225,
    speedKmH: 0,
    status: 'AT_STOP',
    nextStopName: 'Sungam Bypass',
    etaMins: 28,
    progressIdx: 1,
    progressFraction: 0.0
  },
  {
    id: 'BUS-04',
    routeId: 'ROUTE-04',
    busNumber: 'Bus #04',
    lat: 11.0312,
    lng: 76.9460,
    heading: 195,
    speedKmH: 45,
    status: 'RUNNING',
    nextStopName: 'Lawley Road',
    etaMins: 25,
    progressIdx: 1,
    progressFraction: 0.4
  },
  {
    id: 'BUS-05',
    routeId: 'ROUTE-05',
    busNumber: 'Bus #05',
    lat: 11.0298,
    lng: 77.0180,
    heading: 250,
    speedKmH: 35,
    status: 'DELAYED',
    nextStopName: 'Lakshmi Mills',
    etaMins: 32,
    progressIdx: 1,
    progressFraction: 0.2
  }
];

// Map Objects
let mapMarkers = {};
let routePolylines = [];
let stopMarkers = [];

// Initialize Map & Application
window.addEventListener('load', () => {
  initPwaServiceWorker();
  initGoogleMap();
  renderRoleDashboard();
  startGpsSimulator();
});

// PWA Service Worker Registration
function initPwaServiceWorker() {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('./sw.js').catch(err => {
      console.log('Service Worker registration skipped:', err);
    });
  }
}

// Google Maps Initialization
function initGoogleMap() {
  const mapElement = document.getElementById('map');
  if (!mapElement) return;

  // Dark Map Style JSON matching Karunya KitsSmarbus theme
  const darkMapStyle = [
    { elementType: "geometry", stylers: [{ color: "#080E1E" }] },
    { elementType: "labels.text.stroke", stylers: [{ color: "#080E1E" }] },
    { elementType: "labels.text.fill", stylers: [{ color: "#7DD3FC" }] },
    {
      featureType: "administrative.locality",
      elementType: "labels.text.fill",
      stylers: [{ color: "#38BDF8" }]
    },
    {
      featureType: "poi",
      elementType: "labels.text.fill",
      stylers: [{ color: "#94A3B8" }]
    },
    {
      featureType: "road",
      elementType: "geometry",
      stylers: [{ color: "#1E293B" }]
    },
    {
      featureType: "road",
      elementType: "geometry.stroke",
      stylers: [{ color: "#0F172A" }]
    },
    {
      featureType: "road",
      elementType: "labels.text.fill",
      stylers: [{ color: "#CBD5E1" }]
    },
    {
      featureType: "road.highway",
      elementType: "geometry",
      stylers: [{ color: "#0284C7" }]
    },
    {
      featureType: "water",
      elementType: "geometry",
      stylers: [{ color: "#0369A1" }]
    }
  ];

  const karunyaCenter = { lat: 10.9480, lng: 76.8500 };

  map = new google.maps.Map(mapElement, {
    zoom: 12,
    center: karunyaCenter,
    styles: darkMapStyle,
    disableDefaultUI: true,
    zoomControl: true
  });

  trafficLayer = new google.maps.TrafficLayer();

  // Draw Route Polylines and Stop Markers
  drawRoutesAndStops();

  // Draw Bus Markers
  updateBusMarkersOnMap();

  // Draw Karunya Campus Marker
  drawCampusMarker();
}

// Draw Route Polylines and Stop Pins
function drawRoutesAndStops() {
  ROUTES.forEach(route => {
    const pathCoords = route.stops.map(s => ({ lat: s.lat, lng: s.lng }));

    // Polyline
    const polyline = new google.maps.Polyline({
      path: pathCoords,
      geodesic: true,
      strokeColor: route.color,
      strokeOpacity: 0.8,
      strokeWeight: 5,
      map: map
    });
    routePolylines.push(polyline);

    // Stop Pins
    route.stops.forEach(stop => {
      const isCampus = stop.name.includes('Karunya');

      const stopMarker = new google.maps.Marker({
        position: { lat: stop.lat, lng: stop.lng },
        map: map,
        title: stop.name,
        icon: {
          path: google.maps.SymbolPath.CIRCLE,
          scale: isCampus ? 8 : 5,
          fillColor: isCampus ? '#38BDF8' : '#FFFFFF',
          fillOpacity: 1,
          strokeColor: route.color,
          strokeWeight: 2.5
        }
      });

      const infoWindow = new google.maps.InfoWindow({
        content: `
          <div style="color: #0F172A; padding: 6px; font-family: sans-serif;">
            <strong style="font-size: 13px;">📍 ${stop.name}</strong><br>
            <span style="font-size: 11px; color: #475569;">Route: ${route.id} • Stop #${stop.seq}</span>
          </div>
        `
      });

      stopMarker.addListener('click', () => {
        infoWindow.open(map, stopMarker);
      });

      stopMarkers.push(stopMarker);
    });
  });
}

// Draw Karunya Main Campus Marker
function drawCampusMarker() {
  const campusPos = { lat: 10.9360, lng: 76.7440 };

  const campusMarker = new google.maps.Marker({
    position: campusPos,
    map: map,
    title: '🎓 Karunya Institute of Technology and Sciences',
    icon: {
      url: 'https://img.icons8.com/color/48/000000/graduation-cap.png',
      scaledSize: new google.maps.Size(40, 40)
    }
  });

  const campusInfoWindow = new google.maps.InfoWindow({
    content: `
      <div style="color:#0F172A; font-family: sans-serif; padding: 6px;">
        <h4 style="margin: 0 0 4px 0;">🎓 Karunya University Campus</h4>
        <p style="margin: 0; font-size: 11px; color: #475569;">Main Bus Terminal & Dispatch Station</p>
      </div>
    `
  });

  campusMarker.addListener('click', () => {
    campusInfoWindow.open(map, campusMarker);
  });
}

// Update Bus Markers on Map
function updateBusMarkersOnMap() {
  busesState.forEach(bus => {
    const route = ROUTES.find(r => r.id === bus.routeId);
    const busColor = route ? route.color : '#38BDF8';

    const busIconSvg = `
      <svg xmlns="http://www.w3.org/2000/svg" width="42" height="42" viewBox="0 0 48 48">
        <circle cx="24" cy="24" r="22" fill="#0F172A" stroke="${busColor}" stroke-width="3"/>
        <path d="M14 16c0-2.2 1.8-4 4-4h12c2.2 0 4 1.8 4 4v14c0 1.7-1.3 3-3 3v3c0 .6-.4 1-1 1h-2c-.6 0-1-.4-1-1v-3H19v3c0 .6-.4 1-1 1h-2c-.6 0-1-.4-1-1v-3c-1.7 0-3-1.3-3-3V16zm4 2h12v6H18v-6zm3 10c0-1.1-.9-2-2-2s-2 .9-2 2 .9 2 2 2 2-.9 2-2zm10 0c0-1.1-.9-2-2-2s-2 .9-2 2 .9 2 2 2 2-.9 2-2z" fill="${busColor}"/>
      </svg>
    `;

    const iconUrl = 'data:image/svg+xml;charset=UTF-8,' + encodeURIComponent(busIconSvg);

    if (!mapMarkers[bus.id]) {
      const marker = new google.maps.Marker({
        position: { lat: bus.lat, lng: bus.lng },
        map: map,
        title: `${bus.busNumber} • ${bus.speedKmH} km/h`,
        icon: {
          url: iconUrl,
          scaledSize: new google.maps.Size(42, 42),
          anchor: new google.maps.Point(21, 21)
        }
      });

      const infoWindow = new google.maps.InfoWindow({
        content: `
          <div style="color: #0F172A; padding: 8px; font-family: sans-serif; min-width: 180px;">
            <strong style="font-size: 14px; color: #0284C7;">🚌 ${bus.busNumber}</strong><br>
            <span style="font-size: 12px; color: #334155;">Speed: <b>${bus.speedKmH} km/h</b></span><br>
            <span style="font-size: 12px; color: #334155;">Next: <b>${bus.nextStopName}</b></span><br>
            <span style="font-size: 11px; color: #16A34A; font-weight: bold;">ETA: ${bus.etaMins} Mins</span>
          </div>
        `
      });

      marker.addListener('click', () => {
        infoWindow.open(map, marker);
        currentSelectedBusId = bus.id;
        renderRoleDashboard();
      });

      mapMarkers[bus.id] = marker;
    } else {
      mapMarkers[bus.id].setPosition({ lat: bus.lat, lng: bus.lng });
    }
  });
}

// Real-time GPS Simulator Engine
function startGpsSimulator() {
  if (simInterval) clearInterval(simInterval);

  simInterval = setInterval(() => {
    busesState.forEach(bus => {
      if (bus.status === 'PAUSED' || bus.status === 'EMERGENCY') return;

      const route = ROUTES.find(r => r.id === bus.routeId);
      if (!route) return;

      const currentStop = route.stops[bus.progressIdx];
      const nextStop = route.stops[bus.progressIdx + 1];

      if (!nextStop) {
        // Loop back to start stop
        bus.progressIdx = 0;
        bus.progressFraction = 0;
        return;
      }

      // Increment progress fraction
      bus.progressFraction += 0.05 * simSpeed;

      if (bus.progressFraction >= 1.0) {
        bus.progressFraction = 0.0;
        bus.progressIdx += 1;

        // Arrival Notification
        if (bus.id === 'BUS-01') {
          showToast(`🚌 Bus #01 arrived at ${nextStop.name}`);
        }
      }

      // Interpolate lat and lng
      const curLat = currentStop.lat + (nextStop.lat - currentStop.lat) * bus.progressFraction;
      const curLng = currentStop.lng + (nextStop.lng - currentStop.lng) * bus.progressFraction;

      bus.lat = curLat;
      bus.lng = curLng;
      bus.nextStopName = nextStop.name;
      bus.etaMins = Math.max(1, Math.round((1.0 - bus.progressFraction) * 8 + (route.stops.length - bus.progressIdx - 1) * 6));
      bus.speedKmH = bus.status === 'AT_STOP' ? 0 : Math.round(35 + Math.random() * 12);
    });

    updateBusMarkersOnMap();

    // Update active UI cards if open
    updateLiveDashboardStats();
  }, 1000);
}

// Role Switcher Handler
function switchRole(role) {
  currentRole = role;
  renderRoleDashboard();
  showToast(`Switched to ${role.replace('_', ' ')} Mode`);
}

// Render Dashboard based on current selected role
function renderRoleDashboard() {
  const titleEl = document.getElementById('panel-role-title');
  const contentEl = document.getElementById('panel-content');
  const badgeEl = document.getElementById('panel-active-bus-badge');

  if (!contentEl) return;

  const currentBus = busesState.find(b => b.id === currentSelectedBusId) || busesState[0];
  const currentRoute = ROUTES.find(r => r.id === currentBus.routeId) || ROUTES[0];

  if (badgeEl) {
    badgeEl.textContent = `${currentBus.busNumber} • ${currentRoute.id}`;
  }

  switch (currentRole) {
    case 'STUDENT':
      titleEl.textContent = '🎓 Student Pass & Tracking';
      contentEl.innerHTML = getStudentDashboardHtml(currentBus, currentRoute);
      generateQrCode('URK24CS101-BUS01-KARUNYA');
      break;

    case 'PARENT':
      titleEl.textContent = '👨‍👩‍👧 Parent Safety Portal';
      contentEl.innerHTML = getParentDashboardHtml(currentBus, currentRoute);
      break;

    case 'DRIVER':
      titleEl.textContent = '🚍 Driver Navigation Console';
      contentEl.innerHTML = getDriverDashboardHtml(currentBus, currentRoute);
      break;

    case 'COORDINATOR':
      titleEl.textContent = '📋 Coordinator Dispatch';
      contentEl.innerHTML = getCoordinatorDashboardHtml();
      break;

    case 'ADMIN':
      titleEl.textContent = '⚙️ Transport Admin Center';
      contentEl.innerHTML = getAdminDashboardHtml();
      break;
  }
}

// Dynamic HTML Template Generators
function getStudentDashboardHtml(bus, route) {
  return `
    <div class="qr-card">
      <div style="font-size: 12px; color: var(--primary-cyan-light); font-weight: 700;">KARUNYA UNIVERSITY BUS PASS</div>
      <div class="qr-box" id="qrcode"></div>
      <div class="student-info">
        <div class="student-name">Albert Livingstan</div>
        <div class="student-reg">Reg No: URK24CS101 • B.Tech CSE</div>
        <div style="font-size: 11px; color: var(--text-muted); margin-top: 4px;">Assigned: Route 01 (Town Hall)</div>
      </div>
    </div>

    <div class="glass-card">
      <div class="card-title">
        <span>📍 LIVE BUS ETA</span>
        <span class="badge badge-green">${bus.status}</span>
      </div>
      <div class="bus-status-grid">
        <div class="stat-box">
          <div class="stat-label">Next Stop</div>
          <div class="stat-value" style="font-size: 14px;" id="val-next-stop">${bus.nextStopName}</div>
        </div>
        <div class="stat-box">
          <div class="stat-label">Estimated Time</div>
          <div class="stat-value" id="val-eta">${bus.etaMins} Mins</div>
        </div>
        <div class="stat-box">
          <div class="stat-label">Current Speed</div>
          <div class="stat-value" id="val-speed">${bus.speedKmH} km/h</div>
        </div>
        <div class="stat-box">
          <div class="stat-label">Seats Available</div>
          <div class="stat-value">${route.capacity - route.boarded} / ${route.capacity}</div>
        </div>
      </div>
    </div>

    <div class="glass-card">
      <div class="card-title">🚍 ROUTE STOPS SCHEDULE</div>
      <div style="display: flex; flex-direction: column; gap: 10px;">
        ${route.stops.map(stop => `
          <div style="display: flex; align-items: center; justify-content: space-between; font-size: 13px; padding: 6px 0; border-bottom: 1px solid rgba(255,255,255,0.05);">
            <div>
              <span style="color: var(--primary-cyan); font-weight: bold;">#${stop.seq}</span>
              <span style="margin-left: 8px;">${stop.name}</span>
            </div>
            <span class="badge ${bus.nextStopName === stop.name ? 'badge-amber' : 'badge-cyan'}">
              ${bus.nextStopName === stop.name ? 'NEXT' : 'SCHEDULED'}
            </span>
          </div>
        `).join('')}
      </div>
    </div>

    <button class="btn-primary" onclick="callDriver('${route.driverPhone}')">
      📞 Call Driver (${route.driverName})
    </button>
  `;
}

function getParentDashboardHtml(bus, route) {
  return `
    <div class="glass-card" style="border-color: var(--accent-emerald);">
      <div class="card-title">
        <span>🛡️ CHILD SAFETY MONITOR</span>
        <span class="badge badge-green">SAFE ON BUS</span>
      </div>
      <div style="font-size: 15px; font-weight: 700; margin-bottom: 4px;">Student: Daniel Livingstan</div>
      <div style="font-size: 12px; color: var(--text-muted);">Boarded at Town Hall Stop • 07:45 AM</div>
    </div>

    <div class="glass-card">
      <div class="card-title">📍 BUS #01 LIVE STATUS</div>
      <div class="bus-status-grid">
        <div class="stat-box">
          <div class="stat-label">Current Location</div>
          <div class="stat-value" style="font-size: 13px;">${bus.nextStopName} Region</div>
        </div>
        <div class="stat-box">
          <div class="stat-label">ETA to Karunya</div>
          <div class="stat-value">${bus.etaMins} Mins</div>
        </div>
      </div>
    </div>

    <div class="glass-card">
      <div class="card-title">🚨 EMERGENCY ASSISTANCE</div>
      <p style="font-size: 12px; color: var(--text-muted); margin-bottom: 14px;">
        Instant notification to Transport Control Room and Bus Driver.
      </p>
      <button class="btn-danger" style="width:100%;" onclick="triggerParentSos()">
        ⚠️ SEND EMERGENCY ALERT TO DRIVER
      </button>
    </div>

    <button class="btn-secondary" onclick="callDriver('${route.driverPhone}')">
      📞 Call Driver (${route.driverName})
    </button>
  `;
}

function getDriverDashboardHtml(bus, route) {
  return `
    <div class="glass-card">
      <div class="card-title">
        <span>⚙️ ROUTE CONTROLS</span>
        <span class="badge badge-cyan">${route.id}</span>
      </div>
      <div style="display: flex; gap: 10px; margin-top: 10px;">
        <button class="btn-primary" style="flex:1;" onclick="toggleBusPause('${bus.id}')">
          ${bus.status === 'PAUSED' ? '▶️ Resume Route' : '⏸️ Pause Route'}
        </button>
        <button class="btn-secondary" style="flex:1;" onclick="openQrModal()">
          📷 Scan Attendance
        </button>
      </div>
    </div>

    <div class="glass-card">
      <div class="card-title">👥 PASSENGER COUNT</div>
      <div style="display: flex; align-items: center; justify-content: space-between; margin-top: 8px;">
        <div>
          <div style="font-size: 28px; font-family: var(--font-heading); font-weight: 800; color: var(--primary-cyan-light);" id="driver-boarded-val">
            ${route.boarded} / ${route.capacity}
          </div>
          <div style="font-size: 12px; color: var(--text-muted);">Students Boarded</div>
        </div>
        <div style="display: flex; gap: 8px;">
          <button class="btn-icon" style="width:44px; height:44px; font-size:20px;" onclick="adjustPassengerCount(-1)">-</button>
          <button class="btn-icon" style="width:44px; height:44px; font-size:20px;" onclick="adjustPassengerCount(1)">+</button>
        </div>
      </div>
    </div>

    <div class="glass-card">
      <div class="card-title">⚠️ DISPATCH ALERTS</div>
      <button class="btn-danger" style="width:100%;" onclick="reportTrafficDelay()">
        📢 Report Road Traffic Delay
      </button>
    </div>
  `;
}

function getCoordinatorDashboardHtml() {
  return `
    <div class="glass-card">
      <div class="card-title">📊 TODAY ATTENDANCE SUMMARY</div>
      <div class="bus-status-grid">
        <div class="stat-box">
          <div class="stat-label">Total Boarded</div>
          <div class="stat-value">213 / 250</div>
        </div>
        <div class="stat-box">
          <div class="stat-label">Active Buses</div>
          <div class="stat-value">5 / 5</div>
        </div>
      </div>
    </div>

    <div class="glass-card">
      <div class="card-title">🚌 FLEET STATUS</div>
      <table class="custom-table">
        <thead>
          <tr>
            <th>Bus</th>
            <th>Speed</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          ${busesState.map(b => `
            <tr onclick="selectBusFromTable('${b.id}')" style="cursor:pointer;">
              <td><b>${b.busNumber}</b></td>
              <td>${b.speedKmH} km/h</td>
              <td><span class="badge ${b.status === 'RUNNING' ? 'badge-green' : 'badge-amber'}">${b.status}</span></td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>

    <button class="btn-primary" onclick="broadcastNotification()">
      📢 Broadcast Notification to Route 01
    </button>
  `;
}

function getAdminDashboardHtml() {
  return `
    <div class="glass-card">
      <div class="card-title">⚡ SIMULATION CONTROL</div>
      <div style="display: flex; gap: 8px; margin-top: 10px;">
        <button class="btn-secondary" onclick="setSimMultiplier(1.0)">1.0x</button>
        <button class="btn-secondary" onclick="setSimMultiplier(2.0)">2.0x</button>
        <button class="btn-secondary" onclick="setSimMultiplier(4.0)">4.0x</button>
      </div>
    </div>

    <div class="glass-card">
      <div class="card-title">🌐 SYSTEM LOGS</div>
      <div style="font-family: monospace; font-size: 11px; color: #94A3B8; background: rgba(0,0,0,0.4); padding: 10px; border-radius: 8px; height: 120px; overflow-y: auto;">
        <div>[07:30:00] Firebase Auth: Initialized</div>
        <div>[07:35:12] GPS Simulator: 5 Buses connected</div>
        <div>[07:45:00] Geofence Trigger: Bus #01 at Town Hall</div>
        <div>[08:00:15] Attendance QR Scan: URK24CS101 verified</div>
      </div>
    </div>
  `;
}

// Generate QR Code Helper
function generateQrCode(text) {
  setTimeout(() => {
    const qrContainer = document.getElementById('qrcode');
    if (qrContainer && window.QRCode) {
      qrContainer.innerHTML = '';
      new QRCode(qrContainer, {
        text: text,
        width: 140,
        height: 140,
        colorDark: "#0F172A",
        colorLight: "#FFFFFF",
        correctLevel: QRCode.CorrectLevel.H
      });
    }
  }, 100);
}

// Live Dashboard Stats Update (called every second during GPS ticks)
function updateLiveDashboardStats() {
  const curBus = busesState.find(b => b.id === currentSelectedBusId);
  if (!curBus) return;

  const nextStopEl = document.getElementById('val-next-stop');
  const etaEl = document.getElementById('val-eta');
  const speedEl = document.getElementById('val-speed');

  if (nextStopEl) nextStopEl.textContent = curBus.nextStopName;
  if (etaEl) etaEl.textContent = `${curBus.etaMins} Mins`;
  if (speedEl) speedEl.textContent = `${curBus.speedKmH} km/h`;
}

// Map Controls Implementation
function toggleMapType() {
  isSatellite = !isSatellite;
  map.setMapTypeId(isSatellite ? 'hybrid' : 'roadmap');
  const btn = document.getElementById('btn-map-type');
  if (btn) btn.classList.toggle('active', isSatellite);
}

function toggleTraffic() {
  isTrafficOn = !isTrafficOn;
  trafficLayer.setMap(isTrafficOn ? map : null);
  const btn = document.getElementById('btn-traffic');
  if (btn) btn.classList.toggle('active', isTrafficOn);
  showToast(isTrafficOn ? '🚦 Google Traffic Layer Enabled' : 'Traffic Layer Disabled');
}

function toggleSimSpeed() {
  if (simSpeed === 1.0) simSpeed = 2.0;
  else if (simSpeed === 2.0) simSpeed = 4.0;
  else simSpeed = 1.0;

  const speedLabel = document.getElementById('speed-btn-label');
  const speedText = document.getElementById('sim-speed-text');

  if (speedLabel) speedLabel.textContent = `${simSpeed.toFixed(1)}x Speed`;
  if (speedText) speedText.textContent = `${simSpeed.toFixed(1)}x Speed • 5 Active Buses`;

  startGpsSimulator();
  showToast(`⚡ Simulation speed set to ${simSpeed.toFixed(1)}x`);
}

function recenterMap() {
  const curBus = busesState.find(b => b.id === currentSelectedBusId) || busesState[0];
  map.panTo({ lat: curBus.lat, lng: curBus.lng });
  map.setZoom(14);
}

// Action Helpers
function callDriver(phone) {
  window.location.href = `tel:${phone}`;
}

function triggerParentSos() {
  showToast('🚨 SOS ALERT SENT to Driver & Transport Office!', 'RED');
}

function toggleBusPause(busId) {
  const bus = busesState.find(b => b.id === busId);
  if (bus) {
    bus.status = bus.status === 'PAUSED' ? 'RUNNING' : 'PAUSED';
    renderRoleDashboard();
    showToast(bus.status === 'PAUSED' ? '⏸️ Bus Paused' : '▶️ Bus Resumed');
  }
}

function adjustPassengerCount(delta) {
  const bus = busesState.find(b => b.id === currentSelectedBusId);
  if (bus) {
    const route = ROUTES.find(r => r.id === bus.routeId);
    if (route) {
      route.boarded = Math.max(0, Math.min(route.capacity, route.boarded + delta));
      const el = document.getElementById('driver-boarded-val');
      if (el) el.textContent = `${route.boarded} / ${route.capacity}`;
    }
  }
}

function reportTrafficDelay() {
  const bus = busesState.find(b => b.id === currentSelectedBusId);
  if (bus) {
    bus.status = 'DELAYED';
    showToast('📢 Delay report broadcasted to all passengers!');
    renderRoleDashboard();
  }
}

function selectBusFromTable(busId) {
  currentSelectedBusId = busId;
  recenterMap();
  renderRoleDashboard();
}

function broadcastNotification() {
  showToast('📢 Broadcast notification sent to 42 students on Route 01!');
}

function setSimMultiplier(val) {
  simSpeed = val;
  toggleSimSpeed();
}

// QR Modal Dialog
function openQrModal() {
  const modal = document.getElementById('qr-modal');
  if (modal) modal.classList.add('active');
}

function closeQrModal() {
  const modal = document.getElementById('qr-modal');
  if (modal) modal.classList.remove('active');
}

function simulateQrScanSuccess() {
  closeQrModal();
  adjustPassengerCount(1);
  showToast('✅ QR Attendance Verified: Albert Livingstan (URK24CS101)');
}

// Toast Notifications Helper
function showToast(message, type = 'CYAN') {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = 'toast';
  if (type === 'RED') toast.style.borderColor = 'var(--accent-rose)';

  toast.innerHTML = `<span>${message}</span>`;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}
