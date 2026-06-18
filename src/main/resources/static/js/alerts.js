const API = 'http://localhost:8080/api';
let credentials = null;

async function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    credentials = btoa(`${username}:${password}`);

    const response = await fetch(`${API}/alerts/active`, {
        headers: { 'Authorization': `Basic ${credentials}` }
    });

    if (response.ok) {
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('main-content').style.display = 'block';
        loadActiveAlerts();
    } else {
        document.getElementById('error-msg').textContent = 'Invalid credentials';
        credentials = null;
    }
}

async function loadActiveAlerts() {
    const response = await fetch(`${API}/alerts/active`, {
        headers: { 'Authorization': `Basic ${credentials}` }
    });
    const alerts = await response.json();
    const container = document.getElementById('alerts-container');
    container.innerHTML = '';

    if (alerts.length === 0) {
        container.innerHTML = '<p>No active alerts.</p>';
        return;
    }

    for (const alert of alerts) {
        const countResponse = await fetch(`${API}/alerts/${alert.id}/reports/count`, {
            headers: { 'Authorization': `Basic ${credentials}` }
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
}

document.getElementById('report-form')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    const alertId = document.getElementById('alert-id').value;
    const intensity = document.getElementById('intensity').value;

    await fetch(`${API}/alerts/${alertId}/reports?intensity=${intensity}`, {
        method: 'POST',
        headers: { 'Authorization': `Basic ${credentials}` }
    });

    alert('Report submitted!');
    loadActiveAlerts();
});