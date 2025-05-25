package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads patient data from files in a specified directory and stores it in DataStorage.
 */
public class FileDataReader implements DataReader {
    private final String outputDirectory;

    /**
     * Constructs a FileDataReader with a specified output directory.
     *
     * @param outputDirectory path to the directory containing output files
     */
    public FileDataReader(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Reads data from CSV-formatted files in the specified directory and populates the given DataStorage.
     *
     * @param dataStorage the DataStorage to populate
     * @throws IOException if any file reading operation fails
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        File dir = new File(outputDirectory);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IOException("Invalid output directory: " + outputDirectory);
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.out.println("No .txt files found in directory: " + outputDirectory);
            return;
        }

        for (File file : files) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) { // Skip empty lines
                        continue;
                    }
                    String[] parts = line.split(", ");
                    if (parts.length != 4) {
                        System.err.println("Skipping malformed line: " + line);
                        continue;
                    }

                    try {
                        int patientId = Integer.parseInt(parts[0].substring("Patient ID: ".length()));
                        long timestamp = Long.parseLong(parts[1].substring("Timestamp: ".length()));
                        String recordType = parts[2].substring("Label: ".length());
                        String dataString = parts[3].substring("Data: ".length());

                        double measurementValue;
                        // alerts for saturation
                        if (recordType.equals("Saturation") && dataString.endsWith("%")) {
                            measurementValue = Double.parseDouble(dataString.substring(0, dataString.length() - 1));
                        } else if (recordType.equals("Alert")) {
                            if (dataString.equalsIgnoreCase("triggered")) {
                                measurementValue = 1.0;
                            } else if (dataString.equalsIgnoreCase("resolved")) {
                                measurementValue = 0.0;
                            } else {
                                continue;
                            }
                        }
                        else {
                            measurementValue = Double.parseDouble(dataString);
                        }

                        dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                    }
                }
            }
        }
    }
}