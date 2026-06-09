package com.ae2creative.config;

import com.ae2creative.AE2CreativeMod;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CellConfig {
    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<String>>() {}.getType();
    private static final Logger LOGGER = AE2CreativeMod.LOGGER;

    private static List<ResourceLocation> configuredItems = List.of();

    private CellConfig() {
    }

    public static void load() {
        Path configDir = FMLPaths.CONFIGDIR.get().resolve(AE2CreativeMod.MOD_ID);
        Path configFile = configDir.resolve("cells.json");

        try {
            Files.createDirectories(configDir);
            if (!Files.exists(configFile)) {
                writeDefaultConfig(configFile);
            }
            configuredItems = parseConfig(configFile);
        } catch (IOException exception) {
            LOGGER.error("Failed to load cells.json, falling back to embedded defaults", exception);
            configuredItems = loadEmbeddedDefaults();
        }

        if (configuredItems.isEmpty()) {
            LOGGER.warn("cells.json is empty, using embedded defaults");
            configuredItems = loadEmbeddedDefaults();
        }

        if (configuredItems.isEmpty()) {
            LOGGER.error("No creative cells configured — mod items will not be registered");
        } else {
            LOGGER.info("Loaded {} creative cell entries", configuredItems.size());
        }
    }

    public static List<ResourceLocation> getItems() {
        return configuredItems;
    }

    private static void writeDefaultConfig(Path configFile) throws IOException {
        try (InputStream input = CellConfig.class.getResourceAsStream("/defaultcells.json")) {
            if (input == null) {
                throw new IOException("Embedded defaultcells.json not found");
            }
            Files.copy(input, configFile);
        }
    }

    private static List<ResourceLocation> parseConfig(Path configFile) throws IOException {
        try (Reader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
            List<String> rawEntries = GSON.fromJson(reader, LIST_TYPE);
            if (rawEntries == null) {
                return Collections.emptyList();
            }
            return toResourceLocations(rawEntries);
        } catch (JsonSyntaxException exception) {
            throw new IOException("Invalid JSON in cells.json", exception);
        }
    }

    private static List<ResourceLocation> loadEmbeddedDefaults() {
        try (InputStream input = CellConfig.class.getResourceAsStream("/defaultcells.json")) {
            if (input == null) {
                return List.of();
            }
            try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                List<String> rawEntries = GSON.fromJson(reader, LIST_TYPE);
                return rawEntries == null ? List.of() : toResourceLocations(rawEntries);
            }
        } catch (IOException exception) {
            LOGGER.error("Failed to read embedded default cell list", exception);
            return List.of();
        }
    }

    private static List<ResourceLocation> toResourceLocations(List<String> rawEntries) {
        List<ResourceLocation> result = new ArrayList<>();
        for (String entry : rawEntries) {
            if (entry == null || entry.isBlank()) {
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(entry.trim());
            if (id == null) {
                LOGGER.warn("Skipping invalid item id in cells.json: {}", entry);
                continue;
            }
            result.add(id);
        }
        return List.copyOf(result);
    }
}
