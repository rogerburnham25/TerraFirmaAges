package com.terrafirmaagescore.ai;

import java.util.EnumSet;

import com.terrafirmaagescore.entity.custom.NeolithicColonistEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;

public class EmptyInventoryGoal extends Goal {

    private final NeolithicColonistEntity colonist;
    private BlockPos targetChest;

    private int transferTime = 0;

    public EmptyInventoryGoal(NeolithicColonistEntity colonist) {
        this.colonist = colonist;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {

        // Don't run immediately after the colonist is created.
        if (colonist.tickCount < 60) {
            return false;
        }

        // Find a nearby chest containing space for at least one item.
        BlockPos center = colonist.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-20, -5, -20),
                center.offset(20, 5, 20))) {

            BlockState state = colonist.level().getBlockState(pos);

            if (state.getBlock() == Blocks.CHEST) {

                BlockEntity blockEntity =
                        colonist.level().getBlockEntity(pos);

                if (blockEntity instanceof ChestBlockEntity chest) {

                    if (hasItemsToDeposit(chest)) {
                        targetChest = pos.immutable();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Checks whether the colonist has anything in its inventory
     * that can potentially be deposited.
     */
    private boolean hasItemsToDeposit(ChestBlockEntity chest) {

        for (int colonistSlot = 0;
             colonistSlot < colonist.getInventory().getSlots();
             colonistSlot++) {

            ItemStack stack =
                    colonist.getInventory().getStackInSlot(colonistSlot);

            if (stack.isEmpty()) {
                continue;
            }

            ItemStack remaining = stack.copy();

            for (int chestSlot = 0;
                 chestSlot < chest.getContainerSize();
                 chestSlot++) {

                ItemStack chestStack = chest.getItem(chestSlot);

                if (chestStack.isEmpty()) {
                    return true;
                }

                if (ItemStack.isSameItemSameComponents(
                        chestStack,
                        remaining)) {

                    int space =
                            chest.getMaxStackSize() - chestStack.getCount();

                    if (space > 0) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {

        if (targetChest == null) {
            return false;
        }

        BlockEntity blockEntity =
                colonist.level().getBlockEntity(targetChest);

        if (!(blockEntity instanceof ChestBlockEntity chest)) {
            return false;
        }

        return hasItemsToDeposit(chest);
    }

    @Override
    public void start() {

        if (targetChest != null) {

            colonist.getNavigation().moveTo(
                    targetChest.getX() + 0.5,
                    targetChest.getY(),
                    targetChest.getZ() + 0.5,
                    1.0
            );
        }
    }

    @Override
    public void stop() {

        targetChest = null;
        transferTime = 0;

        colonist.getNavigation().stop();
    }

    @Override
    public void tick() {

        if (targetChest == null) {
            return;
        }

        double distanceSquared =
                colonist.distanceToSqr(
                        targetChest.getX() + 0.5,
                        targetChest.getY(),
                        targetChest.getZ() + 0.5
                );

        // Not close enough yet.
        if (distanceSquared >= 2.25) {

            if (colonist.getNavigation().isDone()) {

                colonist.getNavigation().moveTo(
                        targetChest.getX() + 0.5,
                        targetChest.getY(),
                        targetChest.getZ() + 0.5,
                        1.0
                );
            }

            return;
        }

        // Stop walking while emptying the inventory.
        colonist.getNavigation().stop();

        transferTime++;

        // Give the colonist a short delay before transferring.
        if (transferTime < 10) {
            return;
        }

        transferTime = 0;

        BlockEntity blockEntity =
                colonist.level().getBlockEntity(targetChest);

        if (!(blockEntity instanceof ChestBlockEntity chest)) {
            targetChest = null;
            return;
        }

        emptyInventoryIntoChest(chest);
    }

    /**
     * Attempts to transfer everything in the colonist's inventory
     * into the target chest.
     */
    private void emptyInventoryIntoChest(ChestBlockEntity chest) {

        for (int colonistSlot = 0;
             colonistSlot < colonist.getInventory().getSlots();
             colonistSlot++) {

            ItemStack stack =
                    colonist.getInventory().getStackInSlot(colonistSlot);

            if (stack.isEmpty()) {
                continue;
            }

            ItemStack remaining = stack.copy();

            for (int chestSlot = 0;
                 chestSlot < chest.getContainerSize();
                 chestSlot++) {

                if (remaining.isEmpty()) {
                    break;
                }

                ItemStack chestStack = chest.getItem(chestSlot);

                // Put items into an empty slot.
                if (chestStack.isEmpty()) {

                    int amount =
                            Math.min(
                                    remaining.getCount(),
                                    chest.getMaxStackSize()
                            );

                    ItemStack toInsert = remaining.copy();
                    toInsert.setCount(amount);

                    chest.setItem(chestSlot, toInsert);

                    remaining.shrink(amount);
                }

                // Stack with an existing matching stack.
                else if (ItemStack.isSameItemSameComponents(
                        chestStack,
                        remaining)) {

                    int space =
                            chest.getMaxStackSize()
                                    - chestStack.getCount();

                    if (space > 0) {

                        int amount =
                                Math.min(
                                        remaining.getCount(),
                                        space
                                );

                        chestStack.grow(amount);
                        remaining.shrink(amount);

                        chest.setItem(chestSlot, chestStack);
                    }
                }
            }

            // Update the colonist's inventory with anything
            // that couldn't fit.
            colonist.getInventory().setStackInSlot(
                    colonistSlot,
                    remaining
            );
        }

        chest.setChanged();

        // Check whether everything was successfully deposited.
        if (!hasAnyItems()) {
            targetChest = null;
        }
    }

    /**
     * Returns true if the colonist still has anything
     * in its inventory.
     */
    private boolean hasAnyItems() {

        for (int slot = 0;
             slot < colonist.getInventory().getSlots();
             slot++) {

            if (!colonist.getInventory()
                    .getStackInSlot(slot)
                    .isEmpty()) {

                return true;
            }
        }

        return false;
    }
}