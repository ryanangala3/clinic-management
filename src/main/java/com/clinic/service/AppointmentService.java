package com.clinic.service;

import com.clinic.dao.AppointmentDao;
import com.clinic.dao.DoctorDao;
import com.clinic.dao.PatientDao;
import com.clinic.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentService {
  private final AppointmentDao dao;
  private final PatientDao patientDao;
  private final DoctorDao doctorDao;

  public AppointmentService(AppointmentDao dao, PatientDao patientDao, DoctorDao doctorDao) {
    this.dao = dao;
    this.patientDao = patientDao;
    this.doctorDao = doctorDao;
  }

  public int book(Appointment a) {
    validate(a);
    // existence checks
    patientDao.findById(a.getPatientId()).orElseThrow(() -> new ValidationException("Patient not found"));
    doctorDao.findById(a.getDoctorId()).orElseThrow(() -> new ValidationException("Doctor not found"));
    // overlap rule
    if (dao.existsOverlap(a.getDoctorId(), a.getStartTime(), a.getEndTime())) {
      throw new ValidationException("Doctor already booked during this time");
    }
    a.setStatus("BOOKED");
    return dao.create(a);
  }

  public boolean cancel(int id) { return dao.cancel(id); }
  public boolean complete(int id) { return dao.complete(id); }

    public List<Appointment> listWindow(int back, int ahead) {
    return dao.listWindow(back, ahead);
}

  private void validate(Appointment a) {
    if (a.getPatientId() == null) throw new ValidationException("Patient is required");
    if (a.getDoctorId() == null) throw new ValidationException("Doctor is required");
    if (a.getStartTime() == null || a.getEndTime() == null) throw new ValidationException("Start and end time are required");
    if (!a.getEndTime().isAfter(a.getStartTime())) throw new ValidationException("End time must be after start time");
  }
}