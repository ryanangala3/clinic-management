package com.clinic.service;

import com.clinic.dao.PatientDao;
import com.clinic.model.Patient;

import java.util.List;

public class PatientService {
  private final PatientDao dao;
  public PatientService(PatientDao dao) { this.dao = dao; }

  public int create(Patient p) { validate(p); return dao.create(p); }
  public boolean update(Patient p) {
    if (p.getId() == null) throw new ValidationException("Missing id");
    validate(p);
    return dao.update(p);
  }
  public boolean delete(int id) { return dao.delete(id); }
  public List<Patient> listAll() { return dao.findAll(); }
  public List<Patient> search(String q) { return (q == null || q.isBlank()) ? dao.findAll() : dao.searchByName(q.trim()); }

  private void validate(Patient p) {
    if (p.getFirstName() == null || p.getFirstName().isBlank()) throw new ValidationException("First name is required");
    if (p.getLastName()  == null || p.getLastName().isBlank())  throw new ValidationException("Last name is required");
  }
}
