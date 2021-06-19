package com.codexadrian.spirit.event;

import com.codexadrian.spirit.Spirit;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class EventHandler {

    public static int ticks = 0;
    static BlockPos[] soulSoils = new BlockPos[] {
            new BlockPos(0, -1,1),
            new BlockPos(0,-1,-1),
            new BlockPos(1, -1,0),
            new BlockPos(-1,-1,0)
    };
    static BlockPos[] endStones = new BlockPos[] {
            new BlockPos(1,  -1, 1),
            new BlockPos(-1, -1, 1),
            new BlockPos(1, -1, -1),
            new BlockPos(-1,-1, -1),
            new BlockPos(0, -1,2),
            new BlockPos(0,-1,-2),
            new BlockPos(2, -1,0),
            new BlockPos(-2,-1,0)
    };
    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> ticks++);

        UseBlockCallback.EVENT.register((player, level, interactionHand, face) -> {
            BlockPos blockPos = face.getBlockPos();
            if(player.getMainHandItem().getItem() == Items.DIAMOND.asItem()) {
                BlockState block = level.getBlockState(face.getBlockPos());
                if(block.is(Blocks.SOUL_LANTERN)) {
                    for(BlockPos i : soulSoils) {
                        BlockPos soulSoilOffPos = face.getBlockPos().offset(i);
                        if(!level.getBlockState(soulSoilOffPos).is(Blocks.SOUL_SOIL)) {
                            return InteractionResult.PASS;
                        }
                    }
                    for(BlockPos i : endStones) {
                        BlockPos endStoneOffPos = blockPos.offset(i);
                        if(!level.getBlockState(endStoneOffPos).is(Blocks.END_STONE)) {
                            return InteractionResult.PASS;
                        }
                    }
                    BlockPos offSetOne = blockPos.offset(0, -1, 0);
                    if(!level.getBlockState(offSetOne).is(Blocks.DIAMOND_BLOCK)) {
                        return InteractionResult.PASS;
                    }
                    player.getMainHandItem().shrink(1);
                    for(BlockPos i : soulSoils) { level.destroyBlock(blockPos.offset(i), false); }
                    for(BlockPos i : endStones) { level.destroyBlock(blockPos.offset(i), false); }
                    level.destroyBlock(blockPos, false);
                    level.destroyBlock(offSetOne, false);
                    if(!level.isClientSide()) {
                        ServerLevel sLevel = (ServerLevel) level;
                        ItemEntity newCrystal = new ItemEntity(sLevel, blockPos.getX() + 0.5D, blockPos.getY(), blockPos.getZ() + 0.5D, Spirit.SOUL_CRYSTAL.getDefaultInstance());
                        sLevel.addFreshEntity(newCrystal);
                        sLevel.sendParticles(ParticleTypes.SOUL, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 40, 1, 2, 1, 0);
                    }
                    player.playSound(SoundEvents.WITHER_DEATH, 1.0F, 1.0F);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.PASS;
            }
            return InteractionResult.PASS;
        });
    }
}
