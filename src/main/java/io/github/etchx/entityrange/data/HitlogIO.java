package io.github.etchx.entityrange.data;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import io.github.etchx.entityrange.client.EntityRangeClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.etchx.entityrange.client.EntityRangeClient.hasRecorded;

public class HitlogIO {
    public static JsonWriter hitlogWriter;
    public static String hitlogFilename;

    public static void addHit() throws IOException {
        Path dir = getHitlogDir();
        Path path = dir.resolve(hitlogFilename);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        } else if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }
        if (hitlogWriter == null || !hasRecorded) {
            hitlogWriter = new JsonWriter(new FileWriter(path.toFile()));
            hitlogWriter.beginArray();
        }
        new Gson().toJson(EntityRangeClient.hitData, HitData.class, hitlogWriter);
        EntityRangeClient.hitData = null;
        hasRecorded = true;
    }

    public static void endWriting() throws IOException {
        hitlogWriter.endArray();
        hitlogWriter.close();
    }

    public static HitData[] deserializeHitlog(Path path) throws IOException {
        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                return new Gson().fromJson(reader, HitData[].class);
            } catch (IOException e) {
                throw new IOException("Could not parse hitlog", e);
            }
        } else {
            throw new IOException("Hitlog does not exist");
        }
    }

    public static Path getHitlogDir() {
        return FabricLoader.getInstance()
                .getGameDir()
                .resolve("hitlogs");
    }
}