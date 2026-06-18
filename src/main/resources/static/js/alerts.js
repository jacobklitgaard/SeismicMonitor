const API = 'http://localhost:8080/api';

async function loadActiveAlerts() {
    const res = await fetch(`${API}/alerts/active`);
    const alerts = await res.json();
    const container = document.getElementById('alerts-container');
    container.innerHTML = '';

    if (alerts.length === 0) {
        container.innerHTML = '<p>No active alerts.</p>';
        return;
    }

    for (const alert of alerts) {
        const countRes = await fetch(`${API}/alerts/${alert.id}/reports/count`);
        const count = await countRes.json();

        const card = document.createElement('div');
        card.className = 'alert-card status-active';
        card.innerHTML = `
            <h3>Alert #${alert.id}</h3>
            <p>Magnitude: ${alert.estimatedMagnitude.toFixed(1)}</p>
            <p>Epicenter: ${alert.epicenterLatitude.toFixed(4)}, ${alert.epicenterLongitude.toFixed(4)}</p>
            <p>Reports: ${count}</p>
        `;
        container.appendChild(card);
    }
}

document.getElementById('report-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const alertId = document.getElementById('alert-id').value;
    const intensity = document.getElementById('intensity').value;

    await fetch(`${API}/alerts/${alertId}/reports?intensity=${intensity}`, {
        method: 'POST'
    });

    alert('Report submitted!');
    loadActiveAlerts();
});

loadActiveAlerts();