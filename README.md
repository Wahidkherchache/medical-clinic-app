# 🏥 Medical Clinic — Appointment Management System

> A Java Swing desktop application connected to an Oracle database for managing patients, doctors, and appointments in a medical clinic.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Oracle](https://img.shields.io/badge/Oracle-F80000?style=for-the-badge&logo=oracle&logoColor=white)
![JDBC](https://img.shields.io/badge/JDBC-007396?style=for-the-badge&logo=java&logoColor=white)

---

## 📋 Description

This application is designed for the **medical secretary** of a small clinic. It allows managing patients, doctors, and consultation appointments through a modern Java Swing interface connected to an Oracle database via JDBC.

---

## ✨ Features

### 👤 Patient Management
- Add a new patient (Last Name, First Name, Date of Birth, Phone, Address)
- Modify patient information
- Delete a patient (with confirmation)
- Search by name or phone number

### 👨‍⚕️ Doctor Management
- Add, modify and delete doctors
- Manage specialties (Cardiologist, Dermatologist, etc.)
- View the full list of clinic doctors

### 📅 Appointment Management
- Book an appointment (select patient, doctor, date and time slot)
- Check doctor availability before booking
- Cancel an appointment
- Filter appointments by doctor or by date
- Color-coded status: Planned / Completed / Cancelled

---

## 🗄️ Database Schema

```sql
CREATE TABLE Patient (
    Num_patient   NUMBER PRIMARY KEY,
    LastName      VARCHAR2(25) NOT NULL,
    FirstName     VARCHAR2(25) NOT NULL,
    Date_of_Birth DATE NOT NULL,
    Phone         VARCHAR2(15),
    Address       VARCHAR2(40)
);

CREATE TABLE Doctor (
    Num_doctor NUMBER PRIMARY KEY,
    LastName   VARCHAR2(25) NOT NULL,
    FirstName  VARCHAR2(25) NOT NULL,
    Specialty  VARCHAR2(30),
    Phone      VARCHAR2(15)
);

CREATE TABLE Appointment (
    Num_Appointment NUMBER PRIMARY KEY,
    Num_Patient     NUMBER,
    Num_Doctor      NUMBER,
    DateApp         DATE,
    TimeApp         VARCHAR2(10),
    status          VARCHAR2(20) DEFAULT 'Planned'
        CHECK (status IN ('Planned', 'Cancelled', 'Completed')),
    CONSTRAINT FK_Patient FOREIGN KEY (Num_Patient) REFERENCES Patient(Num_patient) ON DELETE CASCADE,
    CONSTRAINT FK_Doctor  FOREIGN KEY (Num_Doctor)  REFERENCES Doctor(Num_doctor)   ON DELETE CASCADE
);
```

---

## 🏗️ Project Structure

```
JavaDb/
├── src/
│   ├── MainWindow.java          # Main window & Oracle connection
│   ├── PatientWindow.java       # Patient management
│   ├── DoctorWindow.java        # Doctor management
│   └── AppointmentWindow.java   # Appointment management
├── lib/
│   ├── ojdbc.jar                # Oracle JDBC driver
│   └── rs2xml.jar               # ResultSet to JTable utility
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- Java JDK 8 or higher
- Oracle Database XE
- Eclipse / NetBeans / IntelliJ IDEA

### Required Libraries
| Library | Purpose |
|---------|---------|
| `ojdbc.jar` | Oracle JDBC Driver |
| `rs2xml.jar` | Convert ResultSet to JTable model |

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/medical-clinic-app.git
   ```

2. **Import into your IDE**
   - Open Eclipse → `File > Import > Existing Projects into Workspace`
   - Add `ojdbc.jar` and `rs2xml.jar` to the build path

3. **Configure the database connection**
   
   In `MainWindow.java`, update the credentials:
   ```java
   connection = DriverManager.getConnection(
       "jdbc:oracle:thin:@localhost:1521:xe",
       "YOUR_USERNAME",
       "YOUR_PASSWORD"
   );
   ```

4. **Run the application**
   
   Run `MainWindow.java` as a Java Application.

---

## 🖥️ Screenshots

> _Coming soon_

---

## 👥 Authors

| Name | Role |
|------|------|
| Abdelouahid Kherchache | Development |
| [Teammate Name] | Development |

---

## 🎓 Academic Info

| | |
|---|---|
| **University** | USTHB — Houari Boumediene University of Science and Technology |
| **Faculty** | Faculty of Computer Science |
| **Department** | AI & Data Science |
| **Module** | DataBase 1 |
| **Group** | L2.ACAD.B |
| **Year** | 2025 / 2026 |
| **Instructor** | R. BOUDOUR |

---

## 📄 License

This project is developed for academic purposes as part of the **DataBase 1** module at USTHB.
