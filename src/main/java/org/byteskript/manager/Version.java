package org.byteskript.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.byteskript.manager.data.Json;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public record Version(int version, String name)
    implements Comparable<Version>, Json {
    
    public Version(String version) {
        this(convert(version), version);
    }
    
    private static int convert(String string) {
        final String[] parts = string.split(".");
        final ByteBuffer buffer = ByteBuffer.allocate(4);
        for (int i = 0; i < parts.length; i++) {
            if (i > 3) break;
            final int x = Integer.parseInt(parts[i]);
            buffer.put(((byte) x));
        }
        return buffer.getInt();
    }
    
    public static Version get(InputStream stream) {
        final JsonObject object = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
        return get(object);
    }
    
    public static Version get(JsonObject object) {
        return new Version(object.get("version").getAsInt(), object.get("name").getAsString());
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public int compareTo(@NotNull Version o) {
        return Integer.compare(version, o.version);
    }
    
    @Override
    public JsonObject getAsObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("version", version);
        object.addProperty("name", name);
        return object;
    }
    
}
