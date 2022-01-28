package org.byteskript.manager.config;

import com.google.gson.JsonObject;
import org.byteskript.manager.data.Json;

public interface ServerConfig extends Json {
    
    default @Override
    JsonObject getAsObject() {
        final JsonObject object = new JsonObject();
        object.addProperty("port", this.port());
        object.addProperty("path", this.path());
        object.addProperty("backlog", this.backlog());
        return object;
    }
    
    int port();
    
    String path();
    
    default int backlog() {
        return 0;
    }
}
