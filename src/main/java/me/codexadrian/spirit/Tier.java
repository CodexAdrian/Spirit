package me.codexadrian.spirit;

import com.google.gson.annotations.SerializedName;

public class Tier {
    
    @SerializedName("requiredSouls")
    private int requiredSouls = 64;
    
    @SerializedName("minSpawnDelay")
    private int minSpawnDelay = 300;
    
    @SerializedName("maxSpawnDelay")
    private int maxSpawnDelay = 1000;
    
    @SerializedName("spawnCount")
    private int spawnCount = 3;
    
    @SerializedName("spawnRange")
    private int spawnRange = 5;
    
    @SerializedName("nearbyRange")
    private int nearbyRange = 16;
    
    @SerializedName("redstoneControlled")
    private boolean redstoneControlled = false;
    
    
    public Tier(int requiredSouls, int minSpawnDelay, int maxSpawnDelay, int spawnCount, int spawnRange, int nearbyRange, boolean redstoneControlled) {
        this.requiredSouls = requiredSouls;
        this.minSpawnDelay = minSpawnDelay;
        this.maxSpawnDelay = maxSpawnDelay;
        this.spawnCount = spawnCount;
        this.spawnRange = spawnRange;
        this.nearbyRange = nearbyRange;
        this.redstoneControlled = redstoneControlled;
    }
    
    public int getRequiredSouls() {
        return requiredSouls;
    }
    
    public int getMinSpawnDelay() {
        return minSpawnDelay;
    }
    
    public int getMaxSpawnDelay() {
        return maxSpawnDelay;
    }
    
    public int getSpawnCount() {
        return spawnCount;
    }
    
    public int getSpawnRange() {
        return spawnRange;
    }
    
    public int getNearbyRange() {
        return nearbyRange;
    }
    
    public boolean isRedstoneControlled() {
        return redstoneControlled;
    }
}
