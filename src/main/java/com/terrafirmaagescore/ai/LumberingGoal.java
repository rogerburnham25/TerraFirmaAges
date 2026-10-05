package com.terrafirmaagescore.ai;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.Collections;

import com.terrafirmaagescore.block.custom.PerimeterDetectorBlockEntity;
import com.terrafirmaagescore.entity.custom.NeolithicColonistEntity;
import com.terrafirmaagescore.block.custom.Farm_Block;

import net.dries007.tfc.common.blocks.crop.CropBlock;
import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.AxeLoggingHelper;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.wood.Wood;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;

import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.dries007.tfc.common.items.TFCItems;
import java.util.HashMap;

import software.bernie.geckolib.animatable.GeoEntity;

public class LumberingGoal extends Goal {

    private BlockPos targetTrunk;
    private final NeolithicColonistEntity NeolithicColonist;

    public LumberingGoal(NeolithicColonistEntity Colonist) {
        this.NeolithicColonist = Colonist;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private static final Map<Supplier<? extends Block>, Supplier<? extends Item>> WOOD_DROPS;

    static {
        Map<Supplier<? extends Block>, Supplier<? extends Item>> map = new HashMap<>();
        map.put(() -> TFCBlocks.WOODS.get(Wood.ACACIA).get(Wood.BlockType.LOG).get(), () -> TFCBlocks.WOODS.get(Wood.ACACIA).get(Wood.BlockType.LOG).get().asItem());
        map.put(() -> TFCBlocks.WOODS.get(Wood.ACACIA).get(Wood.BlockType.STRIPPED_LOG).get(), () -> TFCBlocks.WOODS.get(Wood.ACACIA).get(Wood.BlockType.STRIPPED_LOG).get().asItem()   );
        WOOD_DROPS = map;
    };

    @Override
    public boolean canUse() {
        if (NeolithicColonist.tickCount < 60) {
            return false;
        }

        if (harvestTime > 0) {
            return false;
        }

        BlockPos center = NeolithicColonist.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
            center.offset(-20, -2, -20),
            center.offset(20, 2, 20))) {

            BlockState state = NeolithicColonist.level().getBlockState(pos);
            Block block = state.getBlock();

            for (Wood wood : Wood.values()) {
                if (TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LOG).get() == block) {
                    System.out.println("waaaaa");
                    targetTrunk = pos.immutable();
                    System.out.println(targetTrunk);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void start() {
        if (targetTrunk != null) {
            NeolithicColonist.getNavigation().moveTo(
                targetTrunk.getX() + 0.5,
                targetTrunk.getY(),
                targetTrunk.getZ() + 0.5,
                1.0
            );
        }
    }

    @Override
    public void stop() {
        targetTrunk = null;
        NeolithicColonist.getNavigation().stop();
    }

    private int harvestTime = 0;
        @Override
        public void tick() {
            if (targetTrunk == null)
                return;

            double dx = NeolithicColonist.getX() - (targetTrunk.getX());
            double dz = NeolithicColonist.getZ() - (targetTrunk.getZ());

            if (dx * dx + dz * dz < 2.25) {
                // Close enough horizontally

        // Stop walking
                NeolithicColonist.getNavigation().stop();

        // Start animation once
                if (harvestTime == 0) {
                    harvestTime = 20;
                    NeolithicColonist.triggerAnim("actions", "harvest");
                }   
            System.out.println("harvestTime is " + harvestTime);

            harvestTime--;

            if (harvestTime <= 0) {
                System.out.println("Blerhb");
                BlockState state =
                        NeolithicColonist.level().getBlockState(targetTrunk);

                if (NeolithicColonist.level().getBlockState(targetTrunk).getBlock() instanceof Block) {

                    Item logItem = null;
                    Block worldBlock = state.getBlock();

                        for (Map.Entry<Supplier<? extends Block>, Supplier<? extends Item>> entry : WOOD_DROPS.entrySet()) {
                            if (entry.getKey().get() == worldBlock) {
                                logItem = entry.getValue().get();
                                System.out.println("LogItem has been set to " + logItem);
                                break; // Match found, exit the loop
                            }
                        }

                        System.out.println("LogItem is " + logItem);

                        List<BlockPos> logs = AxeLoggingHelper.findLogs(
                            NeolithicColonist.level(),
                            targetTrunk
                        );

                        if (logItem != null) {
                            ItemStack harvest = new ItemStack(
                                    logItem,
                                    logs.size()
                            );

                            ItemStack remaining = harvest;

                            for (int slot = 0; slot < NeolithicColonist.getInventory().getSlots(); slot++) {
                                remaining = NeolithicColonist.getInventory()
                                        .insertItem(slot, remaining, false);

                                if (remaining.isEmpty()) {
                                    break;
                                }
                            }

                            if (!remaining.isEmpty()) {
                                NeolithicColonist.spawnAtLocation(remaining);
                            }

                            for (BlockPos log : logs) {
                                NeolithicColonist.level().destroyBlock(log, false);
}
                            System.out.println("Block broken at " + targetTrunk);
                        }

                }

                targetTrunk = null;
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (targetTrunk == null)
            return false;

        BlockState state = NeolithicColonist.level().getBlockState(targetTrunk);
        Block block = state.getBlock();

        for (Wood wood : Wood.values()) {
            if (TFCBlocks.WOODS.get(wood)
                    .get(Wood.BlockType.LOG)
                    .get() == block) {
                return true;
            }
        }

        return false;
    }
}