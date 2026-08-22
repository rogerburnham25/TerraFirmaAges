package com.terrafirmaagescore.block.custom;

import com.terrafirmaagescore.TerraFirmaAgesCore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SlabBlock;

public class Farm_Block extends SlabBlock{

    public Farm_Block(BlockBehaviour.Properties properties) {
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

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(TerraFirmaAgesCore.MODID);

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(TerraFirmaAgesCore.MODID);

    public static final DeferredHolder<Block, Block> FARM_BLOCK =
        BLOCKS.register(
                "farm_block",
                () -> new Farm_Block(
                        BlockBehaviour.Properties.of()
                                .strength(2.0F)
                )
        );

    // public static void register(IEventBus eventBus) {
    //     BLOCKS.register(eventBus);
    // }
    
    public static final DeferredHolder<Item, BlockItem> FARM_BLOCK_ITEM = ITEMS.register(
            "farm_block",
            () -> new BlockItem(
                    FARM_BLOCK.get(),
                    new Item.Properties()
            )
    );
}