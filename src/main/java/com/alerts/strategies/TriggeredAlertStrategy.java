package com.alerts.strategies;

import com.alerts.factories.AlertFactory;
import com.alerts.AlertGenerator;
import com.alerts.AlertStrategy;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

public class TriggeredAlertStrategy implements AlertStrategy {
    private AlertFactory generalAlertFactory;

    public TriggeredAlertStrategy() {
        this.generalAlertFactory = new BloodOxygenAlertFactory();
    }

    @Override
    public void check(Patient patient, List<PatientRecord> recentRecords, AlertGenerator generator) {
        for (PatientRecord record : recentRecords) {
            if ("Alert".equalsIgnoreCase(record.getRecordType())) {
                if (record.getMeasurementValue() == 1.0) {
                    generator.triggerAlert(generalAlertFactory.createAlert(String.valueOf(patient.getPatientId()), "Patient Triggered Alert", record.getTimestamp()));
                }
            }
        }
    }
}