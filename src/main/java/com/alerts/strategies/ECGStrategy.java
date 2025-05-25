package com.alerts.strategies;

import com.alerts.factories.AlertFactory;
import com.alerts.AlertGenerator;
import com.alerts.AlertStrategy;
import com.alerts.factories.ECGAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.stream.Collectors;

public class ECGStrategy implements AlertStrategy {
    private AlertFactory ecgAlertFactory;

    public ECGStrategy() {
        this.ecgAlertFactory = new ECGAlertFactory();
    }

    @Override
    public void check(Patient patient, List<PatientRecord> recentRecords, AlertGenerator generator) {
        List<PatientRecord> ecgRecords = recentRecords.stream()
                .filter(r -> "ECG".equalsIgnoreCase(r.getRecordType()))
                .collect(Collectors.toList());

        if (ecgRecords.size() < 5) { // Need a minimum number of records for a sliding window
            return;
        }

        // Simple sliding window average for ECG
        int windowSize = 5; // Example window size
        for (int i = 0; i <= ecgRecords.size() - windowSize; i++) {
            double sum = 0;
            for (int j = 0; j < windowSize; j++) {
                sum += ecgRecords.get(i + j).getMeasurementValue();
            }
            double average = sum / windowSize;

            // Check if any value in the current window is far beyond the average
            for (int j = 0; j < windowSize; j++) {
                PatientRecord currentRecord = ecgRecords.get(i + j);
                double deviation = Math.abs(currentRecord.getMeasurementValue() - average);
                if (deviation > 0.3) {
                    generator.triggerAlert(ecgAlertFactory.createAlert(String.valueOf(patient.getPatientId()), "Abnormal ECG Peak", currentRecord.getTimestamp()));
                    break;
                }
            }
        }
    }
}