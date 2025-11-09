# 🏥 Clinic Desktop Application

[![Java](https://img.shields.io/badge/Java-21-blue?logo=java)](https://openjdk.org/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Build-Maven-orange?logo=apache-maven)](https://maven.apache.org/)
[![Flyway](https://img.shields.io/badge/DB-Migrations%20with%20Flyway-red?logo=flyway)](https://flywaydb.org/)
[![License](https://img.shields.io/badge/License-MIT-green)](#license)

---

## ⚡ Quick Start

1. **Clone the repository**

   ```bash
   git clone https://github.com/yourusername/clinic-desktop.git
   cd clinic-desktop
   ```

2. **Set up the database**

   ```bash
   export CLINIC_DB_URL='jdbc:mysql://127.0.0.1:3306/clinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
   export CLINIC_DB_USER='clinic_app'
   export CLINIC_DB_PASS='changeme123!'
   ```

3. **Run migrations**

   ```bash
   mvn -q org.flywaydb:flyway-maven-plugin:10.17.1:migrate
   ```

4. **Launch the application**
   ```bash
   mvn clean compile exec:java -Dexec.mainClass="com.clinic.Main"
   ```

---

## 🩺 Overview

A desktop-based medical records management system built with **Java Swing**, **MySQL**, and **Flyway** for database migrations.  
It allows staff to manage **patients**, **doctors**, and **appointments** efficiently through a clean graphical interface.

---

## 🚀 Features

- Manage doctors, patients, and appointments
- Add, update, delete, and search records
- Database migrations handled via Flyway
- Built with Maven and follows MVC architecture
- Configurable JDBC connection with HikariCP
- Seeded data for demo/testing
- Includes patient notes field for medical history

---

## 🧩 Tech Stack

| Layer      | Technology         |
| ---------- | ------------------ |
| UI         | Java Swing         |
| Database   | MySQL              |
| ORM / DAO  | JDBC with HikariCP |
| Migrations | Flyway             |
| Build      | Maven              |
| Language   | Java 21            |

---

## 📁 Project Structure

```bash
clinic-desktop/
│
├── src/
│   ├── main/
│   │   ├── java/com/clinic/...
│   │   └── resources/
│   │       └── db/
│   │           └── migration/
│   │               ├── V1__init.sql
│   │               ├── V2__seed.sql
│   │               ├── V3__fix_fk.sql
│   │               ├── V4__seed_notes.sql
│   │               └── V5__backfill_notes.sql
│   └── test/...
│
├── pom.xml
└── README.md
```

---

## 🗄️ Database Setup

### 1. Create the database and user

```sql
mysql -u root -p
CREATE DATABASE clinic CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'clinic_app'@'%' IDENTIFIED BY 'changeme123!';
GRANT ALL PRIVILEGES ON clinic.* TO 'clinic_app'@'%';
FLUSH PRIVILEGES;
```

### 2. Configure environment variables

```bash
export CLINIC_DB_URL='jdbc:mysql://127.0.0.1:3306/clinic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC'
export CLINIC_DB_USER='clinic_app'
export CLINIC_DB_PASS='changeme123!'
```

---

## 🔄 Running Migrations

### Validate and Migrate

```bash
mvn -q validate
mvn -q org.flywaydb:flyway-maven-plugin:10.17.1:migrate
```

### Repair After Failed Migrations

```bash
mvn -q org.flywaydb:flyway-maven-plugin:10.17.1:repair
```

### Check Migration History

```bash
mysql -u clinic_app -p'changeme123!' -h 127.0.0.1 clinic -e "SELECT * FROM flyway_schema_history;"
```

---

## 💻 Running the Application

```bash
mvn clean compile exec:java -Dexec.mainClass="com.clinic.Main"
```

---

## 🧾 License

This project is open for educational and internal development use.  
© 2025 Clinic Desktop – built with ❤️ by Ryan Angala.
