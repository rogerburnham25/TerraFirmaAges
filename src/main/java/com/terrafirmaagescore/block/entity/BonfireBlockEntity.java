package com.terrafirmaagescore.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.terrafirmaagescore.block.custom.Bonfire;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import com.terrafirmaagescore.entity.ModEntities;
import com.terrafirmaagescore.entity.custom.NeolithicColonistEntity;

public class BonfireBlockEntity extends BlockEntity {
    private int burnTimeRemaining = 0;

    public BonfireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BONFIRE.get(), pos, state);
    }

    public static boolean isInTown(Level level, BlockPos newPos) {
                int COLONY_RADIUS = 100;
                int searchRadius = COLONY_RADIUS * 2;
                int squaredRadius = searchRadius * searchRadius;

                int newX = newPos.getX();
                //int newY = newPos.getY();
                int newZ = newPos.getZ();

                for (BlockPos pos : BlockPos.betweenClosed(
                        newPos.offset(-searchRadius, -10, -searchRadius),
                        newPos.offset(searchRadius, 10, searchRadius))) {

                        if (pos.getX() == newX && pos.getZ() == newZ) {
                                continue;
                        }

                        if (level.getBlockState(pos).is(com.terrafirmaagescore.block.custom.Town_Center_Statue.TOWN_CENTER_STATUE.get())) {
            
                                double dx = newX - pos.getX();
                                double dz = newZ - pos.getZ();
                                double distanceSquared = (dx * dx) + (dz * dz);

                                if (distanceSquared < squaredRadius) {
                                        return true; 
                                }
                        }
                }
                return false; 
        } 

    public void addFuel(int ticks) {
        this.burnTimeRemaining += ticks;
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BonfireBlockEntity blockEntity) {
        if (blockEntity.burnTimeRemaining > 0) {
            //System.out.println(blockEntity.burnTimeRemaining);
            blockEntity.burnTimeRemaining--;

            if (blockEntity.burnTimeRemaining <= 0) {
                level.setBlock(pos, state.setValue(Bonfire.LIT, false), 3);
            }
            blockEntity.setChanged();
        } else {
        // Fallback: If it's ticking but fuel is 0, turn off the light property manually
            if (state.getValue(Bonfire.LIT)) {
                level.setBlock(pos, state.setValue(Bonfire.LIT, false), 3);
            }
        }
        if (blockEntity.burnTimeRemaining > 0 && isInTown(level, pos)) {
            // newPos = (BlockPos.getX() + 1, BlockPos.getY(), BlockPos.getZ());
            
            if (Math.random() <= 0.4) {
                if (level instanceof ServerLevel serverLevel) {
                    long timeOfDay = serverLevel.getDayTime() % 24000;
                    if (timeOfDay == 18000) {
                        //System.out.println("code is working");
                        BlockPos spawnPos = pos.above();
                        ModEntities.NEOLITHIC_COLONIST.get().spawn(
                            serverLevel,
                            spawnPos,
                            MobSpawnType.TRIGGERED
                        );
                    }
                }
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
