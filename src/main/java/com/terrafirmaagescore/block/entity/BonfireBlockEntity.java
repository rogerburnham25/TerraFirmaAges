package com.terrafirmaagescore.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.terrafirmaagescore.block.custom.Bonfire;

public class BonfireBlockEntity extends BlockEntity {
    private int burnTimeRemaining = 0;

    public BonfireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BONFIRE.get(), pos, state);
    }

    public void addFuel(int ticks) {
        this.burnTimeRemaining += ticks;
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BonfireBlockEntity blockEntity) {
        if (blockEntity.burnTimeRemaining > 0) {
            blockEntity.burnTimeRemaining--;

            if (blockEntity.burnTimeRemaining <=0) {
                level.setBlock(pos, state.setValue(Bonfire.LIT, false), 3);
            }
            blockEntity.setChanged();
        } else {
        // Fallback: If it's ticking but fuel is 0, turn off the light property manually
            if (state.getValue(Bonfire.LIT)) {
                level.setBlock(pos, state.setValue(Bonfire.LIT, false), 3);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("BurnTime", this.burnTimeRemaining);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.burnTimeRemaining = tag.getInt("BurnTime");
    }
}
