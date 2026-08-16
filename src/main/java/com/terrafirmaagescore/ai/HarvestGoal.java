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
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.blocks.crop.Crop;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.ai.goal.Goal;
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

public class HarvestGoal extends Goal {

    private BlockPos targetCrop;
    private final NeolithicColonistEntity NeolithicColonist;

    public HarvestGoal(NeolithicColonistEntity Colonist) {
        this.NeolithicColonist = Colonist;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private static final Map<Supplier<? extends Block>, Supplier<? extends Item>> CROP_DROPS;

    static {
        Map<Supplier<? extends Block>, Supplier<? extends Item>> map = new HashMap<>();
        map.put(() -> TFCBlocks.CROPS.get(Crop.ALFALFA).get(), () ->  TFCItems.ALFALFA.asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.BARLEY).get(), () -> TFCItems.FOOD.get(Food.BARLEY).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.BEET).get(), () -> TFCItems.FOOD.get(Food.BEET).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.CABBAGE).get(), () -> TFCItems.FOOD.get(Food.CABBAGE).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.CANOLA).get(), () -> TFCItems.FOOD.get(Crop.CANOLA).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.CARROT).get(), () -> TFCItems.FOOD.get(Food.CARROT).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.CASSAVA).get(), () -> TFCItems.FOOD.get(Food.CASSAVA).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.GARLIC).get(), () -> TFCItems.FOOD.get(Food.GARLIC).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.GREEN_BEAN).get(), () -> TFCItems.FOOD.get(Food.GREEN_BEAN).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.JUTE).get(), () -> TFCItems.FOOD.get(Crop.JUTE).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.LENTIL).get(), () -> TFCItems.FOOD.get(Food.LENTIL).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.MAIZE).get(), () -> TFCItems.FOOD.get(Food.MAIZE).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.MELON).get(), () -> TFCItems.FOOD.get(Crop.MELON).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.OAT).get(), () -> TFCItems.FOOD.get(Food.OAT).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.ONION).get(), () -> TFCItems.FOOD.get(Food.ONION).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.PAPYRUS).get(), () -> TFCItems.FOOD.get(Crop.PAPYRUS).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.PEANUT).get(), () -> TFCItems.FOOD.get(Food.PEANUT).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.POTATO).get(), () -> TFCItems.FOOD.get(Food.POTATO).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.PUMPKIN).get(), () -> TFCItems.FOOD.get(Crop.PUMPKIN).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.RADISH).get(), () -> TFCItems.FOOD.get(Food.RADISH).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.RED_BELL_PEPPER).get(), () -> TFCItems.FOOD.get(Food.RED_BELL_PEPPER).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.RICE).get(), () -> TFCItems.FOOD.get(Food.RICE).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.RYE).get(), () -> TFCItems.FOOD.get(Food.RYE).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.SOYBEAN).get(), () -> TFCItems.FOOD.get(Food.SOYBEAN).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.SUGARCANE).get(), () -> TFCItems.FOOD.get(Food.SUGARCANE).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.TOMATO).get(), () -> TFCItems.FOOD.get(Food.TOMATO).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.WHEAT).get(), () -> TFCItems.FOOD.get(Food.WHEAT).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.YELLOW_BELL_PEPPER).get(), () -> TFCItems.FOOD.get(Food.YELLOW_BELL_PEPPER).asItem());

    };
    static {
        Map<Supplier<? extends Block>, Supplier<? extends Item>> map = new HashMap<>();
        map.put(() -> TFCBlocks.CROPS.get(Crop.ALFALFA).get(), () -> TFCItems.CROP_SEEDS.get(Crop.ALFALFA).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.BARLEY).get(), () -> TFCItems.CROP_SEEDS.get(Crop.BARLEY).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.BEET).get(), () -> TFCItems.CROP_SEEDS.get(Crop.BEET).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.CABBAGE).get(), () -> TFCItems.CROP_SEEDS.get(Crop.CABBAGE).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.CANOLA).get(), () -> TFCItems.CROP_SEEDS.get(Crop.CANOLA).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.CARROT).get(), () -> TFCItems.CROP_SEEDS.get(Crop.CARROT).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.CASSAVA).get(), () -> TFCItems.CROP_SEEDS.get(Crop.CASSAVA).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.GARLIC).get(), () -> TFCItems.CROP_SEEDS.get(Crop.GARLIC).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.GREEN_BEAN).get(), () -> TFCItems.CROP_SEEDS.get(Crop.GREEN_BEAN).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.JUTE).get(), () -> TFCItems.CROP_SEEDS.get(Crop.JUTE).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.LENTIL).get(), () -> TFCItems.CROP_SEEDS.get(Crop.LENTIL).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.MAIZE).get(), () -> TFCItems.CROP_SEEDS.get(Crop.MAIZE).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.MELON).get(), () -> TFCItems.CROP_SEEDS.get(Crop.MELON).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.OAT).get(), () -> TFCItems.CROP_SEEDS.get(Crop.OAT).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.ONION).get(), () -> TFCItems.CROP_SEEDS.get(Crop.ONION).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.PAPYRUS).get(), () -> TFCItems.CROP_SEEDS.get(Crop.PAPYRUS).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.PEANUT).get(), () -> TFCItems.CROP_SEEDS.get(Crop.PEANUT).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.POTATO).get(), () -> TFCItems.CROP_SEEDS.get(Crop.POTATO).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.PUMPKIN).get(), () -> TFCItems.CROP_SEEDS.get(Crop.PUMPKIN).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.RADISH).get(), () -> TFCItems.CROP_SEEDS.get(Crop.RADISH).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.RED_BELL_PEPPER).get(), () -> TFCItems.CROP_SEEDS.get(Crop.RED_BELL_PEPPER).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.RICE).get(), () -> TFCItems.CROP_SEEDS.get(Crop.RICE).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.RYE).get(), () -> TFCItems.CROP_SEEDS.get(Crop.RYE).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.SOYBEAN).get(), () -> TFCItems.CROP_SEEDS.get(Crop.SOYBEAN).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.SUGARCANE).get(), () -> TFCItems.CROP_SEEDS.get(Crop.SUGARCANE).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.TOMATO).get(), () -> TFCItems.CROP_SEEDS.get(Crop.TOMATO).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.WHEAT).get(), () -> TFCItems.CROP_SEEDS.get(Crop.WHEAT).asItem());
        map.put(() -> TFCBlocks.CROPS.get(Crop.YELLOW_BELL_PEPPER).get(), () -> TFCItems.CROP_SEEDS.get(Crop.YELLOW_BELL_PEPPER).asItem());

        CROP_DROPS = Collections.unmodifiableMap(map);
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
                center.offset(-20, -5, -20),
                center.offset(20, 5, 20))) {

            BlockState state = NeolithicColonist.level().getBlockState(pos);
            BlockEntity blockEntity = NeolithicColonist.level().getBlockEntity(pos);

            if (state.getBlock() instanceof CropBlock cropblock) {
                    int age = state.getValue(cropblock.getAgeProperty());
                    int maxAge = cropblock.getMaxAge();
                    float growth = (float) age / (float) maxAge;

                    if (growth >= 1.0F && PerimeterDetectorBlockEntity.isBlockInsideArea(
                            NeolithicColonist.level(),
                            pos,
                            Farm_Block.FARM_BLOCK.get())) {

                    targetCrop = pos.immutable();
                    return true;
                            
                }
            }
        }
        return false;
    };

    @Override
    public void start() {
        if (targetCrop != null) {
            NeolithicColonist.getNavigation().moveTo(
                targetCrop.getX(),
                targetCrop.getY(),
                targetCrop.getZ(),
                1.0
            );
        }
    }

    @Override
    public void stop() {
        targetCrop = null;
        NeolithicColonist.getNavigation().stop();
    }

    private int harvestTime = 0;
        @Override
        public void tick() {
            if (targetCrop == null)
                return;

            if (NeolithicColonist.distanceToSqr(
                targetCrop.getX() + 0.5,
                targetCrop.getY(),
                targetCrop.getZ() + 0.5) < 2.25) {

        // Stop walking
                NeolithicColonist.getNavigation().stop();

        // Start animation once
                if (harvestTime == 0) {
                    harvestTime = 20;
                    NeolithicColonist.triggerAnim("actions", "harvest");
                }   

            harvestTime--;

            if (harvestTime <= 0) {
                BlockState state =
                        NeolithicColonist.level().getBlockState(targetCrop);

                BlockEntity blockEntity = NeolithicColonist.level().getBlockEntity(targetCrop);

                if (blockEntity instanceof CropBlockEntity cropEntity) {

                    if (cropEntity.getGrowth() >= 1.0F) {

                        Item cropItem = null;
                        Block worldBlock = state.getBlock();

                        for (Map.Entry<Supplier<? extends Block>, Supplier<? extends Item>> entry : CROP_DROPS.entrySet()) {
                            if (entry.getKey().get() == worldBlock) {
                                cropItem = entry.getValue().get();
                                break; // Match found, exit the loop
                            }
                        }

                        if (cropItem != null) {
                            ItemStack harvest = new ItemStack(
                                    cropItem,
                                    Math.max(1, Math.round(cropEntity.getYield()))
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
                        }
                        for (var entry : TFCBlocks.CROPS.entrySet()) {

                            if (entry.getValue().get() == state.getBlock()) {

                                Item seedItem = TFCItems.CROP_SEEDS.get(entry.getKey()).asItem();

                                ItemStack seeds = new ItemStack(seedItem, 2);

                                ItemStack remainingSeeds = seeds;

                                for (int slot = 0; slot < NeolithicColonist.getInventory().getSlots(); slot++) {
                                    remainingSeeds = NeolithicColonist.getInventory()
                                            .insertItem(slot, remainingSeeds, false);

                                    if (remainingSeeds.isEmpty()) {
                                        break;
                                    }
                                }

                                if (!remainingSeeds.isEmpty()) {
                                    NeolithicColonist.spawnAtLocation(remainingSeeds);
                                }

                                break;
                            }
                        }

                        NeolithicColonist.level().setBlock(
                                targetCrop,
                                Blocks.AIR.defaultBlockState(),
                                3
                        );
                    }
                }

                targetCrop = null;
            }
        }
    }
@Override
public boolean canContinueToUse() {
    if (targetCrop == null)
        return false;

    BlockState state = NeolithicColonist.level().getBlockState(targetCrop);

    if (!(state.getBlock() instanceof CropBlock crop))
        return false;

    BlockEntity blockEntity =
        NeolithicColonist.level().getBlockEntity(targetCrop);

    if (!(blockEntity instanceof CropBlockEntity cropEntity))
        return false;

    return cropEntity.getGrowth() >= 1.0F;
}
}
