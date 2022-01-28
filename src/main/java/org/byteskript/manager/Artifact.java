package org.byteskript.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.byteskript.manager.data.Json;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public record Artifact(
    String id,
    String name,
    String description,
    Version version,
    Artifact... dependencies
) implements Json {
    
    public static Artifact get(File file) {
        try (final InputStream stream = new FileInputStream(file)) {
            return get(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Artifact get(JsonObject object) {
        final List<Artifact> list = new ArrayList<>();
        for (final JsonElement element : object.getAsJsonArray("dependencies")) {
            list.add(get(element.getAsJsonObject()));
        }
        final Version version = Version.get(object.getAsJsonObject("version"));
        return new Artifact(object.get("id").getAsString(), object.get("name").getAsString(), object.get("description")
            .getAsString(), version, list.toArray(new Artifact[0]));
    }
    
    public static Artifact get(InputStream stream) {
        final JsonObject object = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
        return get(object);
    }
    
    @Override
    public JsonObject getAsObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("id", id);
        object.addProperty("name", name);
        object.addProperty("description", description);
        object.add("version", version.getAsObject());
        final JsonArray array = new JsonArray();
        for (final Artifact dependency : dependencies) {
            array.add(dependency.getAsObject());
        }
        object.add("dependencies", array);
        return object;
    }
    
}
