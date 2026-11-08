package com.nttdata.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Config {
    private static final Properties PROPS = load();

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = Config.class.getClassLoader()
                .getResourceAsStream("credentials.properties")) {
            if (in != null) {
                props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return props;
    }

    public static String resolve(String value) {
        if (value == null || !value.startsWith("${") || !value.endsWith("}")) {
            return value;
        }
        String key = value.substring(2, value.length() - 1);
        String found = System.getenv(key);
        if (found == null) {
            found = PROPS.getProperty(key);
        }
        if (found == null) {
            throw new IllegalStateException("Falta la credencial " + key
                    + ". Copia credentials.example.properties a credentials.properties y complétalo.");
        }
        return found;
    }
}