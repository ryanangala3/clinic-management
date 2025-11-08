package com.clinic.ui;

import javax.swing.*;

public final class UI {
  private UI() {}
  public static void info(String msg)  { JOptionPane.showMessageDialog(null, msg, "Info", JOptionPane.INFORMATION_MESSAGE); }
  public static void warn(String msg)  { JOptionPane.showMessageDialog(null, msg, "Validation", JOptionPane.WARNING_MESSAGE); }
  public static void error(String msg, Throwable t) {
    if (t != null) t.printStackTrace();
    JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
  }
}