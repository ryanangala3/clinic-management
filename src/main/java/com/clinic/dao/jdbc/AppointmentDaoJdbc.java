package com.clinic.dao.jdbc;

import com.clinic.DBconnection;
import com.clinic.dao.AppointmentDao;
import com.clinic.model.Appointment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentDaoJdbc implements AppointmentDao {

  @Override
  public int create(Appointment a) {
    String sql = "INSERT INTO appointments(patient_id,doctor_id,start_time,end_time,reason,status) VALUES(?,?,?,?,?,?)";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setInt(1, a.getPatientId());
      ps.setInt(2, a.getDoctorId());
      ps.setTimestamp(3, Timestamp.valueOf(a.getStartTime()));
      ps.setTimestamp(4, Timestamp.valueOf(a.getEndTime()));
      ps.setString(5, a.getReason());
      ps.setString(6, a.getStatus() == null ? "BOOKED" : a.getStatus());
      ps.executeUpdate();
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) return rs.getInt(1);
      }
      return -1;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create appointment", e);
    }
  }

  @Override
  public boolean cancel(int id) {
    String sql = "UPDATE appointments SET status='CANCELLED' WHERE id=?";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to cancel appointment", e);
    }
  }

  @Override
  public boolean complete(int id) {
    String sql = "UPDATE appointments SET status='COMPLETED' WHERE id=?";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      return ps.executeUpdate() > 0;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to complete appointment", e);
    }
  }

  @Override
  public Optional<Appointment> findById(int id) {
    String sql = "SELECT * FROM appointments WHERE id=?";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? Optional.of(map(rs)) : Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find appointment", e);
    }
  }

  @Override
  public List<Appointment> findUpcomingForDoctor(int doctorId, LocalDateTime from) {
    String sql = "SELECT * FROM appointments WHERE doctor_id=? AND start_time >= ? AND status='BOOKED' ORDER BY start_time";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      ps.setTimestamp(2, Timestamp.valueOf(from));
      try (ResultSet rs = ps.executeQuery()) {
        List<Appointment> out = new ArrayList<>();
        while (rs.next()) out.add(map(rs));
        return out;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to list upcoming", e);
    }
  }

  @Override
  public boolean existsOverlap(int doctorId, LocalDateTime start, LocalDateTime end) {
    String sql = """
      SELECT 1 FROM appointments
      WHERE doctor_id = ? AND status='BOOKED'
        AND start_time < ? AND end_time > ?
      LIMIT 1
      """;
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, doctorId);
      ps.setTimestamp(2, Timestamp.valueOf(end));
      ps.setTimestamp(3, Timestamp.valueOf(start));
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to check overlap", e);
    }
  }

  @Override
  public List<Appointment> listUpcoming(LocalDateTime from) {
    String sql = "SELECT * FROM appointments WHERE start_time >= ? AND status='BOOKED' ORDER BY start_time";
    try (Connection c = DBconnection.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setTimestamp(1, Timestamp.valueOf(from));
      try (ResultSet rs = ps.executeQuery()) {
        List<Appointment> out = new ArrayList<>();
        while (rs.next()) out.add(map(rs));
        return out;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to list upcoming", e);
    }
  }

  private Appointment map(ResultSet rs) throws SQLException {
    Appointment a = new Appointment();
    a.setId(rs.getInt("id"));
    a.setPatientId(rs.getInt("patient_id"));
    a.setDoctorId(rs.getInt("doctor_id"));
    a.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
    a.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
    a.setReason(rs.getString("reason"));
    a.setStatus(rs.getString("status"));
    return a;
  }
}