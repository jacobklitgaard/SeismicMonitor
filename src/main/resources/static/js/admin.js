
const API = 'http://localhost:8080/api';

// Runs when page is loaded. Skip login if JWT already exists in localStorage, then start auto-refresh
window.addEventListener('load', () => {
    if (localStorage.getItem('jwt')) {
        if (!getRole()?.includes('ROLE_ADMIN')) {
            localStorage.removeItem('jwt');
            return;
        }
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('admin-content').style.display = 'block';
        loadAllAlerts();
        loadReadings();
        setInterval(() => {
            loadAllAlerts();
        }, 15000);
    }
});

// Authenticate user, store JWT in localStorage and show admin content
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
        if (!getRole()?.includes('ROLE_ADMIN')) {
            document.getElementById('error-msg').textContent = 'Access denied';
            localStorage.removeItem('jwt');
            return;
        }
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('admin-content').style.display = 'block';
        loadAllAlerts();
        loadReadings();
        setInterval(() => {
            loadAllAlerts();
        }, 15000);
    } else {
        document.getElementById('error-msg').textContent = 'Invalid credentials';
    }
}


function getRole() {
    const token = localStorage.getItem('jwt');
    if (!token) return null;
    // ASCII to binary - base64 til string
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.authorities;
}

// Returns Authorization header with JWT for all requests
function getHeaders() {
    return { 'Authorization': localStorage.getItem('jwt') };
}

// Fetches all alerts with report count and average intensity, renders as cards
async function loadAllAlerts() {
    const response = await fetch(`${API}/alerts`, { headers: getHeaders() });
    const alerts = await response.json();
    const container = document.getElementById('all-alerts-container');
    container.innerHTML = '';

    for (const alert of alerts) {
        const countResponse = await fetch(`${API}/alerts/${alert.id}/reports/count`, { headers: getHeaders()});
        const count = await countResponse.json();

        const avgResponse = await fetch(`${API}/alerts/${alert.id}/reports/average-intensity`, { headers: getHeaders() });
        const avg = await avgResponse.json();

        const reportsResponse = await fetch(`${API}/alerts/${alert.id}/reports`, { headers: getHeaders() });
        const reports = await reportsResponse.json();

        const readingsResponse = await fetch(`${API}/alerts/${alert.id}/readings`, { headers: getHeaders() });
        const readings = await readingsResponse.json();

        const card = document.createElement('div');
        card.className = 'alert-card';
        card.innerHTML = `
        <h3>Alert #${alert.id} – ${alert.status}</h3>
        <p>Magnitude: ${alert.estimatedMagnitude.toFixed(1)}</p>
        <p>Epicenter: ${alert.epicenterLatitude.toFixed(4)}, ${alert.epicenterLongitude.toFixed(4)}</p>
        <p>Triggered by:</p>
        <ul>
            ${readings.map(r => `<li>${r.sensor.sensorId} – distance: ${r.estimatedDistanceToEpicenterKm.toFixed(1)} km, magnitude: ${r.estimatedMagnitude.toFixed(2)}</li>`).join('')}
        </ul>
        <p>Location: ${alert.locationName}</p>
        <p>Reports: ${count} (avg. intensity: ${avg.toFixed(1)})</p>
        <ul>
            ${reports.map(r => `<li>${r.reportedBy} – intensity: ${r.intensity}</li>`).join('')}
        </ul>
        <p>Created: ${new Date(alert.createdAt).toLocaleString('da-DK')}</p>
        <div>
            <button onclick="updateStatus(${alert.id}, 'ACTIVE')">Set ACTIVE</button>
            <button onclick="updateStatus(${alert.id}, 'FALSE_ALARM')">Set FALSE_ALARM</button>
            <button onclick="updateStatus(${alert.id}, 'NOT_ACTIVE')">Set NOT_ACTIVE</button>
        </div>
    `;
        container.appendChild(card);
    }
}

// Sends status update to server and reloads alerts on success
async function updateStatus(id, status) {
    const response = await fetch(`${API}/alerts/${id}/status?status=${status}`, {
        method: 'PUT',
        headers: getHeaders()
    });
    if (response.ok) {
        loadAllAlerts();
    } else {
        alert('Invalid status transition');
    }
}

// Fetches all raw sensor readings and renders them in a table
async function loadReadings() {
    const response = await fetch(`${API}/sensor-readings`, { headers: getHeaders() });
    const readings = await response.json();
    const tbody = document.getElementById('readings-body');
    tbody.innerHTML = '';

    for (const r of readings) {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${r.readingId.substring(0, 8)}...</td>
            <td>${r.sensor.sensorId}</td>
            <td style="text-align: left">${r.estimatedDistanceToEpicenterKm.toFixed(1)}</td>
            <td style="text-align: left">${r.estimatedMagnitude.toFixed(2)}</td>
            <td>${r.recordedAt}</td>
        `;
        tbody.appendChild(tr);
    }
}