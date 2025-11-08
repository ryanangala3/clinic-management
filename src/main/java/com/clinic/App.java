package com.clinic;
import com.formdev.flatlaf.FlatLightLaf;
import com.clinic.ui.MainFrame;
import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
public class App {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      FlatLightLaf.setup();

      UIManager.put("defaultFont", new Font("SansSerif", Font.PLAIN, 13));
      UIManager.put("Component.arc", 12);
      UIManager.put("Button.arc", 14);
      UIManager.put("TextComponent.arc", 10);
      UIManager.put("Component.focusWidth", 1);
      UIManager.put("ScrollBar.showButtons", false);
      UIManager.put("Component.accentColor", "#2E77FF");
      UIManager.put("Table.showHorizontalLines", true);
      UIManager.put("Table.showVerticalLines", true);

      // Optional: zebra striping
      UIManager.put("Table.alternateRowColor", new Color(248, 248, 248));

      // Global fallback for unexpected exceptions
      Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, e.toString(), "Unexpected error", JOptionPane.ERROR_MESSAGE);
      });

      new MainFrame().setVisible(true);
    });
  }
}
