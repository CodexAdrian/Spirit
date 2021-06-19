package com.codexadrian.spirit.blocks.soultable;

import com.codexadrian.spirit.Spirit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulCageBlock extends BaseEntityBlock {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);
    public static final IntegerProperty TIER = IntegerProperty.create("tier", 1, 4);

    public SoulCageBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TIER, 1));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return Spirit.SOUL_CAGE_ENTITY.create(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, Spirit.SOUL_CAGE_ENTITY, SoulCageBlockEntity::tick);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (interactionHand != InteractionHand.OFF_HAND) {
            ItemStack itemStack = player.getMainHandItem();
            final SoulCageBlockEntity soulSpawner = Spirit.SOUL_CAGE_ENTITY.getBlockEntity(level, blockPos);
            if (soulSpawner != null) {
                if (soulSpawner.isEmpty()) {
                    if (itemStack.getItem() == Spirit.SOUL_CRYSTAL && itemStack.hasTag()) {
                        if (itemStack.getTag().getCompound("StoredEntity").getInt("Souls") >= Spirit.getSpiritConfig().getRequiredSouls()) {
                            level.setBlock(blockPos, blockState.setValue(TIER, Spirit.getTier(itemStack)), 4);
                            soulSpawner.setItem(0, itemStack.copy());
                            soulSpawner.setType();
                            if (level.isClientSide) soulSpawner.entity = null;
                            if (!player.getAbilities().instabuild) {
                                itemStack.shrink(1);
                            }
                            return InteractionResult.SUCCESS;
                        }
                    }
                } else {
                    final ItemStack DivineCrystal = soulSpawner.removeItemNoUpdate(0);
                    soulSpawner.type = null;
                    if (level.isClientSide) soulSpawner.entity = null;
                    if (itemStack.isEmpty()) {
                        player.setItemInHand(interactionHand, DivineCrystal);
                    } else if (!player.addItem(DivineCrystal)) {
                        player.drop(DivineCrystal, false);
                    }
                    level.setBlock(blockPos, blockState.setValue(TIER, 1), 4);
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(TIER);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, LootContext.Builder builder) {
        List<ItemStack> drops = super.getDrops(blockState, builder);
        BlockEntity blockE = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if(blockE instanceof SoulCageBlockEntity) {
            drops.add(((SoulCageBlockEntity) blockE).getItem(0));
        }
        return drops;
    }
}
