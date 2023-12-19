package com.enigma.entity;

public class Chair {
    private Integer id;
    private String seat_number;
    private Integer theaterId;

    public Chair() {
    }

    public Chair(String seat_number, Integer theaterId) {
        this.seat_number = seat_number;
        this.theaterId = theaterId;
    }

    public Chair(Integer id, String seat_number, Integer theaterId) {
        this.id = id;
        this.seat_number = seat_number;
        this.theaterId = theaterId;
    }

    @Override
    public String toString() {
        return "Chair{" +
                "id=" + id +
                ", seat_number='" + seat_number + '\'' +
                ", theaterId=" + theaterId +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSeat_number() {
        return seat_number;
    }

    public void setSeat_number(String seat_number) {
        this.seat_number = seat_number;
    }

    public Integer getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(Integer theaterId) {
        this.theaterId = theaterId;
    }
}
