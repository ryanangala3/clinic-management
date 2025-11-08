package com.clinic.model;

public class Doctor {
  private Integer id;
  private String firstName;
  private String lastName;
  private String specialty;
  private String email;
  private String phone;
  private boolean active = true;
  

  public Integer getId() { return id; }
  public void setId(Integer id) { this.id = id; }

  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }

  public String getSpecialty() { return specialty; }
  public void setSpecialty(String specialty) { this.specialty = specialty; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }
}