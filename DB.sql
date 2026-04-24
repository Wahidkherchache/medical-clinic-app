-- 1. تنظيف البيئة (حذف القديم لتجنب ORA-00955)
DROP TABLE Appointment CASCADE CONSTRAINTS;
DROP TABLE Patient CASCADE CONSTRAINTS;
DROP TABLE Doctor CASCADE CONSTRAINTS;
DROP SEQUENCE Seq_Patient;
DROP SEQUENCE Seq_Doctor;
DROP SEQUENCE Seq_Appointment;
-- 2. إنشاء الجداول
CREATE TABLE Patient (
    Num_patient NUMBER PRIMARY KEY,
    LastName VARCHAR2(25) NOT NULL,
    FirstName VARCHAR2(25) NOT NULL,
    Date_of_Birth DATE NOT NULL,
    Phone VARCHAR2(15),
    Address VARCHAR2(40)
);
CREATE TABLE Doctor (
    Num_doctor NUMBER PRIMARY KEY,
    LastName VARCHAR2(25) NOT NULL,
    FirstName VARCHAR2(25) NOT NULL,
    Specialty VARCHAR2(30),
    Phone VARCHAR2(15)
);
CREATE TABLE Appointment (
    Num_Appointment NUMBER PRIMARY KEY,
    Num_Patient NUMBER,
    Num_Doctor NUMBER,
    DateApp DATE,
    TimeApp VARCHAR2(10),
    status VARCHAR2(20) DEFAULT 'Planned' CHECK (status IN ('Planned', 'Cancelled', 'Completed')),
    CONSTRAINT FK_Patient FOREIGN KEY (Num_Patient) REFERENCES Patient(Num_patient) ON DELETE CASCADE,
    CONSTRAINT FK_Doctor FOREIGN KEY (Num_Doctor) REFERENCES Doctor(Num_doctor) ON DELETE CASCADE
);
-- 3. إنشاء الـ Sequences
CREATE SEQUENCE Seq_Patient START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE Seq_Doctor START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE Seq_Appointment START WITH 1 INCREMENT BY 1;
-- 4. إنشاء الـ Triggers (الربط التلقائي)
-- ملاحظة: علامة "/" ضرورية جداً في سطر منفصل لتنفيذ الـ Trigger
CREATE OR REPLACE TRIGGER trg_patient_id BEFORE
INSERT ON Patient FOR EACH ROW BEGIN
SELECT Seq_Patient.NEXTVAL INTO :new.Num_patient
FROM dual;
END;
/
CREATE OR REPLACE TRIGGER trg_doctor_id BEFORE
INSERT ON Doctor FOR EACH ROW BEGIN
SELECT Seq_Doctor.NEXTVAL INTO :new.Num_doctor
FROM dual;
END;
/
CREATE OR REPLACE TRIGGER trg_appointment_id BEFORE
INSERT ON Appointment FOR EACH ROW BEGIN
SELECT Seq_Appointment.NEXTVAL INTO :new.Num_Appointment
FROM dual;
END;
/