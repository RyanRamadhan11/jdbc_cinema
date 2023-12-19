package com.enigma.repository;

import com.enigma.entity.Chair;
import com.enigma.entity.Customer;

import java.util.List;

public interface ChairRepo {
    List<Chair> getAll();
    void addChair(Chair chair);
    void updateChair(Chair chair);
    void deleteChair(Integer id);
    Chair getChairById(Integer id);
}
