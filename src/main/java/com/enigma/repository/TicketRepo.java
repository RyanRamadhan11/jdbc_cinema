package com.enigma.repository;

import com.enigma.entity.Chair;
import com.enigma.entity.Ticket;

import java.util.List;

public interface TicketRepo {
    List<Ticket> getAll();
    void addTicket(Ticket ticket);
    void updateTicket(Ticket ticket);
    void deleteTicket(Integer id);
    Ticket getTicketById(Integer id);
}
