package com.clinic.dao;

import com.clinic.model.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientDao {
  int create(Patient p);
  Optional<Patient> findById(int id);
  List<Patient> findAll();
  List<Patient> searchByName(String q);
  boolean update(Patient p);
  boolean delete(int id);
}