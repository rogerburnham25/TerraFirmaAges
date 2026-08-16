package com.terrafirmaagescore.block.custom;

import com.terrafirmaagescore.TerraFirmaAgesCore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.Direction;
import com.terrafirmaagescore.block.custom.Town_Center_Statue;
import com.terrafirmaagescore.block.entity.ModBlockEntities;

import java.util.*;

public class CityRadiusBlock extends Town_Center_Statue {
    public CityRadiusBlock(Properties properties) {
        super(properties);
    }

    // @Nullable
    // public BlockEntity newBlockEntity(BlockPos pos, BlockState State) {
    //     return new CityRadiusBlockEntity(pos, State);
    // }

    // @Override
    // protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    //     if (!level.isClientSide) {
    //         BlockEntity be = level.getBlockEntity(pos);
    //         if (be instanceof CityRadiusBlockEntity detectorBe) {
    //             detectorBe.triggerRadiusCheck();
    //             return InteractionResult.SUCCESS;
    //         }
    //     }
    //     return InteractionResult.SUCCESS;
    // }
}
