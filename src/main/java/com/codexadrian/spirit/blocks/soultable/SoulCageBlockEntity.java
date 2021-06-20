package com.codexadrian.spirit.blocks.soultable;

import com.codexadrian.spirit.Spirit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SoulCageBlockEntity extends BlockEntity implements Container {

    EntityType<?> type;
    private ItemStack DivineCrystal = ItemStack.EMPTY;
    @Environment(EnvType.CLIENT)
    public Entity entity;

    private final SoulCageSpawner enabledSpawner = new SoulCageSpawner(this);

    public SoulCageBlockEntity(BlockPos pos, BlockState state) {
        super(Spirit.SOUL_CAGE_ENTITY, pos, state);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, SoulCageBlockEntity blockEntity) {
        if(blockEntity.hasLevel() && !blockEntity.isEmpty()) {
            blockEntity.enabledSpawner.tick();
        }
    }

    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 1, this.getUpdateTag());
    }

    public CompoundTag getUpdateTag() {
        return this.save(new CompoundTag());
    }

    public boolean triggerEvent(int i, int j) {
        return this.enabledSpawner.onEventTriggered(i) || super.triggerEvent(i, j);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return DivineCrystal.isEmpty();
    }

    @Override
    public ItemStack getItem(int i) {
        return i == 0 ? DivineCrystal : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int i, int j) {
        return removeItemNoUpdate(i);
    }

    @Override
    public ItemStack removeItemNoUpdate(int i) {
        if (i == 0) {
            ItemStack sword = DivineCrystal;
            DivineCrystal = ItemStack.EMPTY;
            return sword;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        if (i == 0) {
            DivineCrystal = itemStack;
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return worldPosition != null && worldPosition.distSqr(player.getX(), player.getY(), player.getZ(), true) <= 16;
    }

    @Override
    public void clearContent() {
        DivineCrystal = ItemStack.EMPTY;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        if (compoundTag.contains("crystal")) {
            DivineCrystal = ItemStack.of(compoundTag.getCompound("crystal"));
            setType();
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        super.save(compoundTag);
        if (!DivineCrystal.isEmpty()) {
            compoundTag.put("crystal", DivineCrystal.save(new CompoundTag()));
        }
        return compoundTag;
    }

    public void setType() {
        if (DivineCrystal.hasTag()) {
            type = Registry.ENTITY_TYPE.get(new ResourceLocation(DivineCrystal.getTag().getCompound("StoredEntity").getString("Type")));
        } else {
            type = null;
        }
    }

    public SoulCageSpawner getSpawner() {
        return this.enabledSpawner;
    }
}
