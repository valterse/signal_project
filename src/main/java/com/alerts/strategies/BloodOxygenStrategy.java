package com.alerts.strategies;

import com.alerts.factories.AlertFactory;
import com.alerts.AlertGenerator;
import com.alerts.AlertStrategy;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.stream.Collectors;

public class BloodOxygenStrategy implements AlertStrategy {
    private AlertFactory bloodOxygenAlertFactory;

    public BloodOxygenStrategy() {
        this.bloodOxygenAlertFactory = new BloodOxygenAlertFactory();
    }

    @Override
    public void check(Patient patient, List<PatientRecord> recentRecords, AlertGenerator generator) {
        List<PatientRecord> saturationRecords = recentRecords.stream()
                .filter(r -> "Saturation".equalsIgnoreCase(r.getRecordType()))
                .collect(Collectors.toList());

        // low Saturation alert
        for (PatientRecord record : saturationRecords) {
            if (record.getMeasurementValue() < 92) {
                generator.triggerAlert(bloodOxygenAlertFactory.createAlert(String.valueOf(patient.getPatientId()), "Low Blood Saturation", record.getTimestamp()));
            }
        }

        // rapid Drop alert (within 10 minutes)
        List<PatientRecord> saturationRecords10Min = patient.getRecords(System.currentTimeMillis() - (10 * 60 * 1000), System.currentTimeMillis()).stream()
                .filter(r -> "Saturation".equalsIgnoreCase(r.getRecordType()))
                .collect(Collectors.toList());

        if (saturationRecords10Min.size() >= 2) {
            saturationRecords10Min.sort((r1, r2) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()));

            for (int i = 0; i < saturationRecords10Min.size() - 1; i++) {
                PatientRecord r1 = saturationRecords10Min.get(i);
                PatientRecord r2 = saturationRecords10Min.get(i + 1);

                if (r2.getTimestamp() - r1.getTimestamp() <= 10 * 60 * 1000) {
                    if (r1.getMeasurementValue() - r2.getMeasurementValue() >= 5) { // 5% drop or more
                        generator.triggerAlert(bloodOxygenAlertFactory.createAlert(String.valueOf(patient.getPatientId()), "Rapid Blood Saturation Drop", r2.getTimestamp()));
                    }
                }
            }
        }
    }
}
