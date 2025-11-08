package com.clinic.dao.jdbc;

import com.clinic.DBconnection;
import com.clinic.dao.PatientDao;
import com.clinic.model.Patient;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientDaoJdbc implements PatientDao {

  @Override
  public int create(Patient p) {
    String sql = "INSERT INTO patients(first_name,last_name,dob,phone,email,notes) VALUES(?,?,?,?,?,?)";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, p.getFirstName());
      ps.setString(2, p.getLastName());
      if (p.getDob() != null) ps.setDate(3, Date.valueOf(p.getDob())); else ps.setNull(3, Types.DATE);
      ps.setString(4, p.getPhone());
      ps.setString(5, p.getEmail());
      ps.setString(6, p.getNotes());
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) return rs.getInt(1);
      }
      return -1;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create patient", e);
    }
  }

  @Override
  public Optional<Patient> findById(int id) {
    String sql = "SELECT * FROM patients WHERE id=?";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? Optional.of(map(rs)) : Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find patient", e);
    }
  }

  @Override
  public List<Patient> findAll() {
    String sql = "SELECT * FROM patients ORDER BY last_name, first_name";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      List<Patient> out = new ArrayList<>();
      while (rs.next()) out.add(map(rs));
      return out;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to list patients", e);
    }
  }

  @Override
  public List<Patient> searchByName(String q) {
    String like = "%" + q + "%";
    String sql = "SELECT * FROM patients WHERE first_name LIKE ? OR last_name LIKE ? ORDER BY last_name, first_name";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, like);
      ps.setString(2, like);
      try (ResultSet rs = ps.executeQuery()) {
        List<Patient> out = new ArrayList<>();
        while (rs.next()) out.add(map(rs));
        return out;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to search patients", e);
    }
  }

  @Override
  public boolean update(Patient p) {
    String sql = "UPDATE patients SET first_name=?, last_name=?, dob=?, phone=?, email=?, notes=? WHERE id=?";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, p.getFirstName());
      ps.setString(2, p.getLastName());
      if (p.getDob() != null) ps.setDate(3, Date.valueOf(p.getDob())); else ps.setNull(3, Types.DATE);
      ps.setString(4, p.getPhone());
      ps.setString(5, p.getEmail());
      ps.setString(6, p.getNotes());
      ps.setInt(7, p.getId());
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to update patient", e);
    }
  }

  @Override
  public boolean delete(int id) {
    String sql = "DELETE FROM patients WHERE id=?";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete patient", e);
    }
  }

  private Patient map(ResultSet rs) throws SQLException {
    Patient p = new Patient();
    p.setId(rs.getInt("id"));
    p.setFirstName(rs.getString("first_name"));
    p.setLastName(rs.getString("last_name"));
    Date d = rs.getDate("dob");
    p.setDob(d != null ? d.toLocalDate() : null);
    p.setPhone(rs.getString("phone"));
    p.setEmail(rs.getString("email"));
    p.setNotes(rs.getString("notes"));
    return p;
  }
}