function addActivity(){
    const time = document.getElementById("time").value;
    const activity = document.getElementById("activity").value;

    const url = "/api/activity?time=" + time + "&activity=" + activity;

    fetch(url, { method: 'POST' })
        .then(response => {
            if (!response.ok) {
                throw new Error('Error al agregar la actividad');
            }
            console.log("Activity added.");
            return getActivities();
        })
        .catch(error => {
            console.error('Error: ', error);
        });

}

function getActivities(){
    let url = "/api/activity";

    fetch (url, {method: 'GET'}).then(response => {
        if (!response.ok) {
            throw new Error('Error al obtener datos del servidor');
        }
        return response.json();
    })
    .then(data => {
        const tableBody = document.querySelector('#timetable tbody');
        tableBody.innerHTML = '';
        console.log("Datos obtenidos: ", data);

        data.forEach(item => {
            const row = document.createElement('tr');

            const timeCell = document.createElement('td');
            timeCell.textContent = item.time;
            row.appendChild(timeCell);

            const activityCell = document.createElement('td');
            activityCell.textContent = item.activity;
            row.appendChild(activityCell);

            const deleteCell = document.createElement('td');
            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Eliminar';
            deleteCell.appendChild(deleteButton);
            row.appendChild(deleteCell);

            tableBody.appendChild(row);

            deleteButton.addEventListener('click', () => {
                const time = row.querySelector('td:first-child').textContent;
                let url = "/api/activity?time=" + time;
                fetch(url, {method: 'DELETE'})
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Error al eliminar el elemento del servidor');
                    }
                    row.remove();
                })
                .catch(error => {
                    console.error('Hubo un problema con la eliminación:', error);
                });
            });
        });
    })
}
