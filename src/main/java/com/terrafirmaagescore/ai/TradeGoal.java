package com.terrafirmaagescore.ai;

import java.io.*;
import java.util.EnumSet;

import com.terrafirmaagescore.block.custom.TownCenterBlockEntity;
import com.terrafirmaagescore.block.custom.TownData;
import com.terrafirmaagescore.block.custom.Town_Center_Statue;
import com.terrafirmaagescore.entity.custom.TradeCaravanEntity;

import net.minecraft.world.level.Level;
import com.terrafirmaagescore.util.RoadDetector;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import java.util.*;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.phys.Vec3;

import static com.terrafirmaagescore.util.ColonyNetworkManager.getActiveStatues;

public class TradeGoal extends Goal {
    private BlockPos targetPos;
    private final TradeCaravanEntity TradeCaravan;
    private int pathRecalculateCooldown;
    private int pathFindingDelay = 0;
    private int scanCooldown = 0;

    public TradeGoal(TradeCaravanEntity Caravan) {
        this.TradeCaravan = Caravan;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    private static final List<TagKey<Block>> ROAD_TAGS = List.of(
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("blockrunner", "slightly_quick_blocks")),
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("blockrunner", "quick_blocks")),
            TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("blockrunner", "very_quick_blocks"))
    );

    @Override
    public boolean canUse() {
        if (--this.scanCooldown > 0) {
            return false;
        }
        this.scanCooldown = 40;

        BlockPos center = TradeCaravan.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(
            center.offset(-20, -5, -20),
            center.offset(20, 5, 20))) {

            BlockEntity town = TradeCaravan.level().getBlockEntity(pos);

            if (town instanceof TownCenterBlockEntity townCenterBlockEntity) {
                List<BlockEntity> RoadNetwork = RoadDetector.coloniesInRange(TradeCaravan.level(), pos.immutable());
                if (!RoadNetwork.isEmpty()) {
                    this.targetPos = RoadNetwork.getFirst().getBlockPos().immutable();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.targetPos == null) return false;
        if (TradeCaravan.blockPosition().closerThan(this.targetPos, 2.5D)) {
            return false;
        }
        return true;
    }

    public static boolean isRoad(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        for (TagKey<Block> tag : ROAD_TAGS) {
            if (state.is(tag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        this.pathRecalculateCooldown = 0;
        this.pathFindingDelay = 0;

        tryMoveToTarget();
    }

    public void tick() {
        if (this.targetPos == null) return;

        if (--this.pathFindingDelay > 0) {
            this.pathFindingDelay--;
        }

        if (--this.pathRecalculateCooldown <= 0) {


            BlockPos currentPos = TradeCaravan.blockPosition();

            if (isRoad(TradeCaravan.level(), currentPos.below()) || isRoad(TradeCaravan.level(), currentPos)) {
                this.pathRecalculateCooldown = 30;
            } else {
                this.pathRecalculateCooldown = 70;
            }
            tryMoveToTarget();
        }
    }

    private void tryMoveToTarget() {
        if (this.targetPos != null) {
            Vec3 currentVec = this.TradeCaravan.position();

            Vec3 targetVec = new Vec3(this.targetPos.getX() + 0.5D, this.targetPos.getY(), this.targetPos.getZ() + 0.5D);

            double distance = currentVec.distanceTo(targetVec);

            Vec3 pathingStep = targetVec;

            if (distance > 16.0D) {
                Vec3 direction = targetVec.subtract(currentVec).normalize();
                pathingStep = currentVec.add(direction.scale(16.0D));
            }

            this.TradeCaravan.getNavigation().moveTo(
                    pathingStep.x,
                    pathingStep.y,
                    pathingStep.z,
                    1.0D
            );
        }
    }

    @Override
    public void stop() {
        this.targetPos = null;
        this.TradeCaravan.getNavigation().stop();
    }
}
