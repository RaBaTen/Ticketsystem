package com.example.ticketsystem.controller;

import com.example.ticketsystem.dto.TicketInput;
import com.example.ticketsystem.dto.TicketResponse;
import com.example.ticketsystem.dto.TicketUpdate;
import com.example.ticketsystem.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketInput input,
            @RequestHeader(value = "X-User", defaultValue = "user1") String username
    ) {
        TicketResponse created = ticketService.createTicket(input, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getTickets(
            @RequestHeader(value = "X-User", defaultValue = "user1") String username,
            @RequestHeader(value = "X-Role", defaultValue = "USER") String role
    ) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        return ResponseEntity.ok(ticketService.getTickets(username, isAdmin));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable Long ticketId,
            @RequestHeader(value = "X-User", defaultValue = "user1") String username,
            @RequestHeader(value = "X-Role", defaultValue = "USER") String role
    ) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        return ResponseEntity.ok(ticketService.getTicketById(ticketId, username, isAdmin));
    }

    @PutMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable Long ticketId,
            @RequestBody TicketUpdate update,
            @RequestHeader(value = "X-User", defaultValue = "user1") String username,
            @RequestHeader(value = "X-Role", defaultValue = "USER") String role
    ) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        return ResponseEntity.ok(ticketService.updateTicket(ticketId, update, username, isAdmin));
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Long ticketId,
            @RequestHeader(value = "X-User", defaultValue = "user1") String username,
            @RequestHeader(value = "X-Role", defaultValue = "USER") String role
    ) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        ticketService.deleteTicket(ticketId, username, isAdmin);
        return ResponseEntity.noContent().build();
    }
}
