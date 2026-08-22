package com.terrafirmaagescore.block.custom;

import com.terrafirmaagescore.TerraFirmaAgesCore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import com.terrafirmaagescore.block.entity.ModBlockEntities;
import com.terrafirmaagescore.block.entity.BonfireBlockEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.tags.ItemTags;

public class Bonfire extends SlabBlock implements EntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public Bonfire(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, false).setValue(LIT, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState baseState = super.getStateForPlacement(context);
        if (baseState != null) {
            return baseState.setValue(TYPE, SlabType.BOTTOM);
        }
        return this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return; 
        
        }
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.25D; 
        double z = pos.getZ() + 0.5D;

        if (random.nextInt(10) == 0) {
            level.playLocalSound(x, y, z,
                SoundEvents.CAMPFIRE_CRACKLE, 
                SoundSource.BLOCKS,
                0.5F + random.nextFloat() * 0.5F,
                1.0F,
                false
            );
        }

        if (random.nextInt(5) == 0) {
            double OffsetX = random.nextDouble() * 0.1D - 0.05D;
            double OffsetZ = random.nextDouble() * 0.1D - 0.05D;

            level.addParticle(ParticleTypes.SMALL_FLAME, x + OffsetX, y, z + OffsetZ, 0.0D, 0.0D, 0.0D);
        }

        if (random.nextInt(3) == 0) {
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                x + (random.nextDouble() * 0.2D - 0.1D),
                y + 0.1D,
                z + (random.nextDouble() * 0.2D - 0.1D),
                0.0D, 0.07D, 0.0D
            );
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (heldItem.is(ItemTags.LOGS_THAT_BURN)) {
            if (!level.isClientSide) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof BonfireBlockEntity BonfireBlockEntity) {
                    BonfireBlockEntity.addFuel(1200);

                    if (!player.isCreative()) {
                        heldItem.shrink(1);
                    }

                    if (!state.getValue(LIT)) {
                        level.setBlock(pos, state.setValue(LIT, true), 3);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BonfireBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || !state.getValue(LIT)) {
            return null;
        }
    
    // Instead of using createTickerHelper, manually verify the BlockEntity type matches
        return type == ModBlockEntities.BONFIRE.get() ? (lvl, pos, st, be) -> {
            if (be instanceof BonfireBlockEntity bonfireBe) {
                BonfireBlockEntity.serverTick(lvl, pos, st, bonfireBe);
            }
        } : null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT); // Registers the LIT property
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }


    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(TerraFirmaAgesCore.MODID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(TerraFirmaAgesCore.MODID);

    public static final DeferredHolder<Block, Block> BONFIRE =
        BLOCKS.register(
                "bonfire",
                () -> new Bonfire(
                        BlockBehaviour.Properties.of()
                                .strength(2.0F)
                                .noOcclusion()
                                .lightLevel((state) -> 15)
                )
        );

    // public static void register(IEventBus eventBus) {
    //     BLOCKS.register(eventBus);
    // }
    
    public static final DeferredHolder<Item, BlockItem> BONFIRE_ITEM = ITEMS.register(
            "bonfire",
            () -> new BlockItem(
                    BONFIRE.get(),
                    new Item.Properties()
            )
    );
}