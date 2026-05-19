package com.example.ticketsystem.controller;

import com.example.ticketsystem.dto.TicketInput;
import com.example.ticketsystem.dto.TicketResponse;
import com.example.ticketsystem.dto.TicketUpdate;
import com.example.ticketsystem.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getTickets(Authentication authentication) {

        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(ticketService.getTickets(username, isAdmin));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable Long ticketId,
            Authentication authentication
    ) {

        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(
                ticketService.getTicketById(ticketId, username, isAdmin)
        );
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketInput input,
            Authentication authentication
    ) {

        String username = authentication.getName();

        TicketResponse created = ticketService.createTicket(input, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody TicketUpdate update,
            Authentication authentication
    ) {

        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(
                ticketService.updateTicket(ticketId, update, username, isAdmin)
        );
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Long ticketId,
            Authentication authentication
    ) {

        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        ticketService.deleteTicket(ticketId, username, isAdmin);

        return ResponseEntity.noContent().build();
    }
}