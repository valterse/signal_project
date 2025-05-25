package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;

class DataStorageTest {

    private DataStorage storage;

    @BeforeEach
    void setUp() {
        // Initialize DataStorage before each test to ensure a clean state
        storage = new DataStorage();
    }

    @Test
    void testAddAndGetRecordsBasic() {
        // Test adding a single record and retrieving it
        storage.addPatientData(1, 98.6, "Temperature", 1678886400000L); // March 15, 2023 12:00:00 PM UTC
        List<PatientRecord> records = storage.getRecords(1, 1678886400000L, 1678886400000L);
        assertEquals(1, records.size());
        assertEquals(98.6, records.get(0).getMeasurementValue());
        assertEquals("Temperature", records.get(0).getRecordType());
        assertEquals(1678886400000L, records.get(0).getTimestamp());
        assertEquals(1, records.get(0).getPatientId());
    }

    @Test
    void testAddMultipleRecordsForSamePatient() {
        // Test adding multiple records for the same patient
        storage.addPatientData(1, 100.0, "HeartRate", 1700000000000L);
        storage.addPatientData(1, 105.0, "HeartRate", 1700000001000L);
        storage.addPatientData(1, 120.0, "BloodPressure", 1700000002000L);

        List<PatientRecord> records = storage.getRecords(1, 1700000000000L, 1700000002000L);
        assertEquals(3, records.size());
        // Verify order and values
        assertEquals(100.0, records.get(0).getMeasurementValue());
        assertEquals("HeartRate", records.get(0).getRecordType());
        assertEquals(105.0, records.get(1).getMeasurementValue());
        assertEquals("HeartRate", records.get(1).getRecordType());
        assertEquals(120.0, records.get(2).getMeasurementValue());
        assertEquals("BloodPressure", records.get(2).getRecordType());
    }

    @Test
    void testGetRecordsWithinTimeRange() {
        // Test retrieving records within a specific time range
        storage.addPatientData(1, 60.0, "HeartRate", 1000L);
        storage.addPatientData(1, 65.0, "HeartRate", 2000L);
        storage.addPatientData(1, 70.0, "HeartRate", 3000L);
        storage.addPatientData(1, 75.0, "HeartRate", 4000L);

        List<PatientRecord> records = storage.getRecords(1, 1500L, 3500L);
        assertEquals(2, records.size());
        assertEquals(65.0, records.get(0).getMeasurementValue());
        assertEquals(70.0, records.get(1).getMeasurementValue());
    }

    @Test
    void testGetRecordsNoRecordsFound() {
        // Test when no records exist for a patient
        List<PatientRecord> records = storage.getRecords(999, 0L, Long.MAX_VALUE);
        assertTrue(records.isEmpty());

        // Test when records exist but not in the specified range
        storage.addPatientData(1, 1.0, "ECG", 1000L);
        records = storage.getRecords(1, 2000L, 3000L);
        assertTrue(records.isEmpty());
    }

    @Test
    void testGetRecordsWithDifferentPatients() {
        // Test adding and retrieving records for different patients
        storage.addPatientData(1, 100.0, "BP", 1000L);
        storage.addPatientData(2, 90.0, "BP", 1100L);
        storage.addPatientData(1, 102.0, "BP", 1200L);

        List<PatientRecord> patient1Records = storage.getRecords(1, 0L, Long.MAX_VALUE);
        assertEquals(2, patient1Records.size());
        assertEquals(100.0, patient1Records.get(0).getMeasurementValue());
        assertEquals(102.0, patient1Records.get(1).getMeasurementValue());

        List<PatientRecord> patient2Records = storage.getRecords(2, 0L, Long.MAX_VALUE);
        assertEquals(1, patient2Records.size());
        assertEquals(90.0, patient2Records.get(0).getMeasurementValue());
    }

    @Test
    void testGetRecordsEdgeCasesTimeRange() {
        // Test edge cases for time ranges (inclusive)
        storage.addPatientData(1, 50.0, "Saturation", 1000L);
        storage.addPatientData(1, 55.0, "Saturation", 2000L);
        storage.addPatientData(1, 60.0, "Saturation", 3000L);

        List<PatientRecord> recordsExactStartEnd = storage.getRecords(1, 1000L, 3000L);
        assertEquals(3, recordsExactStartEnd.size());

        List<PatientRecord> recordsBeforeStart = storage.getRecords(1, 0L, 999L);
        assertTrue(recordsBeforeStart.isEmpty());

        List<PatientRecord> recordsAfterEnd = storage.getRecords(1, 3001L, 4000L);
        assertTrue(recordsAfterEnd.isEmpty());
    }

    @Test
    void testGetAllPatients() {
        // Test retrieving all patients
        storage.addPatientData(1, 100.0, "BP", 1000L);
        storage.addPatientData(2, 90.0, "BP", 1100L);
        storage.addPatientData(3, 80.0, "BP", 1200L);

        List<com.data_management.Patient> allPatients = storage.getAllPatients();
        assertEquals(3, allPatients.size());
        // Check if patient IDs are correct (order might vary based on HashMap implementation)
        assertTrue(allPatients.stream().anyMatch(p -> p.getPatientId() == 1));
        assertTrue(allPatients.stream().anyMatch(p -> p.getPatientId() == 2));
        assertTrue(allPatients.stream().anyMatch(p -> p.getPatientId() == 3));
    }
}