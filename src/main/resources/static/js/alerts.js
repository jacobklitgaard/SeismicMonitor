const API = 'http://localhost:8080/api';

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
        sessionStorage.setItem('jwt', token);
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('main-content').style.display = 'block';
        loadActiveAlerts();
    } else {
        document.getElementById('error-msg').textContent = 'Invalid credentials';
    }
}

function getHeaders() {
    return { 'Authorization': sessionStorage.getItem('jwt') };
}

async function loadActiveAlerts() {
    const response = await fetch(`${API}/alerts/active`, { headers: getHeaders() });
    const alerts = await response.json();
    const container = document.getElementById('alerts-container');
    container.innerHTML = '';

    if (alerts.length === 0) {
        container.innerHTML = '<p>No active alerts.</p>';
        return;
    }

    initMap();

    for (const alert of alerts) {
        const countResponse = await fetch(`${API}/alerts/${alert.id}/reports/count`, {
            headers: getHeaders()
        });
        const count = await countResponse.json();

        const card = document.createElement('div');
        card.className = 'alert-card status-active';
        card.innerHTML = `
            <h3>Alert #${alert.id}</h3>
            <p>Magnitude: ${alert.estimatedMagnitude.toFixed(1)}</p>
            <p>Epicenter: ${alert.epicenterLatitude.toFixed(4)}, ${alert.epicenterLongitude.toFixed(4)}</p>
            <p>Location: ${alert.locationName}</p>
            <p>Reports: ${count}</p>
        `;
        container.appendChild(card);
    }

    addMarkersToMap(alerts);
}

let map = null;

function initMap() {
    if (map) return;
    map = L.map('map').setView([55.0, 12.0], 5);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(map);
}

function addMarkersToMap(alerts) {
    if (!map) return;
    alerts.forEach(alert => {
        L.marker([alert.epicenterLatitude, alert.epicenterLongitude])
            .addTo(map)
            .bindPopup(`
                <b>Alert #${alert.id}</b><br>
                Magnitude: ${alert.estimatedMagnitude.toFixed(1)}<br>
                ${alert.locationName}
            `);
    });
}

document.getElementById('report-form')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const alertId = document.getElementById('alert-id').value;
    const intensity = document.getElementById('intensity').value;

    await fetch(`${API}/alerts/${alertId}/reports?intensity=${intensity}`, {
        method: 'POST',
        headers: getHeaders()
    });

    alert('Report submitted!');
    loadActiveAlerts();
});