package com.alerts;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

public interface AlertStrategy {
    void check(Patient patient, List<PatientRecord> records, AlertGenerator generator);
}