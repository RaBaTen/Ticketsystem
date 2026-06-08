const API_URL = "http://localhost:8080/tickets";

let currentTickets = [];

function getAuthHeader() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    return "Basic " + btoa(username + ":" + password);
}

async function loadTickets() {
    const response = await fetch(API_URL, {
        method: "GET",
        headers: {
            "Authorization": getAuthHeader()
        }
    });

    if (!response.ok) {
        alert("Login failed or request denied");
        return;
    }

    currentTickets = await response.json();

    const table = document.getElementById("ticketTable");
    table.innerHTML = "";

    currentTickets.forEach(ticket => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${ticket.id}</td>
            <td>${ticket.title}</td>
            <td>${ticket.status}</td>
            <td>${ticket.createdBy}</td>
            <td>${ticket.assignedTo ?? "-"}</td>
            <td>
                <button onclick="editTicket(${ticket.id})">
                    Edit
                </button>

                <button onclick="closeTicket(${ticket.id})">
                    Close
                </button>

                <button class="delete-btn"
                        onclick="deleteTicket(${ticket.id})">
                    Delete
                </button>
            </td>
        `;

        table.appendChild(row);
    });
}

async function createTicket() {
    const title = document.getElementById("title").value;
    const description = document.getElementById("description").value;

    const response = await fetch(API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": getAuthHeader()
        },
        body: JSON.stringify({
            title: title,
            description: description
        })
    });

    if (!response.ok) {
        alert("Could not create ticket");
        return;
    }

    clearForm();
    loadTickets();
}

function editTicket(id) {
    const ticket = currentTickets.find(t => t.id === id);

    if (!ticket) {
        alert("Ticket not found");
        return;
    }

    document.getElementById("editTicketId").value = ticket.id;
    document.getElementById("editTitle").value = ticket.title;
    document.getElementById("editDescription").value = ticket.description;
    document.getElementById("editAssignedTo").value =
        ticket.assignedTo ?? "";

    document.getElementById("editModal").style.display = "flex";
}

function closeEditModal() {
    document.getElementById("editModal").style.display = "none";
}

async function saveEdit() {
    const id = document.getElementById("editTicketId").value;

    const title =
        document.getElementById("editTitle").value;

    const description =
        document.getElementById("editDescription").value;

    const assignedTo =
        document.getElementById("editAssignedTo").value;

    const response = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": getAuthHeader()
        },
        body: JSON.stringify({
            title: title,
            description: description,
            assignedTo: assignedTo
        })
    });

    if (!response.ok) {
        alert("Could not update ticket");
        return;
    }

    closeEditModal();
    loadTickets();
}

async function closeTicket(id) {
    const response = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": getAuthHeader()
        },
        body: JSON.stringify({
            status: "Closed"
        })
    });

    if (!response.ok) {
        alert("Could not close ticket");
        return;
    }

    loadTickets();
}

async function deleteTicket(id) {
    const response = await fetch(`${API_URL}/${id}`, {
        method: "DELETE",
        headers: {
            "Authorization": getAuthHeader()
        }
    });

    if (!response.ok) {
        alert("Could not delete ticket");
        return;
    }

    loadTickets();
}

function clearForm() {
    document.getElementById("title").value = "";
    document.getElementById("description").value = "";
}

loadTickets();
