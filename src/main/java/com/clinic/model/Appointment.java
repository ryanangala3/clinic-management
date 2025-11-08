package com.clinic.model;

import java.time.LocalDateTime;

public class Appointment {
  private Integer id;
  private Integer patientId;
  private Integer doctorId;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String reason;
  private String status; // BOOKED, CANCELLED, COMPLETED

  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }
  public Integer getPatientId() { return patientId; }
  public void setPatientId(Integer patientId) { this.patientId = patientId; }
  public Integer getDoctorId() { return doctorId; }
  public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }
  public LocalDateTime getStartTime() { return startTime; }
  public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
  public LocalDateTime getEndTime() { return endTime; }
  public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
}