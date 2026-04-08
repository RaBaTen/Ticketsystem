package com.example.ticketsystem.service;

import com.example.ticketsystem.dto.TicketInput;
import com.example.ticketsystem.dto.TicketResponse;
import com.example.ticketsystem.dto.TicketUpdate;
import com.example.ticketsystem.exception.ForbiddenOperationException;
import com.example.ticketsystem.exception.ResourceNotFoundException;
import com.example.ticketsystem.model.Ticket;
import com.example.ticketsystem.model.TicketStatus;
import com.example.ticketsystem.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public TicketResponse createTicket(TicketInput input, String username) {
        Ticket ticket = new Ticket();
        ticket.setTitle(input.getTitle());
        ticket.setDescription(input.getDescription());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setCreatedBy(username);
        ticket.setAssignedTo(null);

        return toResponse(ticketRepository.save(ticket));
    }

    public List<TicketResponse> getTickets(String username, boolean isAdmin) {
        List<Ticket> tickets = isAdmin
                ? ticketRepository.findAll()
                : ticketRepository.findByCreatedBy(username);

        return tickets.stream().map(this::toResponse).toList();
    }

    public TicketResponse getTicketById(Long id, String username, boolean isAdmin) {
        Ticket ticket = findTicket(id);
        checkAccess(ticket, username, isAdmin);
        return toResponse(ticket);
    }

    public TicketResponse updateTicket(Long id, TicketUpdate update, String username, boolean isAdmin) {
        Ticket ticket = findTicket(id);
        checkAccess(ticket, username, isAdmin);

        if (update.getTitle() != null && !update.getTitle().isBlank()) {
            ticket.setTitle(update.getTitle());
        }

        if (update.getDescription() != null && !update.getDescription().isBlank()) {
            ticket.setDescription(update.getDescription());
        }

        if (update.getAssignedTo() != null) {
            if (!isAdmin) {
                throw new ForbiddenOperationException("Only admins can assign tickets");
            }
            ticket.setAssignedTo(update.getAssignedTo());
        }

        if (update.getStatus() != null) {
            ticket.setStatus(parseStatus(update.getStatus()));
        }

        return toResponse(ticketRepository.save(ticket));
    }

    public void deleteTicket(Long id, String username, boolean isAdmin) {
        Ticket ticket = findTicket(id);
        checkAccess(ticket, username, isAdmin);
        ticketRepository.delete(ticket);
    }

    private Ticket findTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket with id " + id + " not found"));
    }

    private void checkAccess(Ticket ticket, String username, boolean isAdmin) {
        if (!isAdmin && !ticket.getCreatedBy().equals(username)) {
            throw new ForbiddenOperationException("You can only access your own tickets");
        }
    }

    private TicketStatus parseStatus(String value) {
        return switch (value.trim().toLowerCase()) {
            case "open" -> TicketStatus.OPEN;
            case "in progress", "in_progress" -> TicketStatus.IN_PROGRESS;
            case "closed" -> TicketStatus.CLOSED;
            default -> throw new IllegalArgumentException("Invalid status: " + value);
        };
    }

    private TicketResponse toResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setStatus(formatStatus(ticket.getStatus()));
        response.setCreatedBy(ticket.getCreatedBy());
        response.setAssignedTo(ticket.getAssignedTo());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        return response;
    }

    private String formatStatus(TicketStatus status) {
        return switch (status) {
            case OPEN -> "Open";
            case IN_PROGRESS -> "In Progress";
            case CLOSED -> "Closed";
        };
    }
}
