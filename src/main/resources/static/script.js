function addActivity(){
    const time = document.getElementById("time").value;
    const activity = document.getElementById("activity").value;

    const url = "/add?time=" + time + "&activity=" + activity;

    fetch (url, {method: 'POST'})
        .then(console.log("Activity added."))
        .catch(error => console.error('Error: ', error));

    var tableBody = document.getElementById("timetable").getElementsByTagName("tbody")[0];
    var newRow = document.createElement("tr");

    var cell1 = document.createElement("td");
    var cell2 = document.createElement("td");
    var cell3 = document.createElement("td");

    cell1.textContent = time;
    cell2.textContent = activity;

    var deleteButton = document.createElement("button");
    deleteButton.textContent = "Delete";
    deleteButton.classList.add("delete-btn");

    deleteButton.onclick = function() {
        newRow.remove();
    };

    cell3.appendChild(deleteButton);

    newRow.appendChild(cell1);
    newRow.appendChild(cell2);
    newRow.appendChild(cell3);

    tableBody.appendChild(newRow)
}
