const API = 'http://localhost:8080/api';

window.addEventListener('load', () => {
    if (localStorage.getItem('jwt')) {
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('admin-content').style.display = 'block';
        loadAllAlerts();
        loadReadings();
        setInterval(() => {
            loadAllAlerts();
            loadReadings();
        }, 15000);
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
        document.getElementById('admin-content').style.display = 'block';
        loadAllAlerts();
        loadReadings();
        setInterval(() => {
            loadAllAlerts();
            loadReadings();
        }, 15000);
    } else {
        document.getElementById('error-msg').textContent = 'Invalid credentials';
    }
}

function getHeaders() {
    return { 'Authorization': localStorage.getItem('jwt') };
}

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

        const card = document.createElement('div');
        card.className = 'alert-card';
        card.innerHTML = `
        <h3>Alert #${alert.id} – ${alert.status}</h3>
        <p>Magnitude: ${alert.estimatedMagnitude.toFixed(1)}</p>
        <p>Epicenter: ${alert.epicenterLatitude.toFixed(4)}, ${alert.epicenterLongitude.toFixed(4)}</p>
        <p>Location: ${alert.locationName}</p>
        <p>Reports: ${count} (avg. intensity: ${avg.toFixed(1)})</p>
        <p>Created: ${alert.createdAt}</p>
        <div>
            <button onclick="updateStatus(${alert.id}, 'ACTIVE')">Set ACTIVE</button>
            <button onclick="updateStatus(${alert.id}, 'FALSE_ALARM')">Set FALSE_ALARM</button>
            <button onclick="updateStatus(${alert.id}, 'NOT_ACTIVE')">Set NOT_ACTIVE</button>
        </div>
    `;
        container.appendChild(card);
    }
}

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

async function loadReadings() {
    const response = await fetch(`${API}/sensor-readings`, { headers: getHeaders() });
    const readings = await response.json();
    const tbody = document.getElementById('readings-body');
    tbody.innerHTML = '';

    for (const r of readings) {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${r.readingId}</td>
            <td>${r.sensor.sensorId}</td>
            <td>${r.estimatedDistanceToEpicenterKm}</td>
            <td>${r.estimatedMagnitude}</td>
            <td>${r.recordedAt}</td>
        `;
        tbody.appendChild(tr);
    }
}