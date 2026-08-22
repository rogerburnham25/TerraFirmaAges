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

public class Bonfire extends SlabBlock{

    public Bonfire(BlockBehaviour.Properties properties) {
        super(properties);
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