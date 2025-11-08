package com.clinic.dao.jdbc;

import com.clinic.DBconnection;
import com.clinic.dao.DoctorDao;
import com.clinic.model.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorDaoJdbc implements DoctorDao {

  @Override
  public int create(Doctor d) {
    String sql = "INSERT INTO doctors(first_name,last_name,specialty,email,phone,active) VALUES(?,?,?,?,?,?)";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, d.getFirstName());
      ps.setString(2, d.getLastName());
      ps.setString(3, d.getSpecialty());
      ps.setString(4, d.getEmail());
      ps.setString(5, d.getPhone());
      ps.setBoolean(6, d.isActive());
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) return rs.getInt(1);
      }
      return -1;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create doctor", e);
    }
  }

  @Override
  public Optional<Doctor> findById(int id) {
    String sql = "SELECT * FROM doctors WHERE id=?";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? Optional.of(map(rs)) : Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find doctor", e);
    }
  }

  @Override
  public List<Doctor> findAll() {
    String sql = "SELECT * FROM doctors ORDER BY last_name, first_name";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      List<Doctor> out = new ArrayList<>();
      while (rs.next()) out.add(map(rs));
      return out;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to list doctors", e);
    }
  }

  @Override
  public List<Doctor> findActive() {
    String sql = "SELECT * FROM doctors WHERE active = 1 ORDER BY last_name, first_name";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      List<Doctor> out = new ArrayList<>();
      while (rs.next()) out.add(map(rs));
      return out;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to list active doctors", e);
    }
  }

  @Override
  public boolean update(Doctor d) {
    String sql = "UPDATE doctors SET first_name=?, last_name=?, specialty=?, email=?, phone=?, active=? WHERE id=?";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, d.getFirstName());
      ps.setString(2, d.getLastName());
      ps.setString(3, d.getSpecialty());
      ps.setString(4, d.getEmail());
      ps.setString(5, d.getPhone());
      ps.setBoolean(6, d.isActive());
      ps.setInt(7, d.getId());
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to update doctor", e);
    }
  }

  @Override
  public boolean delete(int id) {
    String sql = "DELETE FROM doctors WHERE id=?";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete doctor", e);
    }
  }

  private Doctor map(ResultSet rs) throws SQLException {
    Doctor d = new Doctor();
    d.setId(rs.getInt("id"));
    d.setFirstName(rs.getString("first_name"));
    d.setLastName(rs.getString("last_name"));
    d.setSpecialty(rs.getString("specialty"));
    d.setEmail(rs.getString("email"));
    d.setPhone(rs.getString("phone"));
    d.setActive(rs.getBoolean("active"));
    return d;
  }
}