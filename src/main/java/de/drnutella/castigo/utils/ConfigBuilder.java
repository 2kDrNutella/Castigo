package de.drnutella.castigo.utils;

import de.drnutella.castigo.Castigo;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

public class ConfigBuilder {

    private final File file;
    private final Map<String, Object> config;
    private final Yaml yaml = new Yaml();

    public ConfigBuilder(String path, String filename) {
        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        this.file = new File(dir, filename);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.config = loadConfig();
    }

    public ConfigBuilder(String path, String filename, String templatePath, String templateFileName) {
        File dir = new File(path);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        this.file = new File(dir, filename);

        // Falls die Datei nicht existiert, wird die Vorlage aus dem Ressourcenverzeichnis kopiert.
        if (!file.exists()) {
            try (InputStream inputStream = Castigo.getInstance().getClass().getClassLoader().getResourceAsStream(templatePath + templateFileName + ".yml")) {
                if (inputStream != null) {
                    FileUtils.copyInputStreamToFile(inputStream, file);
                } else {
                    throw new RuntimeException("Template file not found in resources: " + templatePath + templateFileName + ".yml");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.config = loadConfig();
    }

    private Map<String, Object> loadConfig() {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            return yaml.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration from file: " + file.getPath(), e);
        }
    }

    public File getFile() {
        return file;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void save() {
        try {
            FileUtils.write(file, yaml.dump(config), "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save configuration to file: " + file.getPath(), e);
        }
    }
}
