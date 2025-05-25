package com.alerts.strategies;

import com.alerts.*;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

public class CombinedAlertStrategy implements AlertStrategy {
    private AlertFactory bloodPressureAlertFactory;
    private AlertFactory bloodOxygenAlertFactory;

    public CombinedAlertStrategy() {
        this.bloodPressureAlertFactory = new BloodPressureAlertFactory();
        this.bloodOxygenAlertFactory = new BloodOxygenAlertFactory();
    }

    @Override
    public void check(Patient patient, List<PatientRecord> recentRecords, AlertGenerator generator) {
        boolean lowSystolic = false;
        boolean lowSaturation = false;
        long timestampCombined = 0;

        for (PatientRecord record : recentRecords) {
            if ("SystolicPressure".equalsIgnoreCase(record.getRecordType()) && record.getMeasurementValue() < 90) {
                lowSystolic = true;
                timestampCombined = Math.max(timestampCombined, record.getTimestamp());
            } else if ("Saturation".equalsIgnoreCase(record.getRecordType()) && record.getMeasurementValue() < 92) {
                lowSaturation = true;
                timestampCombined = Math.max(timestampCombined, record.getTimestamp());
            }
        }

        if (lowSystolic && lowSaturation) {
            generator.triggerAlert(bloodOxygenAlertFactory.createAlert(String.valueOf(patient.getPatientId()), "Hypotensive Hypoxemia Alert", timestampCombined));
        }
    }
}