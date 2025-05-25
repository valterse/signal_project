package com.alerts.strategies;

import com.alerts.factories.AlertFactory;
import com.alerts.AlertGenerator;
import com.alerts.AlertStrategy;
import com.alerts.factories.ECGAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.stream.Collectors;

public class HeartRateStrategy implements AlertStrategy {
    private AlertFactory ecgAlertFactory; // using ecg alert factory for heart rate related alerts

    public HeartRateStrategy() {
        this.ecgAlertFactory = new ECGAlertFactory();
    }

    @Override
    public void check(Patient patient, List<PatientRecord> recentRecords, AlertGenerator generator) {
        List<PatientRecord> heartRateRecords = recentRecords.stream()
                .filter(r -> "HeartRate".equalsIgnoreCase(r.getRecordType()))
                .collect(Collectors.toList());

        // check for abnormal heart rates
        for (PatientRecord record : heartRateRecords) {
            if (record.getMeasurementValue() < 50 || record.getMeasurementValue() > 100) {
                generator.triggerAlert(ecgAlertFactory.createAlert(String.valueOf(patient.getPatientId()), "Abnormal Heart Rate", record.getTimestamp()));
            }
        }
    }
}