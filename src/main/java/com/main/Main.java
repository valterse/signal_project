package com.main;

import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;

public class Main {
    public static void main(String[] args) throws java.io.IOException {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            DataStorage.main(new String[]{});
        } else {
            HealthDataSimulator.main(args);
        }
    }
}