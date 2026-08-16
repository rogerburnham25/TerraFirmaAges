package com.terrafirmaagescore.block.custom;

import com.terrafirmaagescore.TerraFirmaAgesCore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.Direction;

public class Farm_Block extends SlabBlock{

    public Farm_Block(BlockBehaviour.Properties properties) {
        super(properties);
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