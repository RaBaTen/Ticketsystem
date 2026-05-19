const API_URL = "http://localhost:8080/tickets";
 
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
 
    const tickets = await response.json();

    const table = document.getElementById("ticketTable");

    table.innerHTML = "";
 
    tickets.forEach(ticket => {

        const row = document.createElement("tr");
 
        row.innerHTML = `
<td>${ticket.id}</td>
<td>${ticket.title}</td>
<td class="status">${ticket.status}</td>
<td>${ticket.createdBy}</td>
<td>${ticket.assignedTo ?? "-"}</td>
<td>
    <button onclick="editTicket(${ticket.id}, '${ticket.title}', '${ticket.description}')">
        Edit
    </button>

    <button onclick="closeTicket(${ticket.id})">
        Close
    </button>

    <button class="delete-btn" onclick="deleteTicket(${ticket.id})">
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
 
    document.getElementById("title").value = "";

    document.getElementById("description").value = "";
 
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

        alert("Could not update ticket");

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
 
loadTickets();
 
