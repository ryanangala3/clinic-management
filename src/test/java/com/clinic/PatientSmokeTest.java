package com.clinic;

import com.clinic.dao.jdbc.PatientDaoJdbc;
import com.clinic.model.Patient;
import com.clinic.service.PatientService;

import java.time.LocalDate;

public class PatientSmokeTest {
  public static void main(String[] args) {
    PatientService service = new PatientService(new PatientDaoJdbc()); // ← explicit type

    Patient p = new Patient();
    p.setFirstName("Test");
    p.setLastName("User");
    p.setDob(LocalDate.of(1990, 1, 1));
    p.setEmail("test.user@example.com");

    int id = service.create(p);
    System.out.println("Created patient id=" + id);

    service.search("").forEach(pt ->
      System.out.println(pt.getId() + " " + pt.getFirstName() + " " + pt.getLastName())
    );

    p.setId(id);
    p.setPhone("604-555-0000");
    service.update(p);
    service.delete(id);
    System.out.println("Deleted patient id=" + id);
  }
}