package com.enigma.entity;

public class Ticket {
    private Integer id;
    private Integer seatId;
    private Integer customerId;

    public Ticket() {
    }

    public Ticket(Integer id, Integer seatId, Integer customerId) {
        this.id = id;
        this.seatId = seatId;
        this.customerId = customerId;
    }

    public Ticket(Integer seatId, Integer customerId) {
        this.seatId = seatId;
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", seatId=" + seatId +
                ", customerId=" + customerId +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
