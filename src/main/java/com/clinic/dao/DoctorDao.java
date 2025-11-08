package com.clinic.dao;

import com.clinic.model.Doctor;
import java.util.List;
import java.util.Optional;

public interface DoctorDao {
  int create(Doctor d);
  Optional<Doctor> findById(int id);
  List<Doctor> findAll();
  List<Doctor> findActive();
  boolean update(Doctor d);
  boolean delete(int id);
}