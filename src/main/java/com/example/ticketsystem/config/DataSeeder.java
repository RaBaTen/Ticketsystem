package com.example.ticketsystem.config;

import com.example.ticketsystem.model.Ticket;
import com.example.ticketsystem.model.TicketStatus;
import com.example.ticketsystem.repository.TicketRepository;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder {

    public DataSeeder(TicketRepository ticketRepository) {
        if (ticketRepository.count() == 0) {
            Ticket t1 = new Ticket();
            t1.setTitle("Cannot log in");
            t1.setDescription("User cannot log in to the system");
            t1.setStatus(TicketStatus.OPEN);
            t1.setCreatedBy("user1");
            t1.setAssignedTo("agent1");

            Ticket t2 = new Ticket();
            t2.setTitle("Page not loading");
            t2.setDescription("Dashboard page stays blank");
            t2.setStatus(TicketStatus.IN_PROGRESS);
            t2.setCreatedBy("user2");
            t2.setAssignedTo("agent2");

            ticketRepository.save(t1);
            ticketRepository.save(t2);
        }
    }
}
