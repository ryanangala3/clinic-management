package com.clinic.ui;
import javax.swing.*;
import java.awt.*;
public class MainFrame extends JFrame {
  public MainFrame() {
    super("Clinic Management");
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setSize(960, 560);
    setLocationRelativeTo(null);

    var patientsPanel  = new PatientsPanel();
    var doctorsPanel   = new DoctorsPanel();
    var apptsPanel     = new AppointmentsPanel();

    var tabs = new JTabbedPane();
    tabs.add("Patients", new PatientsPanel());
    tabs.add("Doctors", new DoctorsPanel());
    tabs.add("Appointments", new AppointmentsPanel());

    tabs.addChangeListener(e -> {
      if (tabs.getSelectedComponent() == apptsPanel) {
        SwingUtilities.invokeLater(apptsPanel::refreshFromDb);
      }
    });

    add(tabs, BorderLayout.CENTER);
  }
}