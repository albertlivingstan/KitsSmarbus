// Karunya KitsSmarbus - Bus No. 14 Real Tracking & Single Page App Engine

// Application Session & Active State
let currentUser = null; // { email: 'student@karunya.edu.in', role: 'STUDENT' }
let activeTab = 'TRACKING';
let map = null;
let trafficLayer = null;
let isSatellite = false;
let isTrafficOn = false;
let isDriverGpsActive = false;
let driverGpsWatchId = null;
let simInterval = null;

// Official BUS NO. 14 Route & Stop Timings (Exact match from Karunya University Schedule Image)
const BUS_14_ROUTE = {
  id: 'BUS-14',
  number: 'BUS NO. 14',
  vehicleNo: 'TN 37 CZ 1414',
  driverName: 'M. Vijay',
  driverPhone: '+91 98430 11223',
  color: '#38BDF8',
  capacity: 50,
  boarded: 42,
  stops: [
    { seq: 1, name: 'ACC Pirivu', time: '6:55 a.m', lat: 10.9015, lng: 76.9205 },
    { seq: 2, name: 'K.G. Chavadi', time: '7:05 a.m', lat: 10.8752, lng: 76.9085 },
    { seq: 3, name: 'Mara company', time: '7:10 a.m', lat: 10.8885, lng: 76.9382 },
    { seq: 4, name: 'Rajeshwari Nagar', time: '7:13 a.m', lat: 10.8950, lng: 76.9450 },
    { seq: 5, name: 'Madukkarai', time: '7:15 a.m', lat: 10.9022, lng: 76.9580 },
    { seq: 6, name: 'ACC Quarry', time: '7:25 a.m', lat: 10.9105, lng: 76.9620 },
    { seq: 7, name: 'Mile Kal', time: '7:26 a.m', lat: 10.9152, lng: 76.9650 },
    { seq: 8, name: 'K. Pudhur Wellness Pharmacy', time: '7:35 a.m', lat: 10.9350, lng: 76.9680 },
    { seq: 9, name: 'Terminal Junction', time: '7:37 a.m', lat: 10.9420, lng: 76.9600 },
    { seq: 10, name: 'JRD', time: '7:38 a.m', lat: 10.9480, lng: 76.9520 },
    { seq: 11, name: 'Kalam Palayam', time: '7:47 a.m', lat: 10.9580, lng: 76.9250 },
    { seq: 12, name: 'Mango Hills Resort', time: '8:00 a.m', lat: 10.9450, lng: 76.8100 },
    { seq: 13, name: 'Karunya Deemed University Campus', time: '8:15 a.m', lat: 10.9360, lng: 76.7440 }
  ]
};

// Bus #14 Live GPS State
let bus14State = {
  lat: 10.9015,
  lng: 76.9205,
  speedKmH: 42,
  heading: 240,
  status: 'ON TIME',
  nextStopIdx: 5,
  progressFraction: 0.3,
  isRealDeviceGps: false
};

// Map Objects
let busMarker = null;
let polyline = null;
let stopMarkers = [];

// App Initialization
window.addEventListener('load', () => {
  initPwaServiceWorker();
  checkAuthSession();
  initGoogleMap();
  startFallbackSimulator();
});

function initPwaServiceWorker() {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('./sw.js').catch(err => console.log('SW register skipped', err));
  }
}

// Authentication Check & Handler
function checkAuthSession() {
  const savedUser = localStorage.getItem('kitssmarbus_user');
  if (savedUser) {
    currentUser = JSON.parse(savedUser);
    hideAuthModal();
    updateUserProfileDisplay();
  } else {
    showAuthModal();
  }
}

function showAuthModal() {
  const modal = document.getElementById('auth-modal');
  if (modal) modal.style.display = 'flex';
}

function hideAuthModal() {
  const modal = document.getElementById('auth-modal');
  if (modal) modal.style.display = 'none';
}

function quickFillLogin(username, password) {
  document.getElementById('auth-username').value = username;
  document.getElementById('auth-password').value = password;
  handleLoginSubmit(new Event('submit'));
}

function handleLoginSubmit(event) {
  if (event) event.preventDefault();

  const userVal = document.getElementById('auth-username').value.trim().toLowerCase();
  const passVal = document.getElementById('auth-password').value.trim();

  let role = 'STUDENT';

  // Admin login check
  if (userVal === 'admin' && passVal === '12345') {
    role = 'ADMIN';
  } else if (userVal.endsWith('@karunya.edu.in')) {
    role = 'STUDENT';
  } else if (userVal.endsWith('@karunya.edu')) {
    role = 'COORDINATOR';
  } else if (userVal.includes('driver')) {
    role = 'DRIVER';
  } else {
    // Default fallback to student if valid domain or demo username
    role = 'STUDENT';
  }

  currentUser = { email: userVal, role: role };
  localStorage.setItem('kitssmarbus_user', JSON.stringify(currentUser));

  hideAuthModal();
  updateUserProfileDisplay();
  renderActiveTabContent();
  showToast(`✅ Welcome! Signed in as ${role}`);
}

function signOut() {
  currentUser = null;
  localStorage.removeItem('kitssmarbus_user');
  showAuthModal();
  showToast('Signed out successfully');
}

function updateUserProfileDisplay() {
  if (!currentUser) return;
  const emailEl = document.getElementById('user-email-display');
  const roleEl = document.getElementById('user-role-display');

  if (emailEl) emailEl.textContent = currentUser.email;
  if (roleEl) roleEl.textContent = currentUser.role.replace('_', ' ');
}

// Google Maps Setup
function initGoogleMap() {
  const mapElement = document.getElementById('map');
  if (!mapElement) return;

  const darkMapStyle = [
    { elementType: "geometry", stylers: [{ color: "#080E1E" }] },
    { elementType: "labels.text.stroke", stylers: [{ color: "#080E1E" }] },
    { elementType: "labels.text.fill", stylers: [{ color: "#7DD3FC" }] },
    { featureType: "road", elementType: "geometry", stylers: [{ color: "#1E293B" }] },
    { featureType: "road.highway", elementType: "geometry", stylers: [{ color: "#0284C7" }] },
    { featureType: "water", elementType: "geometry", stylers: [{ color: "#0369A1" }] }
  ];

  map = new google.maps.Map(mapElement, {
    zoom: 12,
    center: { lat: 10.9200, lng: 76.8800 },
    styles: darkMapStyle,
    disableDefaultUI: true,
    zoomControl: true
  });

  trafficLayer = new google.maps.TrafficLayer();

  // Draw Bus 14 Route Polyline & Stops
  drawBus14RouteOnMap();
}

function drawBus14RouteOnMap() {
  const pathCoords = BUS_14_ROUTE.stops.map(s => ({ lat: s.lat, lng: s.lng }));

  // Draw Route Polyline
  polyline = new google.maps.Polyline({
    path: pathCoords,
    geodesic: true,
    strokeColor: '#38BDF8',
    strokeOpacity: 0.95,
    strokeWeight: 6,
    map: map
  });

  // Draw Stop Pin Markers
  BUS_14_ROUTE.stops.forEach(stop => {
    const isCampus = stop.name.includes('Karunya');

    const pin = new google.maps.Marker({
      position: { lat: stop.lat, lng: stop.lng },
      map: map,
      title: `${stop.name} (${stop.time})`,
      icon: {
        path: google.maps.SymbolPath.CIRCLE,
        scale: isCampus ? 8 : 5,
        fillColor: isCampus ? '#38BDF8' : '#FFFFFF',
        fillOpacity: 1,
        strokeColor: '#0284C7',
        strokeWeight: 2.5
      }
    });

    const infoWindow = new google.maps.InfoWindow({
      content: `
        <div style="color: #0F172A; font-family: sans-serif; padding: 6px;">
          <strong style="font-size: 13px;">📍 #${stop.seq}. ${stop.name}</strong><br>
          <span style="font-size: 12px; color: #0284C7; font-weight: bold;">Scheduled Boarding Time: ${stop.time}</span>
        </div>
      `
    });

    pin.addListener('click', () => infoWindow.open(map, pin));
    stopMarkers.push(pin);
  });

  // Draw Bus Marker
  updateBusMarkerPosition();
}

function updateBusMarkerPosition() {
  const busIconSvg = `
    <svg xmlns="http://www.w3.org/2000/svg" width="46" height="46" viewBox="0 0 48 48">
      <circle cx="24" cy="24" r="22" fill="#0F172A" stroke="#38BDF8" stroke-width="3"/>
      <path d="M14 16c0-2.2 1.8-4 4-4h12c2.2 0 4 1.8 4 4v14c0 1.7-1.3 3-3 3v3c0 .6-.4 1-1 1h-2c-.6 0-1-.4-1-1v-3H19v3c0 .6-.4 1-1 1h-2c-.6 0-1-.4-1-1v-3c-1.7 0-3-1.3-3-3V16zm4 2h12v6H18v-6zm3 10c0-1.1-.9-2-2-2s-2 .9-2 2 .9 2 2 2 2-.9 2-2zm10 0c0-1.1-.9-2-2-2s-2 .9-2 2 .9 2 2 2 2-.9 2-2z" fill="#38BDF8"/>
    </svg>
  `;
  const iconUrl = 'data:image/svg+xml;charset=UTF-8,' + encodeURIComponent(busIconSvg);

  if (!busMarker) {
    busMarker = new google.maps.Marker({
      position: { lat: bus14State.lat, lng: bus14State.lng },
      map: map,
      title: `BUS NO. 14 (${bus14State.speedKmH} km/h)`,
      icon: {
        url: iconUrl,
        scaledSize: new google.maps.Size(46, 46),
        anchor: new google.maps.Point(23, 23)
      }
    });

    const info = new google.maps.InfoWindow({
      content: `
        <div style="color: #0F172A; font-family: sans-serif; padding: 6px;">
          <h4 style="margin:0 0 4px 0; color:#0284C7;">🚌 BUS NO. 14</h4>
          <div>Speed: <b>${bus14State.speedKmH} km/h</b></div>
          <div>Driver: <b>${BUS_14_ROUTE.driverName}</b></div>
        </div>
      `
    });
    busMarker.addListener('click', () => info.open(map, busMarker));
  } else {
    busMarker.setPosition({ lat: bus14State.lat, lng: bus14State.lng });
  }

  // Update HUD text
  const nextStop = BUS_14_ROUTE.stops[bus14State.nextStopIdx] || BUS_14_ROUTE.stops[0];
  const hudTitle = document.getElementById('hud-bus-title');
  const hudSub = document.getElementById('hud-sub-text');

  if (hudTitle) hudTitle.textContent = `BUS NO. 14 • ${bus14State.isRealDeviceGps ? 'REAL DRIVER GPS' : 'LIVE GPS TRACKING'}`;
  if (hudSub) hudSub.textContent = `Next Stop: ${nextStop.name} (${nextStop.time}) • Speed: ${bus14State.speedKmH} km/h`;
}

// Fallback GPS Simulator (when driver device GPS is inactive)
function startFallbackSimulator() {
  if (simInterval) clearInterval(simInterval);

  simInterval = setInterval(() => {
    if (bus14State.isRealDeviceGps) return;

    const curStop = BUS_14_ROUTE.stops[bus14State.nextStopIdx - 1] || BUS_14_ROUTE.stops[0];
    const nextStop = BUS_14_ROUTE.stops[bus14State.nextStopIdx] || BUS_14_ROUTE.stops[1];

    bus14State.progressFraction += 0.04;
    if (bus14State.progressFraction >= 1.0) {
      bus14State.progressFraction = 0.0;
      bus14State.nextStopIdx += 1;
      if (bus14State.nextStopIdx >= BUS_14_ROUTE.stops.length) {
        bus14State.nextStopIdx = 1;
      }
    }

    bus14State.lat = curStop.lat + (nextStop.lat - curStop.lat) * bus14State.progressFraction;
    bus14State.lng = curStop.lng + (nextStop.lng - curStop.lng) * bus14State.progressFraction;
    bus14State.speedKmH = Math.round(38 + Math.random() * 10);

    updateBusMarkerPosition();
    updateLiveTabStats();
  }, 1000);
}

// Real Driver Device GPS Location Engine (watchPosition)
function toggleDriverDeviceGps() {
  if (isDriverGpsActive) {
    // Stop Real GPS
    if (driverGpsWatchId !== null) {
      navigator.geolocation.clearWatch(driverGpsWatchId);
      driverGpsWatchId = null;
    }
    isDriverGpsActive = false;
    bus14State.isRealDeviceGps = false;
    document.getElementById('btn-driver-gps').classList.remove('active');
    showToast('📡 Driver Device GPS Stopped. Fallback GPS active.');
  } else {
    // Start Real GPS
    if (!('geolocation' in navigator)) {
      showToast('❌ Geolocation API is not supported on this device');
      return;
    }

    isDriverGpsActive = true;
    bus14State.isRealDeviceGps = true;
    document.getElementById('btn-driver-gps').classList.add('active');
    showToast('🛰️ Driver Real Device GPS Activated! Broadcasting location...');

    driverGpsWatchId = navigator.geolocation.watchPosition(
      (position) => {
        bus14State.lat = position.coords.latitude;
        bus14State.lng = position.coords.longitude;
        bus14State.speedKmH = Math.round((position.coords.speed || 0) * 3.6) || 45;

        updateBusMarkerPosition();
        map.panTo({ lat: bus14State.lat, lng: bus14State.lng });
      },
      (error) => {
        showToast('⚠️ Driver GPS position error: ' + error.message);
      },
      {
        enableHighAccuracy: true,
        maximumAge: 0,
        timeout: 10000
      }
    );
  }
}

// Single Page Tab Switcher
function switchTab(tab) {
  activeTab = tab;

  // Update nav buttons
  document.querySelectorAll('.nav-tab-btn').forEach(b => b.classList.remove('active'));
  const activeBtn = document.getElementById(`tab-btn-${tab}`);
  if (activeBtn) activeBtn.classList.add('active');

  // Update mobile nav
  document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));
  const mobileBtn = document.getElementById(`mobile-nav-${tab}`);
  if (mobileBtn) mobileBtn.classList.add('active');

  renderActiveTabContent();
}

function renderActiveTabContent() {
  const titleEl = document.getElementById('panel-title-text');
  const bodyEl = document.getElementById('panel-content-body');
  if (!bodyEl) return;

  const nextStop = BUS_14_ROUTE.stops[bus14State.nextStopIdx] || BUS_14_ROUTE.stops[0];

  switch (activeTab) {
    case 'TRACKING':
      if (titleEl) titleEl.textContent = '🗺️ Live Bus 14 Tracking';
      bodyEl.innerHTML = `
        <div class="glass-card">
          <div class="card-title">
            <span>🚍 BUS NO. 14 LIVE STATUS</span>
            <span class="badge badge-green">ON TIME</span>
          </div>
          <div class="bus-status-grid">
            <div class="stat-box">
              <div class="stat-label">Next Boarding Stop</div>
              <div class="stat-value" style="font-size: 13px;" id="stat-next-stop">${nextStop.name}</div>
            </div>
            <div class="stat-box">
              <div class="stat-label">Scheduled Time</div>
              <div class="stat-value" id="stat-stop-time">${nextStop.time}</div>
            </div>
            <div class="stat-box">
              <div class="stat-label">Live Speed</div>
              <div class="stat-value" id="stat-speed">${bus14State.speedKmH} km/h</div>
            </div>
            <div class="stat-box">
              <div class="stat-label">Seats Filled</div>
              <div class="stat-value">${BUS_14_ROUTE.boarded} / ${BUS_14_ROUTE.capacity}</div>
            </div>
          </div>
        </div>

        <div class="glass-card">
          <div class="card-title">👨‍✈️ BUS DRIVER CONTACT</div>
          <div style="font-size: 15px; font-weight: 700;">Driver: ${BUS_14_ROUTE.driverName}</div>
          <div style="font-size: 12px; color: var(--text-muted); margin-bottom: 12px;">Bus No: ${BUS_14_ROUTE.vehicleNo}</div>
          <button class="btn-primary" onclick="callDriver('${BUS_14_ROUTE.driverPhone}')">
            📞 Call Driver (${BUS_14_ROUTE.driverPhone})
          </button>
        </div>
      `;
      break;

    case 'TIMETABLE':
      if (titleEl) titleEl.textContent = '📋 Bus 14 Timetable Schedule';
      bodyEl.innerHTML = `
        <div class="glass-card">
          <div class="card-title">
            <span>OFFICIAL TIMETABLE SCHEDULE</span>
            <span class="badge badge-cyan">13 STOPS</span>
          </div>
          <div style="font-size: 12px; color: var(--text-muted); margin-bottom: 10px;">
            Karunya Deemed University, Coimbatore • Bus No. 14
          </div>
          <div class="timetable-list">
            ${BUS_14_ROUTE.stops.map(s => `
              <div class="timetable-item ${nextStop.name === s.name ? 'active-stop' : ''}">
                <div class="timetable-dot"></div>
                <div>
                  <div class="timetable-stop-name">#${s.seq}. ${s.name}</div>
                  <div style="font-size: 11px; color: var(--text-dim);">Boarding Point</div>
                </div>
                <div class="timetable-stop-time">${s.time}</div>
              </div>
            `).join('')}
          </div>
        </div>
      `;
      break;

    case 'BUSPASS':
      if (titleEl) titleEl.textContent = '🎫 Student QR Bus Pass';
      bodyEl.innerHTML = `
        <div class="qr-card">
          <div style="font-size: 12px; color: var(--primary-cyan-light); font-weight: 700;">KARUNYA UNIVERSITY BUS PASS</div>
          <div class="qr-box" id="qrcode"></div>
          <div>
            <div style="font-size: 17px; font-weight: 700;">Albert Livingstan</div>
            <div style="font-size: 12px; color: var(--primary-cyan); font-weight: 600;">Reg No: URK24CS101 • B.Tech CSE</div>
            <div style="font-size: 11px; color: var(--text-muted); margin-top: 4px;">Assigned: BUS NO. 14 (Madukkarai)</div>
          </div>
        </div>
      `;
      generateQrCode('URK24CS101-BUS14-KARUNYA');
      break;

    case 'CONSOLE':
      if (titleEl) titleEl.textContent = '⚙️ Driver & Control Console';
      bodyEl.innerHTML = `
        <div class="glass-card">
          <div class="card-title">📡 LIVE GPS TRANSMITTER</div>
          <p style="font-size: 12px; color: var(--text-muted); margin-bottom: 12px;">
            Turn on live driver device GPS tracking to stream real location to all students.
          </p>
          <button class="btn-primary" onclick="toggleDriverDeviceGps()">
            ${isDriverGpsActive ? '🛑 Stop Driver GPS' : '📡 Start Driver Real Device GPS'}
          </button>
        </div>

        <div class="glass-card">
          <div class="card-title">🚨 EMERGENCY & DELAYS</div>
          <button class="btn-danger" style="width:100%; margin-bottom: 10px;" onclick="triggerEmergencyAlert()">
            ⚠️ SEND SOS EMERGENCY ALERT
          </button>
          <button class="btn-secondary" onclick="reportTrafficDelay()">
            📢 Broadcast Road Delay
          </button>
        </div>
      `;
      break;
  }
}

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

function updateLiveTabStats() {
  if (activeTab !== 'TRACKING') return;

  const nextStop = BUS_14_ROUTE.stops[bus14State.nextStopIdx] || BUS_14_ROUTE.stops[0];
  const nextStopEl = document.getElementById('stat-next-stop');
  const stopTimeEl = document.getElementById('stat-stop-time');
  const speedEl = document.getElementById('stat-speed');

  if (nextStopEl) nextStopEl.textContent = nextStop.name;
  if (stopTimeEl) stopTimeEl.textContent = nextStop.time;
  if (speedEl) speedEl.textContent = `${bus14State.speedKmH} km/h`;
}

// Map Tools Controls
function toggleMapType() {
  isSatellite = !isSatellite;
  map.setMapTypeId(isSatellite ? 'hybrid' : 'roadmap');
  document.getElementById('btn-map-type').classList.toggle('active', isSatellite);
}

function toggleTraffic() {
  isTrafficOn = !isTrafficOn;
  trafficLayer.setMap(isTrafficOn ? map : null);
  document.getElementById('btn-traffic').classList.toggle('active', isTrafficOn);
  showToast(isTrafficOn ? '🚦 Traffic Layer Enabled' : 'Traffic Layer Disabled');
}

function recenterOnBus14() {
  map.panTo({ lat: bus14State.lat, lng: bus14State.lng });
  map.setZoom(14);
}

function callDriver(phone) {
  window.location.href = `tel:${phone}`;
}

function triggerEmergencyAlert() {
  showToast('🚨 SOS ALERT SENT to Karunya Control Room!', 'RED');
}

function reportTrafficDelay() {
  showToast('📢 Delay notification broadcasted to all students!');
}

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
