package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;

public class BloodOxygenAlertFactory implements AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new BloodOxygenAlert(patientId, condition, timestamp);
    }
}