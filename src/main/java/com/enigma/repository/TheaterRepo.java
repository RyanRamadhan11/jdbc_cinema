package com.enigma.repository;

import com.enigma.entity.Film;
import com.enigma.entity.Theater;

import java.util.List;

public interface TheaterRepo {
    List<Theater> getAll();
    void addTheater(Theater theater);
    void updateTheater(Theater theater);
    void deleteTheater(Integer id);
    Theater getTheaterById(Integer id);
}
