package com.clinic.dao;

import com.clinic.model.Appointment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentDao {
  int create(Appointment a);
  boolean cancel(int id);
  boolean complete(int id);
  Optional<Appointment> findById(int id);
  List<Appointment> findUpcomingForDoctor(int doctorId, LocalDateTime from);
  boolean existsOverlap(int doctorId, LocalDateTime start, LocalDateTime end);
  List<Appointment> listUpcoming(LocalDateTime from);
  List<Appointment> listWindow(int daysBack, int daysAhead);
}