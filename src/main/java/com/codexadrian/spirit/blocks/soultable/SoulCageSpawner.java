package com.codexadrian.spirit.blocks.soultable;

import com.codexadrian.spirit.Corrupted;
import com.codexadrian.spirit.Spirit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class SoulCageSpawner {
    private double spin;
    private int spawnDelay = 20;
    private final SoulCageBlockEntity soulCageBlockEntity;

    public SoulCageSpawner(SoulCageBlockEntity entity) {
        this.soulCageBlockEntity = entity;
    }

    public void tick() {
        Level level = this.getLevel();
        BlockPos blockPos = this.getPos();
        if (level.isClientSide){
            if (this.isNearPlayer()) {
                double d = (double)blockPos.getX() + level.random.nextDouble();
                double e = (double)blockPos.getY() + level.random.nextDouble();
                double f = (double)blockPos.getZ() + level.random.nextDouble();
                level.addParticle(ParticleTypes.SOUL, d, e, f, 0.0D, 0.0D, 0.0D);
                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d, e, f, 0.0D, 0.0D, 0.0D);

                this.spin = (this.spin + 20D) % 360D;
            }
        } else if (this.isNearPlayer()) {
            int tier = getLevel().getBlockState(getPos()).getValue(SoulCageBlock.TIER) - 1;

            if (this.spawnDelay == -1) {
                this.delay(tier);
            }

            if (this.spawnDelay > 0) {
                --this.spawnDelay;
                return;
            }

            boolean bl = false;
            int i = 0;

            while (true) {
                if (i >= Spirit.getSpiritConfig().getSpawnCount(tier)) {
                    if (bl) {
                        this.delay(tier);
                    }
                    break;
                }

                if (soulCageBlockEntity.type == null) {
                    this.delay(tier);
                    return;
                }

                double x = blockPos.getX() + (level.random.nextDouble() - level.random.nextDouble()) * Spirit.getSpiritConfig().getSpawnRange(tier) + 0.5D;
                double y = blockPos.getY() + level.random.nextInt(3) - 1;
                double z = blockPos.getZ() + (level.random.nextDouble() - level.random.nextDouble()) * Spirit.getSpiritConfig().getSpawnRange(tier) + 0.5D;
                if (level.noCollision(soulCageBlockEntity.type.getAABB(x, y, z))) {
                    ServerLevel serverLevel = (ServerLevel) level;
                    if (SpawnPlacements.checkSpawnRules(soulCageBlockEntity.type, serverLevel, MobSpawnType.SPAWNER, new BlockPos(x, y, z), level.getRandom())) {
                        Entity spawned = soulCageBlockEntity.type.create(level);
                        if (spawned == null) {
                            this.delay(tier);
                            return;
                        }
                        ((Corrupted) spawned).setCorrupted();
                        spawned.moveTo(x, y, z, spawned.getYRot(), spawned.getXRot());

                        int l = level.getEntitiesOfClass(spawned.getClass(), new AABB(blockPos).inflate(Spirit.getSpiritConfig().getSpawnRange(tier))).size();
                        if (l >= 6) {
                            this.delay(tier);
                            return;
                        }

                        spawned.moveTo(spawned.getX(), spawned.getY(), spawned.getZ(), level.random.nextFloat() * 360.0F, 0.0F);
                        if (spawned instanceof Mob) {
                            Mob mob = (Mob) spawned;
                            if (!mob.checkSpawnRules(level, MobSpawnType.SPAWNER) || !mob.checkSpawnObstruction(level)) {
                                this.delay(tier);
                                return;
                            }

                            ((Mob) spawned).finalizeSpawn(serverLevel, level.getCurrentDifficultyAt(spawned.blockPosition()), MobSpawnType.SPAWNER, null, null);
                        }

                        if (!serverLevel.tryAddFreshEntityWithPassengers(spawned)) {
                            this.delay(tier);
                            return;
                        }

                        serverLevel.sendParticles(ParticleTypes.SOUL, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 20, 1, 1, 1, 0);

                        if (spawned instanceof Mob) {
                            ((Mob) spawned).spawnAnim();
                        }

                        bl = true;
                    }
                }

                ++i;
            }
        }
    }


    private boolean isNearPlayer() {
        BlockPos blockPos = this.getPos();
        int tier = getLevel().getBlockState(getPos()).getValue(SoulCageBlock.TIER) - 1;
        if(Spirit.getSpiritConfig().getNearbyRange(tier) > 0) {
            return this.getLevel().hasNearbyAlivePlayer((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, Spirit.getSpiritConfig().getNearbyRange(tier));
        }
        return true;
    }
    private void delay(int tier) {
        if (Spirit.getSpiritConfig().getMaxSpawnDelay(tier ) <= Spirit.getSpiritConfig().getMinSpawnDelay(tier )) {
            this.spawnDelay = Spirit.getSpiritConfig().getMinSpawnDelay(tier );
        } else {
            this.spawnDelay = Spirit.getSpiritConfig().getMinSpawnDelay(tier ) + this.getLevel().random.nextInt(Spirit.getSpiritConfig().getMaxSpawnDelay(tier) - Spirit.getSpiritConfig().getMinSpawnDelay(tier));
        }

        this.broadcastEvent(1);
    }

    public boolean onEventTriggered(int i) {
        if (i == 1 && this.getLevel().isClientSide) {
            this.spawnDelay = Spirit.getSpiritConfig().getMinSpawnDelay(getLevel().getBlockState(getPos()).getValue(SoulCageBlock.TIER) );
            return true;
        } else {
            return false;
        }
    }

    public void broadcastEvent(int i) {
        final Level level = soulCageBlockEntity.getLevel();
        if (level != null) {
            level.blockEvent(soulCageBlockEntity.getBlockPos(), Spirit.SOUL_CAGE, i, 0);
        }
    }

    public Level getLevel() {
        return soulCageBlockEntity.getLevel();
    }

    public BlockPos getPos() {
        return soulCageBlockEntity.getBlockPos();
    }

    public double getSpin() {
        return spin;
    }

}
