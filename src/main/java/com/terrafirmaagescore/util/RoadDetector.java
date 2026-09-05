package com.terrafirmaagescore.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
//import com.therighthon.rnr.datagen;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import com.terrafirmaagescore.block.custom.Town_Center_Statue;

import java.util.*;

public class RoadDetector {
    private static final int MAX_SEARCH_NODES = 5000;

    private static final List<TagKey<Block>> ROAD_TAGS = List.of(
        TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("blockrunner", "slightly_quick_blocks")),
        TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("blockrunner", "quick_blocks")),
        TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("blockrunner", "very_quick_blocks"))
    );

    public static boolean isRoadConnected(Level level, BlockPos startBlock, BlockPos targetBlock) {
        BlockPos start = startBlock.below();
        BlockPos target = targetBlock.below();
        System.out.println("[RoadDebug] Checking connection from " + start.toShortString() + " to " + target.toShortString());

        if (!isValidRoad(level, start)) {
            System.out.println("[RoadDebug] Aborted! Start position is not a valid road block.");
            return false;
        }
        if (!isValidRoad(level, target)) {
            System.out.println("[RoadDebug] Aborted! Target position is not a valid road block.");
            return false;
        }

        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        BlockPos startImm = start.immutable();
        queue.add(startImm);
        visited.add(startImm);

        BlockPos[] directions = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, 1),
            new BlockPos(-1, 0, -1),
            new BlockPos(-1, 0, 1),
            new BlockPos(1, 0, -1)
        };

        while (!queue.isEmpty()) {
            if (visited.size() > MAX_SEARCH_NODES) {
                return false;
            }
            BlockPos current = queue.poll();

            if (current.equals(target)) {
                return true;
            }

            for (BlockPos dir : directions) {
            // 1. Get the raw forward step
                BlockPos flatNeighbor = current.offset(dir);
            
            // 2. Dynamically calculate the three height variations relative to our current step
                BlockPos[] possibleSteps = {
                    flatNeighbor,         // Flat Ground (Y+0)
                    flatNeighbor.above(), // Slope Up (Y+1)
                    flatNeighbor.below()  // Slope Down (Y-1)
                };

            // 3. Evaluate each path candidate independently
                for (BlockPos neighbor : possibleSteps) {
                    BlockPos neighborImm = neighbor.immutable();
                
                // If we haven't visited this exact coordinate and it's a valid road block
                    if (!visited.contains(neighborImm) && isValidRoad(level, neighborImm)) {
                        visited.add(neighborImm);
                        queue.add(neighborImm);
                    }
                }
            }
        }
        return false;
    }

    public static List<BlockEntity> coloniesInRange(Level level, BlockPos posA) {
    List<BlockEntity> connectedStatues = new ArrayList<>();

    int COLONY_RADIUS = 2500;
    int searchRadius = COLONY_RADIUS * 2;
    long squaredRadius = (long) searchRadius * searchRadius;

    for (BlockPos posB : com.terrafirmaagescore.util.ColonyNetworkManager.getActiveStatues()) {
        if (posB.equals(posA)) continue;
            double dx = posA.getX() - posB.getX();
            double dz = posA.getZ() - posB.getZ();
            double distanceSquared = (dx * dx) + (dz * dz);

            if (distanceSquared < squaredRadius) {
            // Grab the actual block entity from the world coordinate safely
                var blockEntity = level.getBlockEntity(posB);
                if (blockEntity != null) {
                    connectedStatues.add(blockEntity);
                }
            }
        }
        return connectedStatues;
    }

    private static boolean isValidRoad(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
    
    // Check if the block is an active instance of your statue class type
        if (state.getBlock() instanceof com.terrafirmaagescore.block.custom.Town_Center_Statue) {
            return true;
        }

    // Fall back to matching your JSON block runner tags
        for (TagKey<Block> tag : ROAD_TAGS) {
            if (state.is(tag)) {
                return true;
            }
        }
        return false;
    }
}
