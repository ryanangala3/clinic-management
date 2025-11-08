package com.clinic.service;

import com.clinic.dao.DoctorDao;
import com.clinic.model.Doctor;

import java.util.List;
import java.util.Optional;

public class DoctorService {
  private final DoctorDao dao;
  public DoctorService(DoctorDao dao) { this.dao = dao; }

  public int create(Doctor d) {
    validate(d);
    return dao.create(d);
  }

  public boolean update(Doctor d) {
    if (d.getId() == null) throw new ValidationException("Missing id");
    validate(d);
    return dao.update(d);
  }

  public boolean delete(int id) { return dao.delete(id); }

  public List<Doctor> listAll() { return dao.findAll(); }
  public List<Doctor> listActive() { return dao.findActive(); }
  public Optional<Doctor> findById(int id) { return dao.findById(id); }

  private void validate(Doctor d) {
    if (d.getFirstName() == null || d.getFirstName().isBlank())
      throw new ValidationException("First name is required");
    if (d.getLastName() == null || d.getLastName().isBlank())
      throw new ValidationException("Last name is required");
    if (d.getEmail() != null && !d.getEmail().isBlank() && !d.getEmail().contains("@"))
      throw new ValidationException("Email looks invalid");
  }
}
