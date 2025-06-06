package io.github.etchx.entityrange.client;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

public class EntityRangeConfig {
    private static final String FILE_NAME = "entityrange.json";

    public static boolean showHitsInChat = false;
    public static boolean hideHitDisplay = false;
    public static boolean hideDistanceDisplay = false;
    public static boolean useLongDistance = true;

    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    private static Path getConfigPath() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .resolve(EntityRangeConfig.FILE_NAME);
    }

    public static EntityRangeConfig load() {
        Path path = getConfigPath();
        EntityRangeConfig config;

        if (Files.exists(path)) {
            try (FileReader reader = new FileReader(path.toFile())) {
                config = GSON.fromJson(reader, EntityRangeConfig.class);
            } catch (IOException e) {
                throw new RuntimeException("Could not parse config", e);
            }
        } else {
            config = new EntityRangeConfig();
        }

        try {
            writeChanges(config);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't update config file", e);
        }

        return config;
    }

    public static void writeChanges(EntityRangeConfig config) throws IOException {
        Path path = getConfigPath();
        Path dir = path.getParent();

        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        } else if (!Files.isDirectory(dir)) {
            throw new IOException("Not a directory: " + dir);
        }

        Files.writeString(path, GSON.toJson(config));
    }

}
