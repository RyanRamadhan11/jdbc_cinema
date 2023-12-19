package com.enigma.entity;

public class Customer {
    private Integer id;
    private String name;
    private String birthDate;

    public Customer() {
    }

    public Customer(String name, String birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }

    public Customer(Integer id, String name, String birthDate) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthDate='" + birthDate + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
