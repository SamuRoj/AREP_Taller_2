document.getElementById("add-activity-btn").addEventListener("click", function() {

    const hora = prompt("Ingresa la hora de la actividad (ej. 08:00 AM):");
    const actividad = prompt("Ingresa el nombre de la actividad:");

    if (hora && actividad) {
        const table = document.getElementById("timetable").getElementsByTagName('tbody')[0];
        const newRow = table.insertRow();

        const cellHora = newRow.insertCell(0);
        const cellActividad = newRow.insertCell(1);
        const cellAcciones = newRow.insertCell(2);

        cellHora.textContent = hora;
        cellActividad.textContent = actividad;

        const deleteBtn = document.createElement("button");
        deleteBtn.textContent = "Eliminar";
        deleteBtn.classList.add("delete-btn");

        deleteBtn.addEventListener("click", function() {
            table.deleteRow(newRow.rowIndex - 1);
        });

        cellAcciones.appendChild(deleteBtn);
    }
});

document.querySelectorAll(".delete-btn").forEach(button => {
    button.addEventListener("click", function() {
        const row = this.closest("tr");
        row.remove();
    });
});
