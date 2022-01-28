package org.byteskript.manager;

import java.io.IOException;
import java.io.OutputStream;

public interface ServerOutputTemplate {
    
    void list(OutputStream stream) throws IOException;
    
    void get(String id, OutputStream stream) throws IOException;
    
    void get(String id, Version version, OutputStream stream) throws IOException;
    
}
