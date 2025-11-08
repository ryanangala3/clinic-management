package com.clinic.ui;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public final class Async {
  private Async() {}

  public static <T> void run(
      JComponent ctx,
      Callable<T> task,
      Consumer<T> onSuccess,
      Consumer<Throwable> onError,
      JComponent... toDisable
  ) {
    for (var c : toDisable) if (c != null) c.setEnabled(false);

    new SwingWorker<T, Void>() {
      @Override protected T doInBackground() throws Exception { return task.call(); }
      @Override protected void done() {
        for (var c : toDisable) if (c != null) c.setEnabled(true);
        try { onSuccess.accept(get()); }
        catch (Exception ex) {
          var cause = ex.getCause() != null ? ex.getCause() : ex;
          if (onError != null) onError.accept(cause);
          else JOptionPane.showMessageDialog(ctx, cause.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }.execute();
  }
}