package org.byteskript.manager.impl;

import com.google.gson.JsonArray;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.byteskript.manager.Artifact;
import org.byteskript.manager.ServerOutputTemplate;
import org.byteskript.manager.Version;
import org.byteskript.manager.config.ServerConfig;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimpleServer implements ServerOutputTemplate {
    
    protected final HttpServer server;
    protected File packages, binaries, index;
    
    public SimpleServer(ServerConfig config, HttpHandler handler) throws IOException {
        this.packages = new File("packages/");
        this.binaries = new File("binaries/");
        this.index = new File("index.json");
        if (!packages.exists()) packages.mkdirs();
        if (!binaries.exists()) packages.mkdirs();
        this.index();
        this.server = HttpServer.create(new InetSocketAddress(config.port()), config.backlog());
        this.server.createContext(config.path(), handler);
    }
    
    private void index() throws IOException {
        final File[] files = packages.listFiles();
        assert files != null;
        final JsonArray array = new JsonArray();
        for (final File file : files) {
            try (final InputStream stream = new FileInputStream(file)) {
                final Artifact artifact = Artifact.get(stream);
                array.add(artifact.id());
            } catch (Throwable ignore) {}
        }
        if (!index.exists()) index.createNewFile();
        try (final OutputStream stream = new FileOutputStream(index)) {
            stream.write(array.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
    
    @Override
    public void list(OutputStream stream) throws IOException {
        try (final InputStream input = new FileInputStream(index)) {
            input.transferTo(stream);
        }
    }
    
    @Override
    public void get(String id, OutputStream output) throws IOException {
        final File[] files = packages.listFiles();
        assert files != null;
        final List<Artifact> list = new ArrayList<>();
        for (final File file : files) {
            try (final InputStream stream = new FileInputStream(file)) {
                final Artifact artifact = Artifact.get(stream);
                if (!artifact.id().equals(id)) continue;
                list.add(artifact);
            } catch (Throwable ignore) {}
        }
        list.sort(Comparator.comparing(Artifact::version));
        if (list.size() < 1) return;
        final Artifact artifact = list.get(0);
        this.writeBinary(artifact.id(), artifact.version().version(), output);
    }
    
    @Override
    public void get(String id, Version version, OutputStream output) throws IOException {
        this.writeBinary(id, version.version(), output);
    }
    
    protected void writeBinary(String id, int version, OutputStream stream) throws IOException {
        final File file = new File(binaries, id.replace(File.separatorChar, '_') + "_" + version + ".bin");
        if (!file.exists()) return;
        try (final InputStream input = new FileInputStream(file)) {
            input.transferTo(stream);
        }
    }
}
