package me.codexadrian.spirit;

import me.codexadrian.spirit.blocks.soultable.SoulCageBlock;
import me.codexadrian.spirit.blocks.soultable.SoulCageBlockEntity;
import me.codexadrian.spirit.items.DivineCrystalItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Spirit implements ModInitializer {
    public static final String MODID = "spirit";
    public static final CreativeModeTab SPIRIT = FabricItemGroupBuilder.create(new ResourceLocation(MODID, "spirit")).icon(() -> new ItemStack(Spirit.SOUL_CAGE)).build();
    public static final SoulCageBlock SOUL_CAGE = new SoulCageBlock(FabricBlockSettings.copyOf(Blocks.SPAWNER).breakByTool(FabricToolTags.PICKAXES).requiresCorrectToolForDrops());
    public static final BlockEntityType<SoulCageBlockEntity> SOUL_CAGE_ENTITY = FabricBlockEntityTypeBuilder.create(SoulCageBlockEntity::new, SOUL_CAGE).build(null);
    public static final Item SOUL_CAGE_ITEM = new BlockItem(SOUL_CAGE, new Item.Properties().tab(Spirit.SPIRIT).rarity(Rarity.EPIC));
    public static final MobEffect CORRUPTED = new MobEffect(MobEffectCategory.NEUTRAL, 0x33f8ff){};
    public static final Item SOUL_CRYSTAL = new DivineCrystalItem(new Item.Properties().tab(Spirit.SPIRIT).stacksTo(1).rarity(Rarity.RARE));
    public static final Item SPAWNER_SHARD = new Item(new Item.Properties().tab(Spirit.SPIRIT).rarity(Rarity.EPIC));
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Block BROKEN_SPAWNER = new Block(FabricBlockSettings.copyOf(Blocks.SPAWNER).breakByTool(FabricToolTags.PICKAXES).requiresCorrectToolForDrops());
    public static final Item BROKEN_SPAWNER_ITEM = new BlockItem(BROKEN_SPAWNER, new Item.Properties().tab(Spirit.SPIRIT).rarity(Rarity.EPIC));
    private static SpiritConfig spiritConfig;
    public static final BlockPos[] GLASS_POSITIONS = new BlockPos[] {
            new BlockPos(0, -1,1),
            new BlockPos(0,-1,-1),
            new BlockPos(1, -1,0),
            new BlockPos(-1,-1,0)
    };

    public static SpiritConfig getSpiritConfig() {
        return spiritConfig;
    }

    public static int getTier(ItemStack itemStack) {
        int storedSouls = itemStack.getTag().getCompound("StoredEntity").getInt("Souls");
        if(storedSouls < getSpiritConfig().getRequiredSouls() * 4)
            return storedSouls/ getSpiritConfig().getRequiredSouls();
        return 4;
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
        for(BlockPos glassPos : GLASS_POSITIONS) {
            if (!level.getBlockState(blockPos.offset(glassPos)).is(Blocks.WARPED_WART_BLOCK)) {
                return false;
            }
        }
        for(BlockPos glassPos : GLASS_POSITIONS) {
            level.destroyBlock(blockPos.offset(glassPos), true);
        }
        return true;
    }
}
