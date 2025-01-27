package task4.factory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

class ConfigLoader {
    private final Properties properties = new Properties();

    public ConfigLoader(String config_file_path) {
        try (FileInputStream input = new FileInputStream(config_file_path)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file " + config_file_path, e);
        }
    }

    public int getStorageBodySize() {
        return Integer.parseInt(properties.getProperty("StorageBodySize", "100"));
    }

    public int getStorageMotorSize() {
        return Integer.parseInt(properties.getProperty("StorageMotorSize", "100"));
    }

    public int getStorageAccessorySize() {
        return Integer.parseInt(properties.getProperty("StorageAccessorySize", "100"));
    }

    public int getStorageCarSize() {
        return Integer.parseInt(properties.getProperty("StorageCarSize", "100"));
    }

    public int getBodySuppliers() {
        return Integer.parseInt(properties.getProperty("BodySuppliers", "5"));
    }

    public int getMotorSuppliers() {
        return Integer.parseInt(properties.getProperty("MotorSuppliers", "5"));
    }

    public int getAccessorySuppliers() {
        return Integer.parseInt(properties.getProperty("AccessorySuppliers", "5"));
    }

    public int getWorkers() {
        return Integer.parseInt(properties.getProperty("Workers", "10"));
    }

    public int getDealers() {
        return Integer.parseInt(properties.getProperty("Dealers", "20"));
    }

    public boolean isLogEnabled() {
        return Boolean.parseBoolean(properties.getProperty("Log", "true"));
    }
}
