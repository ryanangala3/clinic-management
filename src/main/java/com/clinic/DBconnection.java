package com.clinic;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import com.clinic.db.Config;

public final class DBconnection {
  private static HikariDataSource ds;

  static {
    try {
      Properties p = Config.props(); 
      String url  = req(p, "db.url");
      String user = req(p, "db.user");
      String pass = p.getProperty("db.pass", "");
      HikariConfig hc = new HikariConfig();
      hc.setJdbcUrl(p.getProperty("db.url"));
      hc.setUsername(p.getProperty("db.user"));
      hc.setPassword(p.getProperty("db.pass"));
      hc.setMaximumPoolSize(Integer.parseInt(p.getProperty("db.pool.max", "10")));
      hc.setMinimumIdle(Integer.parseInt(p.getProperty("db.pool.min", "2")));
      hc.setDriverClassName("com.mysql.cj.jdbc.Driver");
      hc.setConnectionTestQuery("SELECT 1");

      ds = new HikariDataSource(hc);
       Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        try { if (ds != null) ds.close(); } catch (Exception ignored) {}
       }));
    } catch (Exception e) {
      throw new RuntimeException("Failed to init datasource", e);
    }
  }

  private DBconnection() {}
  public static Connection get() throws SQLException { return ds.getConnection(); }
  public static DataSource dataSource() { return ds; }
  private static String req(Properties p, String key) {
    String v = p.getProperty(key);
    if (v == null || v.isBlank()) throw new RuntimeException("Missing required property: " + key);
    return v;
  }
}