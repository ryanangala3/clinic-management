package com.clinic.ui;

import com.clinic.dao.jdbc.AppointmentDaoJdbc;
import com.clinic.dao.jdbc.DoctorDaoJdbc;
import com.clinic.dao.jdbc.PatientDaoJdbc;
import com.clinic.model.Appointment;
import com.clinic.model.Doctor;
import com.clinic.model.Patient;
import com.clinic.service.AppointmentService;
import com.clinic.service.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentsPanel extends JPanel {
  private final AppointmentService service =
      new AppointmentService(new AppointmentDaoJdbc(), new PatientDaoJdbc(), new DoctorDaoJdbc());

  private final DefaultTableModel model = new DefaultTableModel(
    new Object[]{"ID","Patient","Doctor","Start","End","Reason","Status","Patient_ID","Doctor_ID"}, 0
  );
  private final JTable table = new JTable(model);

  private final JComboBox<ComboItem> cbPatient = new JComboBox<>();
  private final JComboBox<ComboItem> cbDoctor  = new JComboBox<>();
  private final JComboBox<ComboItem> cbFilterDoctor = new JComboBox<>();

  private final JTextField tfStart = new JTextField(); // yyyy-MM-dd HH:mm
  private final JTextField tfEnd   = new JTextField();
  private final JTextField tfReason = new JTextField();
  private final JTextField tfFrom = new JTextField(); // yyyy-MM-dd
private final JTextField tfTo   = new JTextField();


  private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public AppointmentsPanel() {
    setLayout(new BorderLayout(12,12));

    var top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
    top.add(new JLabel("Doctor:"));
    top.add(cbFilterDoctor);
    top.add(new JLabel("From:")); tfFrom.setColumns(10);
    tfFrom.putClientProperty("JTextComponent.placeholderText","yyyy-MM-dd");
    top.add(tfFrom);
    top.add(new JLabel("To:"));   tfTo.setColumns(10);
    tfTo.putClientProperty("JTextComponent.placeholderText","yyyy-MM-dd");
    top.add(tfTo);
    var btnApply = new JButton("Apply");
    top.add(btnApply);
    add(top, BorderLayout.NORTH);

    table.removeColumn(table.getColumnModel().getColumn(7)); // hide Patient_ID
    table.removeColumn(table.getColumnModel().getColumn(7));

    loadPickers();
    loadFilterDoctors();
    refreshTable();

    JPanel form = new JPanel(new GridLayout(0,2,8,8));
    form.add(new JLabel("Patient")); form.add(cbPatient);
    form.add(new JLabel("Doctor"));  form.add(cbDoctor);
    form.add(new JLabel("Start"));   form.add(tfStart);
    form.add(new JLabel("End"));     form.add(tfEnd);
    form.add(new JLabel("Reason"));  form.add(tfReason);

    JButton btnBook = new JButton("Book");
    JButton btnCancel = new JButton("Cancel selected");
    JPanel actions = new JPanel(); actions.add(btnBook); actions.add(btnCancel);

    add(new JScrollPane(table), BorderLayout.CENTER);
    JPanel right = new JPanel(new BorderLayout(8,8));
    right.add(form, BorderLayout.CENTER);
    right.add(actions, BorderLayout.SOUTH);
    add(right, BorderLayout.EAST);

    btnCancel.addActionListener(evt -> {
  int viewRow = table.getSelectedRow();
  if (viewRow < 0) {
    warn("Select an appointment to cancel");
    return;
  }

  int row = table.convertRowIndexToModel(viewRow);
  Integer id = (Integer) model.getValueAt(row, 0);

  Async.run(this,
    () -> service.cancel(id),
    ok -> {
      refreshTable();
      info("Cancelled");
    },
    err -> error("Failed to cancel: " + err.getMessage(), err),
    btnCancel
  );
});

    var root = SwingUtilities.getRootPane(this);
    if (root != null) root.setDefaultButton(btnBook);

 btnBook.addActionListener(evt -> {
  var pItem = (ComboItem) cbPatient.getSelectedItem();
  var dItem = (ComboItem) cbDoctor.getSelectedItem();
  var start = tfStart.getText().trim();
  var end   = tfEnd.getText().trim();

  if (pItem == null) { warn("Select a patient"); return; }
  if (dItem == null) { warn("Select a doctor"); return; }

  java.time.LocalDateTime startDt, endDt;
  try { startDt = java.time.LocalDateTime.parse(start, FMT); } catch (Exception e) { warn("Invalid Start. Use yyyy-MM-dd HH:mm"); tfStart.requestFocus(); return; }
  try { endDt   = java.time.LocalDateTime.parse(end, FMT);   } catch (Exception e) { warn("Invalid End. Use yyyy-MM-dd HH:mm"); tfEnd.requestFocus(); return; }

  var a = new Appointment();
  a.setPatientId(pItem.id());
  a.setDoctorId(dItem.id());
  a.setStartTime(startDt);
  a.setEndTime(endDt);
  a.setReason(tfReason.getText().trim());

  Async.run(this,
    () -> { service.book(a); return true; },
    ok -> { refreshTable(); clearForm(); info("Booked"); },
    err -> {
      if (err instanceof ValidationException ve) { warn(ve.getMessage()); }
      else { error("Failed to book: " + err.getMessage(), err); }
    },
    btnBook
  );
});
}

  public void refreshFromDb() {
  loadPickers();   // if you want pickers fresh too
  refreshTable();  // repopulate the table
}

  private void loadPickers() {
    cbPatient.removeAllItems();
    cbDoctor.removeAllItems();
    List<Patient> patients = new PatientDaoJdbc().findAll();
    List<Doctor> doctors = new DoctorDaoJdbc().findActive();
    for (Patient p : patients) cbPatient.addItem(new ComboItem(p.getId(), p.getFirstName() + " " + p.getLastName()));
    for (Doctor d : doctors) cbDoctor.addItem(new ComboItem(d.getId(), d.getLastName() + ", " + d.getFirstName()));
    tfStart.setText(LocalDateTime.now().withSecond(0).withNano(0).format(FMT));
    tfEnd.setText(LocalDateTime.now().plusMinutes(30).withSecond(0).withNano(0).format(FMT));
  }

  private void loadFilterDoctors() {
  cbFilterDoctor.removeAllItems();
  cbFilterDoctor.addItem(new ComboItem(-1, "All Doctors"));
  for (var d : new DoctorDaoJdbc().findActive()) {
    cbFilterDoctor.addItem(new ComboItem(d.getId(), d.getLastName() + ", " + d.getFirstName()));
  }
}
private void refreshTable() {
  model.setRowCount(0);

  var patients = new PatientDaoJdbc().findAll();
  var doctors  = new DoctorDaoJdbc().findAll();

  var patientNames = new java.util.HashMap<Integer, String>();
  for (var p : patients) patientNames.put(p.getId(), p.getFirstName() + " " + p.getLastName());

  var doctorNames = new java.util.HashMap<Integer, String>();
  for (var d : doctors) doctorNames.put(d.getId(), d.getFirstName() + " " + d.getLastName());

  // read filter values
  Integer filterDoctorId = null;
  var sel = (ComboItem) cbFilterDoctor.getSelectedItem();
  if (sel != null && sel.id() != -1) filterDoctorId = sel.id();

  java.time.LocalDate from = null, to = null;
  try { var s = tfFrom.getText().trim(); if (!s.isEmpty()) from = java.time.LocalDate.parse(s); } catch (Exception ignored) {}
  try { var s = tfTo.getText().trim();   if (!s.isEmpty())   to   = java.time.LocalDate.parse(s); } catch (Exception ignored) {}

  // get data from service
  var appts = service.listUpcoming(); // keep your existing method
  // optional: if you add listBetween(from, to), call it when either date is provided

  for (var a : appts) {
    if (filterDoctorId != null && a.getDoctorId() != filterDoctorId) continue;
    if (from != null && a.getStartTime().toLocalDate().isBefore(from)) continue;
    if (to   != null && a.getStartTime().toLocalDate().isAfter(to))   continue;

    model.addRow(new Object[]{
      a.getId(),
      patientNames.getOrDefault(a.getPatientId(), "Unknown"),
      doctorNames.getOrDefault(a.getDoctorId(), "Unknown"),
      a.getStartTime().format(FMT),
      a.getEndTime().format(FMT),
      a.getReason(),
      a.getStatus(),
      a.getPatientId(),
      a.getDoctorId()
    });
  }
}

  private void clearForm() {
    tfReason.setText("");
    tfStart.setText(LocalDateTime.now().withSecond(0).withNano(0).format(FMT));
    tfEnd.setText(LocalDateTime.now().plusMinutes(30).withSecond(0).withNano(0).format(FMT));
  }

  private record ComboItem(int id, String label) {
    @Override public String toString() { return label; }
  }

  private void info(String m) { JOptionPane.showMessageDialog(this, m, "Info", JOptionPane.INFORMATION_MESSAGE); }
  private void warn(String m) { JOptionPane.showMessageDialog(this, m, "Validation", JOptionPane.WARNING_MESSAGE); }
  private void error(String m, Throwable t) { t.printStackTrace(); JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}