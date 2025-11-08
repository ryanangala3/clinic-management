package com.clinic.db;

import java.io.InputStream;
import java.util.Properties;

public final class Config {
  private static Properties props;
  private Config(){}

  public static Properties props() {
    if (props != null) return props;
    try (InputStream in = Config.class.getClassLoader().getResourceAsStream("application.properties")) {
      Properties p = new Properties();
      if (in != null) p.load(in);
      // allow env overrides
      System.getenv().forEach((k,v) -> {
        if (k.startsWith("CLINIC_")) p.setProperty(k.substring("CLINIC_".length()).toLowerCase().replace('_','.'), v);
      });
      props = p;
      return props;
    } catch (Exception e) {
      throw new RuntimeException("Cannot load application.properties", e);
    }
  }
}