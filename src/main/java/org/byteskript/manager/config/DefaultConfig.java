package org.byteskript.manager.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

public record DefaultConfig(int port, String path) {
    
    public static DefaultConfig getInternal() {
        try (final InputStream stream = DefaultConfig.class.getClassLoader().getResourceAsStream("config.json")) {
            return get(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static DefaultConfig get(InputStream stream) {
        final JsonObject object = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
        return get(object);
    }
    
    public static DefaultConfig get(JsonObject object) {
        return new DefaultConfig(object.get("port").getAsInt(), object.get("path").getAsString());
    }
    
    public static DefaultConfig get(File file) {
        try (final InputStream stream = new FileInputStream(file)) {
            return get(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
