package me.codexadrian.spirit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.codexadrian.spirit.blocks.soulcage.SoulCageBlock;
import me.codexadrian.spirit.blocks.soulcage.SoulCageBlockEntity;
import me.codexadrian.spirit.items.DivineCrystalItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Arrays;

public class Spirit implements ModInitializer {
    public static final String MODID = "spirit";
    public static final CreativeModeTab SPIRIT = FabricItemGroupBuilder.create(new ResourceLocation(MODID, "spirit")).icon(() -> new ItemStack(Spirit.SOUL_CAGE)).build();
    public static final SoulCageBlock SOUL_CAGE = new SoulCageBlock(FabricBlockSettings.copyOf(Blocks.SPAWNER).requiresCorrectToolForDrops());
    public static final BlockEntityType<SoulCageBlockEntity> SOUL_CAGE_ENTITY = FabricBlockEntityTypeBuilder.create(SoulCageBlockEntity::new, SOUL_CAGE).build(null);
    public static final Item SOUL_CAGE_ITEM = new BlockItem(SOUL_CAGE, new Item.Properties().tab(Spirit.SPIRIT).rarity(Rarity.EPIC));
    public static final Item SOUL_CRYSTAL = new DivineCrystalItem(new Item.Properties().tab(Spirit.SPIRIT).stacksTo(1).rarity(Rarity.RARE));
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Block BROKEN_SPAWNER = new Block(FabricBlockSettings.copyOf(Blocks.SPAWNER).requiresCorrectToolForDrops());
    public static final Item BROKEN_SPAWNER_ITEM = new BlockItem(BROKEN_SPAWNER, new Item.Properties().tab(Spirit.SPIRIT).rarity(Rarity.EPIC));
    private static SpiritConfig spiritConfig;
    public static final BlockPos[] WARPED_WART_POSITIONS = new BlockPos[] {
            new BlockPos(0, -1,1),
            new BlockPos(0,-1,-1),
            new BlockPos(1, -1,0),
            new BlockPos(-1,-1,0)
    };

    public static SpiritConfig getSpiritConfig() {
        return spiritConfig;
    }

    public static Tier getTier(ItemStack itemStack) {
        if(!itemStack.hasTag() || !itemStack.getTag().contains("StoredEntity")) {
            return null;
        }
        int storedSouls = itemStack.getTag().getCompound("StoredEntity").getInt("Souls");
        String type = itemStack.getTag().getCompound("StoredEntity").getString("Type");
        Tier tier = null;
        for(Tier t : spiritConfig.getTiers()) {
            if(Arrays.stream(t.getBlacklist()).noneMatch(b -> b.equals(type))) {
                if (t.getRequiredSouls() <= storedSouls) {
                    tier = t;
                } else {
                    break;
                }
            }
        }
        return tier;
    }
    
    public static int getTierIndex(ItemStack itemStack) {
        if(!itemStack.hasTag() || !itemStack.getTag().contains("StoredEntity")) {
            return -1;
        }
        int storedSouls = itemStack.getTag().getCompound("StoredEntity").getInt("Souls");
        String type = itemStack.getTag().getCompound("StoredEntity").getString("Type");
        int tier = 0;
        for(int i = 0; i < spiritConfig.getTiers().length; i++) {
            Tier t = spiritConfig.getTiers()[i];
            if(Arrays.stream(t.getBlacklist()).noneMatch(b -> b.equals(type))) {
                if (t.getRequiredSouls() <= storedSouls) {
                    tier = i;
                } else {
                    break;
                }
            }
        }
        return tier;
    }
    
    public static Tier getNextTier(ItemStack itemStack) {
        if(!itemStack.hasTag() || !itemStack.getTag().contains("StoredEntity")) {
            return null;
        }
        int storedSouls = itemStack.getTag().getCompound("StoredEntity").getInt("Souls");
        String type = itemStack.getTag().getCompound("StoredEntity").getString("Type");
        Tier tier = null;
        for(Tier t : spiritConfig.getTiers()) {
            if(Arrays.stream(t.getBlacklist()).noneMatch(b -> b.equals(type))) {
                if (t.getRequiredSouls() > storedSouls) {
                    tier = t;
                    break;
                }
            }
        }
        return tier;
    }
    
    public static int getMaxSouls(ItemStack itemStack) {
        if(!itemStack.hasTag() || !itemStack.getTag().contains("StoredEntity")) {
            return Integer.MAX_VALUE;
        }
        String type = itemStack.getTag().getCompound("StoredEntity").getString("Type");
        int requiredSouls = 0;
        for(int i = 0; i < spiritConfig.getTiers().length; i++) {
            Tier t = spiritConfig.getTiers()[i];
            if(Arrays.stream(t.getBlacklist()).noneMatch(b -> b.equals(type))) {
                if(requiredSouls < t.getRequiredSouls()) {
                    requiredSouls = t.getRequiredSouls();
                }
            }
        }
        return requiredSouls;
    }
    
    public static Tier getMaxTier(ItemStack itemStack) {
        if(!itemStack.hasTag() || !itemStack.getTag().contains("StoredEntity")) {
            return null;
        }
        String type = itemStack.getTag().getCompound("StoredEntity").getString("Type");
        Tier tier = null;
        for(Tier t : spiritConfig.getTiers()) {
            if(Arrays.stream(t.getBlacklist()).noneMatch(b -> b.equals(type))) {
                tier = t;
            }
        }
        return tier;
    }
    
    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new ResourceLocation(MODID, "soul_cage"), SOUL_CAGE);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(MODID, "soul_cage"), SOUL_CAGE_ENTITY);
        Registry.register(Registry.ITEM, new ResourceLocation(MODID, "soul_cage"), SOUL_CAGE_ITEM);
        Registry.register(Registry.BLOCK, new ResourceLocation(MODID, "broken_spawner"), BROKEN_SPAWNER);
        Registry.register(Registry.ITEM, new ResourceLocation(MODID, "broken_spawner"), BROKEN_SPAWNER_ITEM);
        Registry.register(Registry.ITEM, new ResourceLocation(MODID, "soul_crystal"), SOUL_CRYSTAL);

        try {
            spiritConfig = SpiritConfig.loadConfig(FabricLoader.getInstance().getConfigDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkMultiblock(BlockPos blockPos, Level level) {
        for(BlockPos glassPos : WARPED_WART_POSITIONS) {
            if (!level.getBlockState(blockPos.offset(glassPos)).is(Blocks.WARPED_WART_BLOCK)) {
                return false;
            }
        }
        for(BlockPos glassPos : WARPED_WART_POSITIONS) {
            level.destroyBlock(blockPos.offset(glassPos), false);
        }
        return true;
    }

}
