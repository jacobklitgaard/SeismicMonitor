
const API = 'http://localhost:8080/api';
let map;
let mapInitialized = false;
let markers = [];

window.addEventListener('load', () => {
    if (localStorage.getItem('jwt')) {
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('main-content').style.display = 'block';
        loadActiveAlerts();
        setInterval(loadActiveAlerts, 15000);
    }
});

async function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const response = await fetch(`${API}/dologin`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });

    if (response.ok) {
        const token = response.headers.get('Authorization');
        localStorage.setItem('jwt', token);
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('main-content').style.display = 'block';
        loadActiveAlerts();
        setInterval(loadActiveAlerts, 15000);
    } else {
        document.getElementById('error-msg').textContent = 'Invalid credentials';
    }
}

function getHeaders() {
    return { 'Authorization': localStorage.getItem('jwt') };
}

async function loadActiveAlerts() {
    const response = await fetch(`${API}/alerts/active`, { headers: getHeaders() });
    const alerts = await response.json();
    const container = document.getElementById('alerts-container');

    const noAlertsMsg = container.querySelector('p');       // ← her
    if (noAlertsMsg && alerts.length > 0) noAlertsMsg.remove();

    const alertCards = container.querySelectorAll('.alert-card');
    const existingIds = new Set([...alertCards].map(card => card.dataset.alertId));
    const newIds = new Set(alerts.map(alert => String(alert.id)));

    for (const id of existingIds) {
        if (!newIds.has(id)) {
            container.querySelector(`[data-alert-id="${id}"]`).remove();
        }
    }

    if (alerts.length === 0) {
        container.innerHTML = '<p>No active alerts.</p>';
        return;
    }

    initMap();

    for (const alert of alerts) {
        const countResponse = await fetch(`${API}/alerts/${alert.id}/reports/count`, { headers: getHeaders() });
        const count = await countResponse.json();

        if (existingIds.has(String(alert.id))) {
            document.getElementById(`count-${alert.id}`).textContent = count;
        } else {
            const card = document.createElement('div');
            card.className = 'alert-card status-active';
            card.dataset.alertId = alert.id;
            card.innerHTML = `
                <h3>Alert #${alert.id}</h3>
                <p>Magnitude: ${alert.estimatedMagnitude.toFixed(1)}</p>
                <p>Epicenter: ${alert.epicenterLatitude.toFixed(4)}, ${alert.epicenterLongitude.toFixed(4)}</p>
                <p>Location: ${alert.locationName}</p>
                <p>Reports: <span id="count-${alert.id}">${count}</span></p>
                <div class="report-section">
                    <p><strong>Report shaking:</strong></p>
                    <input type="number" id="intensity-${alert.id}" min="1" max="10" placeholder="Intensity (1-10)" style="width: 140px;">
                    <button onclick="submitReport(${alert.id})">Submit Report</button>
                </div>
            `;
            container.appendChild(card);
        }
    }

    addMarkersToMap(alerts);
}

async function submitReport(alertId) {
    const intensity = document.getElementById(`intensity-${alertId}`).value;

    if (!intensity || intensity < 1 || intensity > 10) {
        alert('Please enter an intensity between 1 and 10');
        return;
    }

    const response = await fetch(`${API}/alerts/${alertId}/reports?intensity=${intensity}`, {
        method: 'POST',
        headers: getHeaders()
    });

    if (response.ok) {
        const countResponse = await fetch(`${API}/alerts/${alertId}/reports/count`, { headers: getHeaders() });
        const count = await countResponse.json();
        document.getElementById(`count-${alertId}`).textContent = count;
        alert('Report submitted!');
    } else {
        const msg = await response.text();
        alert(msg);
    }
}

function initMap() {
    if (mapInitialized) return;
    mapInitialized = true;

    map = L.map('map').setView([55.0, 12.0], 5);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(map);
}

function addMarkersToMap(alerts) {
    if (!mapInitialized) return;
    markers.forEach(m => m.remove());
    markers = [];
    alerts.forEach(alert => {
        const marker = L.marker([alert.epicenterLatitude, alert.epicenterLongitude])
            .addTo(map)
            .bindPopup(`<b>Alert #${alert.id}</b><br>Magnitude: ${alert.estimatedMagnitude.toFixed(1)}<br>${alert.locationName}`);
        markers.push(marker);
    });
}