package com.alerts;

import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.alerts.factories.ECGAlertFactory;
import com.alerts.strategies.*;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private AlertFactory bloodPressureAlertFactory;
    private AlertFactory bloodOxygenAlertFactory;
    private AlertFactory ecgAlertFactory;

    // Strategy Pattern: New fields for alert strategies
    private AlertStrategy bloodPressureStrategy;
    private AlertStrategy bloodOxygenStrategy;
    private AlertStrategy heartRateStrategy;
    private AlertStrategy ecgStrategy;
    private AlertStrategy combinedAlertStrategy;
    private AlertStrategy triggeredAlertStrategy;


    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.bloodPressureAlertFactory = new BloodPressureAlertFactory();
        this.bloodOxygenAlertFactory = new BloodOxygenAlertFactory();
        this.ecgAlertFactory = new ECGAlertFactory();

        // Initialize strategies
        this.bloodPressureStrategy = new BloodPressureStrategy();
        this.bloodOxygenStrategy = new BloodOxygenStrategy();
        this.heartRateStrategy = new HeartRateStrategy();
        this.ecgStrategy = new ECGStrategy();
        this.combinedAlertStrategy = new CombinedAlertStrategy();
        this.triggeredAlertStrategy = new TriggeredAlertStrategy();
    }

    /**
     * Evaluates a patient's data to check for various alert conditions and triggers
     * alerts if conditions are met.
     *
     * @param patient the patient whose data is to be evaluated
     */
    public void evaluateData(Patient patient) {
        long currentTime = System.currentTimeMillis();
        long thirtyMinutesAgo = currentTime - (30 * 60 * 1000); // 30 minutes in milliseconds

        List<PatientRecord> recentRecords = patient.getRecords(thirtyMinutesAgo, currentTime);

        // check for Missing Recent Data (if no records in the last 30 minutes)
        if (recentRecords.isEmpty()) {
            triggerAlert(bloodOxygenAlertFactory.createAlert(String.valueOf(patient.getPatientId()), "Missing Recent Data", currentTime));
            return; // No further evaluation if no recent data
        }

        // Strategy Pattern: Delegate alert checking to specific strategies
        bloodPressureStrategy.check(patient, recentRecords, this);
        bloodOxygenStrategy.check(patient, recentRecords, this);
        heartRateStrategy.check(patient, recentRecords, this); // Assuming heart rate is a distinct alert type
        ecgStrategy.check(patient, recentRecords, this);
        combinedAlertStrategy.check(patient, recentRecords, this);
        triggeredAlertStrategy.check(patient, recentRecords, this);
    }

    /**
     * Triggers an alert by printing it to the console.
     * In a real system, this would involve more sophisticated logging, notification, or storage.
     *
     * @param alert the alert to trigger
     */
    public void triggerAlert(Alert alert) {
        alert.trigger();
    }
}