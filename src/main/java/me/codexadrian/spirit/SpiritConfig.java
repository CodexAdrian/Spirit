package me.codexadrian.spirit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import net.fabricmc.loader.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpiritConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @SerializedName("requiredSouls")
    private int requiredSouls = 64;

    @SerializedName("tiers")
    private int tiers = 4;

    @SerializedName("minSpawnDelay")
    private int[] minSpawnDelay = new int[] {300, 150, 75, 25};

    @SerializedName("maxSpawnDelay")
    private int[] maxSpawnDelay = new int[] {1000, 600, 400, 300};

    @SerializedName("spawnCount")
    private int[] spawnCount = new int[] {3, 5, 7, 9};

    @SerializedName("spawnRange")
    private int[] spawnRange = new int[] {5, 7, 9, 11};

    @SerializedName("nearbyRange")
    private int[] nearbyRange = new int[] {16, 24, 32, -1};

    public int getRequiredSouls() {
        return requiredSouls;
    }

    public int getMaxSpawnDelay(int index) {
        return maxSpawnDelay[index];
    }

    public int getMinSpawnDelay(int index) {
        return minSpawnDelay[index];
    }

    public int getSpawnCount(int index) {
        return spawnCount[index];
    }

    public int getSpawnRange(int index) {
        return spawnRange[index];
    }

    public int getNearbyRange(int index) {
        return nearbyRange[index];
    };

    public int getMaxTierAmount() {
        return tiers > 0 ? Math.min(this.tiers, 4) : 1;
    };

    public int getMaxSouls() {
        return requiredSouls *  getMaxTierAmount();
    }

    public static SpiritConfig loadConfig(Path configFolder) throws IOException {
        Path configPath = configFolder.resolve(Spirit.MODID + ".json");

        if(!Files.exists(configPath)) {
            SpiritConfig config = new SpiritConfig();
            try(Writer writer = new FileWriter(configPath.toFile())) {
                GSON.toJson(config, writer);
            }
            LOGGER.info("Created config file for mod " + Spirit.MODID);

            return config;
        }

        return GSON.fromJson(new InputStreamReader(Files.newInputStream(configPath)), SpiritConfig.class);
    }
}
