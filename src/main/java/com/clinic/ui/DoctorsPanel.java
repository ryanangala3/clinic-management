package com.clinic.ui;

import com.clinic.dao.jdbc.DoctorDaoJdbc;
import com.clinic.model.Doctor;
import com.clinic.service.DoctorService;
import com.clinic.service.ValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.event.TableModelEvent;
import com.clinic.ui.Async;

public class DoctorsPanel extends JPanel {

  // Service
  private final DoctorService service = new DoctorService(new DoctorDaoJdbc());

  // Table model with Active column editable
  private final DefaultTableModel model = new DefaultTableModel(
      new Object[]{"ID","First","Last","Specialty","Email","Phone","Active"}, 0) {
    @Override public boolean isCellEditable(int row, int col) { return col == 6; } // only Active
    @Override public Class<?> getColumnClass(int col) { return col == 6 ? Boolean.class : Object.class; }
  };
  private final JTable table = new JTable(model);

  // Search
  private final JTextField tfSearch = new JTextField();

  // Form fields
  private final JTextField tfFirst = new JTextField();
  private final JTextField tfLast  = new JTextField();
  private final JTextField tfSpec  = new JTextField();
  private final JTextField tfEmail = new JTextField();
  private final JTextField tfPhone = new JTextField();
  private final JCheckBox  cbActive = new JCheckBox("Active", true);

  // Buttons
  private final JButton btnSave = new JButton("Save");
  private final JButton btnDelete = new JButton("Delete");
  private final JButton btnClear = new JButton("Clear");

  // Tracking edit state
  private Integer editingId = null;

  public DoctorsPanel() {
    setLayout(new BorderLayout(12,12));

    // Top bar: search
    var top = new JPanel(new BorderLayout(8,8));
    top.add(new JLabel("Search:"), BorderLayout.WEST);
    top.add(tfSearch, BorderLayout.CENTER);
    add(top, BorderLayout.NORTH);

    // Table
    table.setRowHeight(28);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.getTableHeader().setReorderingAllowed(false);
    add(new JScrollPane(table), BorderLayout.CENTER);

    // Right side: form + actions
    var form = new JPanel(new GridLayout(0,2,8,8));
    tfFirst.putClientProperty("JTextComponent.placeholderText", "First name");
    tfLast.putClientProperty("JTextComponent.placeholderText", "Last name");
    tfSpec.putClientProperty("JTextComponent.placeholderText", "Specialty");
    tfEmail.putClientProperty("JTextComponent.placeholderText", "name@example.com");
    tfPhone.putClientProperty("JTextComponent.placeholderText", "Phone");

    form.add(new JLabel("First")); form.add(tfFirst);
    form.add(new JLabel("Last"));  form.add(tfLast);
    form.add(new JLabel("Specialty")); form.add(tfSpec);
    form.add(new JLabel("Email")); form.add(tfEmail);
    form.add(new JLabel("Phone")); form.add(tfPhone);
    form.add(new JLabel("")); form.add(cbActive);

    var actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    actions.add(btnSave); actions.add(btnDelete); actions.add(btnClear);

    var right = new JPanel(new BorderLayout(8,8));
    right.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
    right.add(form, BorderLayout.CENTER);
    right.add(actions, BorderLayout.SOUTH);
    add(right, BorderLayout.EAST);

    // Default button on Enter
    var root = SwingUtilities.getRootPane(this);
    if (root != null) root.setDefaultButton(btnSave);

    // Listeners
    wireListeners();

    // Initial load
    refreshTable();
  }

  private void wireListeners() {
    // Search on Enter or when user presses a small delay you can add later
    tfSearch.addActionListener(e -> refreshTable());

    // Double click row to edit
    table.addMouseListener(new java.awt.event.MouseAdapter() {
      @Override public void mouseClicked(java.awt.event.MouseEvent e) {
        if (e.getClickCount() == 2) loadSelectedIntoForm();
      }
    });

    // Persist toggle of Active when user edits the checkbox cell
model.addTableModelListener(e -> {
  if (e.getType() != TableModelEvent.UPDATE) return;
  if (e.getColumn() != 6) return;                 // Active column
  int r = e.getFirstRow();
  if (r < 0) return;

  Integer id = (Integer) model.getValueAt(r, 0);
  Boolean active = (Boolean) model.getValueAt(r, 6);
  if (id == null) return;

  // Explicit generic helps inference in some IDEs
  Async.<Boolean>run(DoctorsPanel.this,
      () -> {
        var docOpt = service.findById(id);
        if (docOpt.isEmpty()) return false;
        var d = docOpt.get();
        d.setActive(Boolean.TRUE.equals(active));
        return service.update(d);
      },
      ok -> {}, // silent success
      err -> error("Failed to toggle active: " + err.getMessage(), err)
  );
});

    // Save
    btnSave.addActionListener(e -> {
      var first = tfFirst.getText().trim();
      var last  = tfLast.getText().trim();
      var spec  = tfSpec.getText().trim();
      var email = tfEmail.getText().trim();
      var phone = tfPhone.getText().trim();

      if (first.isEmpty()) { warn("First name is required."); tfFirst.requestFocus(); return; }
      if (last.isEmpty())  { warn("Last name is required.");  tfLast.requestFocus(); return; }
      if (!email.isEmpty() && !email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
        warn("Invalid email format."); tfEmail.requestFocus(); return;
      }

      var d = new Doctor();
      d.setId(editingId);
      d.setFirstName(first);
      d.setLastName(last);
      d.setSpecialty(spec);
      d.setEmail(email);
      d.setPhone(phone);
      d.setActive(cbActive.isSelected());

      Async.run(this,
          () -> {
            if (editingId == null) service.create(d); else service.update(d);
            return true;
          },
          ok -> { refreshTable(); clearForm(); info("Saved"); },
          err -> {
            if (err instanceof ValidationException vex) { warn(vex.getMessage()); }
            else { error("Failed to save: " + err.getMessage(), err); }
          },
          btnSave
      );
    });

    // Delete
    btnDelete.addActionListener(e -> {
      if (editingId == null) { info("Select a doctor to delete (double click a row to load)."); return; }
      int choice = JOptionPane.showConfirmDialog(this, "Delete this doctor?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
      if (choice != JOptionPane.OK_OPTION) return;

      var id = editingId;
      Async.run(this,
          () -> service.delete(id),
          ok -> { refreshTable(); clearForm(); },
          err -> error("Failed to delete: " + err.getMessage(), err),
          btnDelete
      );
    });

    // Clear
    btnClear.addActionListener(e -> clearForm());
  }

  private void loadSelectedIntoForm() {
    int viewRow = table.getSelectedRow();
    if (viewRow < 0) return;
    int row = table.convertRowIndexToModel(viewRow);

    editingId = (Integer) model.getValueAt(row, 0);
    tfFirst.setText(String.valueOf(model.getValueAt(row, 1)));
    tfLast.setText(String.valueOf(model.getValueAt(row, 2)));
    tfSpec.setText(String.valueOf(model.getValueAt(row, 3)));
    tfEmail.setText(String.valueOf(model.getValueAt(row, 4)));
    tfPhone.setText(String.valueOf(model.getValueAt(row, 5)));
    cbActive.setSelected(Boolean.TRUE.equals(model.getValueAt(row, 6)));
    tfFirst.requestFocus();
  }

  private void refreshTable() {
    model.setRowCount(0);
    var q = tfSearch.getText().trim();
    List<Doctor> doctors;
    if (q.isEmpty()) doctors = service.listAll();
    else doctors = service.listAll().stream()
        .filter(d -> matches(d, q))
        .toList();

    for (var d : doctors) {
      model.addRow(new Object[]{
          d.getId(), d.getFirstName(), d.getLastName(), d.getSpecialty(),
          d.getEmail(), d.getPhone(), d.isActive()
      });
    }
  }

  private boolean matches(Doctor d, String q) {
    var s = q.toLowerCase();
    return (d.getFirstName() != null && d.getFirstName().toLowerCase().contains(s)) ||
           (d.getLastName()  != null && d.getLastName().toLowerCase().contains(s))  ||
           (d.getSpecialty() != null && d.getSpecialty().toLowerCase().contains(s)) ||
           (d.getEmail()     != null && d.getEmail().toLowerCase().contains(s))     ||
           (d.getPhone()     != null && d.getPhone().toLowerCase().contains(s));
  }

  private void clearForm() {
    editingId = null;
    tfFirst.setText(""); tfLast.setText(""); tfSpec.setText("");
    tfEmail.setText(""); tfPhone.setText("");
    cbActive.setSelected(true);
    table.clearSelection();
  }

  private void info(String m) { JOptionPane.showMessageDialog(this, m, "Info", JOptionPane.INFORMATION_MESSAGE); }
  private void warn(String m) { JOptionPane.showMessageDialog(this, m, "Validation", JOptionPane.WARNING_MESSAGE); }
  private void error(String m, Throwable t) { t.printStackTrace(); JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}