package com.enigma.repository;

import com.enigma.entity.Customer;
import com.enigma.entity.Film;

import java.util.List;

public interface FilmRepo {
    List<Film> getAll();
    void addFilm(Film film);
    void updateFilm(Film film);
    void deleteFilm(Integer id);
    Film getFilmById(Integer id);
}
