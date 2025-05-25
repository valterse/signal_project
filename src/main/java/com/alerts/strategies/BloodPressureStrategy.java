package com.alerts.strategies;

import com.alerts.factories.AlertFactory;
import com.alerts.AlertGenerator;
import com.alerts.AlertStrategy;
import com.alerts.factories.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.stream.Collectors;

public class BloodPressureStrategy implements AlertStrategy {
    private AlertFactory bloodPressureAlertFactory;

    public BloodPressureStrategy() {
        this.bloodPressureAlertFactory = new BloodPressureAlertFactory();
    }

    @Override
    public void check(Patient patient, List<PatientRecord> recentRecords, AlertGenerator generator) {
        List<PatientRecord> systolicRecords = recentRecords.stream()
                .filter(r -> "SystolicPressure".equalsIgnoreCase(r.getRecordType()))
                .collect(Collectors.toList());
        List<PatientRecord> diastolicRecords = recentRecords.stream()
                .filter(r -> "DiastolicPressure".equalsIgnoreCase(r.getRecordType()))
                .collect(Collectors.toList());

        // threshold for systolic
        for (PatientRecord record : systolicRecords) {
            if (record.getMeasurementValue() > 180 || record.getMeasurementValue() < 90) {
                generator.triggerAlert(bloodPressureAlertFactory.createAlert(String.valueOf(patient.getPatientId()), "Critical Systolic Pressure", record.getTimestamp()));
            }
        }
        //threshold for diastolic
        for (PatientRecord record : diastolicRecords) {
            if (record.getMeasurementValue() > 120 || record.getMeasurementValue() < 60) {
                generator.triggerAlert(bloodPressureAlertFactory.createAlert(String.valueOf(patient.getPatientId()), "Critical Diastolic Pressure", record.getTimestamp()));
            }
        }

        // systolic alert
        checkBloodPressureTrend(patient, systolicRecords, "Systolic", generator);

        // diastolic alert
        checkBloodPressureTrend(patient, diastolicRecords, "Diastolic", generator);
    }

    private void checkBloodPressureTrend(Patient patient, List<PatientRecord> bpRecords, String type, AlertGenerator generator) {
        if (bpRecords.size() >= 3) {
            // ensure records are sorted by timestamp
            bpRecords.sort((r1, r2) -> Long.compare(r1.getTimestamp(), r2.getTimestamp()));

            for (int i = 0; i <= bpRecords.size() - 3; i++) {
                PatientRecord r1 = bpRecords.get(i);
                PatientRecord r2 = bpRecords.get(i + 1);
                PatientRecord r3 = bpRecords.get(i + 2);

                double val1 = r1.getMeasurementValue();
                double val2 = r2.getMeasurementValue();
                double val3 = r3.getMeasurementValue();

                // check for increasing trend
                if (val2 - val1 > 10 && val3 - val2 > 10) {
                    generator.triggerAlert(bloodPressureAlertFactory.createAlert(String.valueOf(patient.getPatientId()), type + " Pressure Increasing Trend", r3.getTimestamp()));
                }
                // check for decreasing trend
                else if (val1 - val2 > 10 && val2 - val3 > 10) {
                    generator.triggerAlert(bloodPressureAlertFactory.createAlert(String.valueOf(patient.getPatientId()), type + " Pressure Decreasing Trend", r3.getTimestamp()));
                }
            }
        }
    }
}