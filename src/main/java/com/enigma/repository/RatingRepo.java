package com.enigma.repository;

import com.enigma.entity.Customer;
import com.enigma.entity.Rating;

import java.util.List;

public interface RatingRepo {
    List<Rating> getAll();
    void addRating(Rating rating);
    void updateRating(Rating rating);
    void deleteRating(Integer id);
    Rating getRatingById(Integer id);
}
