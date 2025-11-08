package com.clinic.ui;

import com.clinic.dao.jdbc.PatientDaoJdbc;
import com.clinic.model.Patient;
import com.clinic.service.PatientService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PatientsPanel extends JPanel {
  private final PatientService service = new PatientService(new PatientDaoJdbc());
  private final javax.swing.table.DefaultTableModel model = new DefaultTableModel(
    new Object[]{"ID","First","Last","DOB","Phone","Email"}, 0
  );
  private final javax.swing.JTable table = new JTable(model);
  private final javax.swing.JTextField tfSearch = new javax.swing.JTextField();
  private final javax.swing.JTextField tfFirst = new javax.swing.JTextField();
  private final javax.swing.JTextField tfLast  = new javax.swing.JTextField();
  private final javax.swing.JTextField tfDob   = new javax.swing.JTextField(); 
  private final javax.swing.JTextField tfPhone = new javax.swing.JTextField();
  private final javax.swing.JTextField tfEmail = new javax.swing.JTextField();
  private Integer editingId = null;

private final JButton btnSave = new JButton("Save");
private final JButton btnDelete = new JButton("Delete");
private final JButton btnClear = new JButton("Clear");

  public PatientsPanel() {
    setLayout(new java.awt.BorderLayout(12,12));

// Top: search
var top = new javax.swing.JPanel(new java.awt.BorderLayout(8,8));
top.add(new javax.swing.JLabel("Search:"), java.awt.BorderLayout.WEST);
top.add(tfSearch, java.awt.BorderLayout.CENTER);
add(top, java.awt.BorderLayout.NORTH);

// Table
table.setRowHeight(28);
table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
table.getTableHeader().setReorderingAllowed(false);
add(new javax.swing.JScrollPane(table), java.awt.BorderLayout.CENTER);

// Right: simple form + actions (adjust layout to your current UI)
var form = new javax.swing.JPanel(new java.awt.GridLayout(0,2,8,8));
tfFirst.putClientProperty("JTextComponent.placeholderText","First name");
tfLast.putClientProperty("JTextComponent.placeholderText","Last name");
tfDob.putClientProperty("JTextComponent.placeholderText","yyyy-MM-dd");
tfPhone.putClientProperty("JTextComponent.placeholderText","Phone");
tfEmail.putClientProperty("JTextComponent.placeholderText","name@example.com");

form.add(new javax.swing.JLabel("First")); form.add(tfFirst);
form.add(new javax.swing.JLabel("Last"));  form.add(tfLast);
form.add(new javax.swing.JLabel("DOB"));   form.add(tfDob);
form.add(new javax.swing.JLabel("Phone")); form.add(tfPhone);
form.add(new javax.swing.JLabel("Email")); form.add(tfEmail);

var actions = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
actions.add(btnSave); actions.add(btnDelete); actions.add(btnClear);

var right = new javax.swing.JPanel(new java.awt.BorderLayout(8,8));
right.setBorder(javax.swing.BorderFactory.createEmptyBorder(8,8,8,8));
right.add(form, java.awt.BorderLayout.CENTER);
right.add(actions, java.awt.BorderLayout.SOUTH);
add(right, java.awt.BorderLayout.EAST);

// Default button on Enter
var root = javax.swing.SwingUtilities.getRootPane(this);
if (root != null) root.setDefaultButton(btnSave);

refreshTable();

// Search on Enter
tfSearch.addActionListener(e -> refreshTable());

// Double-click row to load into form
table.addMouseListener(new java.awt.event.MouseAdapter() {
  @Override public void mouseClicked(java.awt.event.MouseEvent e) {
    if (e.getClickCount() == 2) loadSelectedIntoForm();
  }
});

// Save async
btnSave.addActionListener(e -> {
  var first = tfFirst.getText().trim();
  var last  = tfLast.getText().trim();
  var dob   = tfDob.getText().trim();
  var phone = tfPhone.getText().trim();
  var email = tfEmail.getText().trim();

  if (first.isEmpty()) { warn("First name is required."); tfFirst.requestFocus(); return; }
  if (last.isEmpty())  { warn("Last name is required.");  tfLast.requestFocus();  return; }
  java.time.LocalDate dobDate = null;
  if (!dob.isEmpty()) {
    try { dobDate = java.time.LocalDate.parse(dob); }
    catch (Exception ex) { warn("Use yyyy-MM-dd for DOB."); tfDob.requestFocus(); return; }
  }
  if (!email.isEmpty() && !email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
    warn("Invalid email format.");
    tfEmail.requestFocus(); return;
  }

  var p = new com.clinic.model.Patient();
  p.setId(editingId);
  p.setFirstName(first);
  p.setLastName(last);
  p.setDob(dobDate);
  p.setPhone(phone);
  p.setEmail(email);

  com.clinic.ui.Async.run(this,
    () -> { if (editingId == null) service.create(p); else service.update(p); return true; },
    ok -> { refreshTable(); clearForm(); info("Saved"); },
    err -> error("Failed to save: " + err.getMessage(), err),
    btnSave
  );
});

// Delete async
btnDelete.addActionListener(e -> {
  if (editingId == null) {
    info("Select a patient to delete (double click a row).");
    return;
  }
  int choice = javax.swing.JOptionPane.showConfirmDialog(this,"Delete this patient?","Confirm",javax.swing.JOptionPane.OK_CANCEL_OPTION);
  if (choice != javax.swing.JOptionPane.OK_OPTION) return;

  var id = editingId;
  com.clinic.ui.Async.run(this,
    () -> service.delete(id),
    ok -> { refreshTable(); clearForm(); },
    err -> error("Failed to delete: " + err.getMessage(), err),
    btnDelete
  );
});

// Clear
btnClear.addActionListener(e -> clearForm());

    table.getSelectionModel().addListSelectionListener(e -> {
      if (e.getValueIsAdjusting()) return;
      int row = table.getSelectedRow();
      if (row >= 0) {
        editingId = (Integer) model.getValueAt(row, 0);
        tfFirst.setText(String.valueOf(model.getValueAt(row, 1)));
        tfLast.setText(String.valueOf(model.getValueAt(row, 2)));
        tfDob.setText(String.valueOf(model.getValueAt(row, 3)));
        tfPhone.setText(String.valueOf(model.getValueAt(row, 4)));
        tfEmail.setText(String.valueOf(model.getValueAt(row, 5)));
      }
    });

    btnSave.addActionListener(evt -> {
  var first = tfFirst.getText().trim();
  var last  = tfLast.getText().trim();
  var dob   = tfDob.getText().trim();
  var phone = tfPhone.getText().trim();
  var email = tfEmail.getText().trim();

  if (first.isEmpty()) { warn("First name is required."); tfFirst.requestFocus(); return; }
  if (last.isEmpty())  { warn("Last name is required.");  tfLast.requestFocus(); return; }
  if (!dob.isEmpty()) {
    try { java.time.LocalDate.parse(dob); } catch (Exception e) { warn("Use yyyy-MM-dd for DOB."); tfDob.requestFocus(); return; }
  }
  if (!email.isEmpty() && !email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
    warn("Invalid email format.");
    tfEmail.requestFocus(); return;
  }

  var p = new com.clinic.model.Patient();
  p.setId(editingId);
  p.setFirstName(first);
  p.setLastName(last);
  p.setDob(dob.isEmpty() ? null : java.time.LocalDate.parse(dob));
  p.setPhone(phone);
  p.setEmail(email);

  Async.run(this,
    () -> { if (editingId == null) service.create(p); else service.update(p); return true; },
    ok -> { refreshTable(); clearForm(); info("Saved"); },
    err -> error("Failed to save: " + err.getMessage(), err),
    btnSave
  );
});

    btnDelete.addActionListener(evt -> {
      if (editingId == null) return;
      int choice = JOptionPane.showConfirmDialog(this, "Delete this patient?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
      if (choice == JOptionPane.OK_OPTION) {
        try { service.delete(editingId); refreshTable(); clearForm(); }
        catch (Exception ex) {
          error(ex.getMessage(), ex);
        }
      }
    });

    btnClear.addActionListener(evt -> clearForm());
  }

 private void loadSelectedIntoForm() {
  int viewRow = table.getSelectedRow();
  if (viewRow < 0) return;
  int row = table.convertRowIndexToModel(viewRow);

  editingId = (Integer) model.getValueAt(row, 0);
  tfFirst.setText(String.valueOf(model.getValueAt(row, 1)));
  tfLast.setText(String.valueOf(model.getValueAt(row, 2)));
  tfDob.setText(String.valueOf(model.getValueAt(row, 3)));
  tfPhone.setText(String.valueOf(model.getValueAt(row, 4)));
  tfEmail.setText(String.valueOf(model.getValueAt(row, 5)));
  tfFirst.requestFocus();
}

private void refreshTable() {
  model.setRowCount(0);
  var q = tfSearch.getText().trim();

  java.util.List<com.clinic.model.Patient> pts =
      q.isEmpty() ? service.listAll()
                  : service.listAll().stream().filter(p -> matches(p, q))
                    .collect(java.util.stream.Collectors.toList());

  for (var p : pts) {
    model.addRow(new Object[]{
      p.getId(),
      p.getFirstName(),
      p.getLastName(),
      p.getDob() == null ? "" : p.getDob().toString(),
      p.getPhone(),
      p.getEmail()
    });
  }
}

private boolean matches(com.clinic.model.Patient p, String q) {
  var s = q.toLowerCase();
  return (p.getFirstName()!=null && p.getFirstName().toLowerCase().contains(s)) ||
         (p.getLastName() !=null && p.getLastName().toLowerCase().contains(s))  ||
         (p.getPhone()    !=null && p.getPhone().toLowerCase().contains(s))     ||
         (p.getEmail()    !=null && p.getEmail().toLowerCase().contains(s));
}

private void clearForm() {
  editingId = null;
  tfFirst.setText(""); tfLast.setText(""); tfDob.setText("");
  tfPhone.setText(""); tfEmail.setText("");
  table.clearSelection();
}
  private void info(String m) { JOptionPane.showMessageDialog(this, m, "Info", JOptionPane.INFORMATION_MESSAGE); }
  private void warn(String m) { JOptionPane.showMessageDialog(this, m, "Validation", JOptionPane.WARNING_MESSAGE); }
  private void error(String m, Throwable t) { t.printStackTrace(); JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
  
}