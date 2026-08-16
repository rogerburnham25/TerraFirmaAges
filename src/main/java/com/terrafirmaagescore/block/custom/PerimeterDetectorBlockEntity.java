package com.terrafirmaagescore.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import com.terrafirmaagescore.block.entity.ModBlockEntities;
import com.terrafirmaagescore.block.custom.Farm_Block;
import com.terrafirmaagescore.TerraFirmaAgesCore;
import net.minecraft.world.level.block.Block;

import java.util.*;

public class PerimeterDetectorBlockEntity extends BlockEntity {
    private static final int MAX_BLOCKS_SCAN = 1000;

    public PerimeterDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PERIMETER_DETECTOR.get(), pos, state);
    }

    public void triggerPerimeterCheck() {
        Level level = this.getLevel();
        if (level == null || level.isClientSide) return;

        BlockState targetState = this.getBlockState();

        Set<BlockPos> connectedBlocks = findConnectedBlocks(level, this.worldPosition, targetState);

        Set<BlockPos> perimeterPositions = filterPerimeter(level, connectedBlocks, targetState);

        for (BlockPos perimeterPos : perimeterPositions) {
            level.setBlock(perimeterPos, Farm_Block.FARM_BLOCK.get().defaultBlockState(), 3);
        }
    }

    private Set<BlockPos> findConnectedBlocks(Level level, BlockPos startPos, BlockState targetState) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(startPos);
        visited.add(startPos);

        while (!queue.isEmpty() && visited.size() < 1000) {
            BlockPos current = queue.poll();

            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.relative(direction);

                if (!visited.contains(neighbor)) {
                    BlockState neighborState = level.getBlockState(neighbor);

                    if (neighborState.is(targetState.getBlock())) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }
        }
        return visited;
    }

    private Set<BlockPos> filterPerimeter(Level level, Set<BlockPos> allConnected, BlockState targetState) {
        Set<BlockPos> perimeter = new HashSet<>();

        for (BlockPos pos : allConnected) {
            boolean isEdge = false;

            for (Direction direction : Direction.values()) {
                BlockPos neighbor = pos.relative(direction);
                BlockState neighborState = level.getBlockState(neighbor);

                if (!neighborState.is(targetState.getBlock())) {
                    isEdge = true;
                    break;
                }
            }
            if (isEdge) {
                perimeter.add(pos);
            }
        }
        return perimeter;
    }


    public static boolean isBlockInsideArea(Level level, BlockPos cropPos, Block farmBlock) {

    List<BlockPos> corners = new ArrayList<>();

    int radius = 20;

    // Find all possible corners
    for (BlockPos pos : BlockPos.betweenClosed(
            cropPos.offset(-radius, 0, -radius),
            cropPos.offset(radius, 0, radius))) {

        if (level.getBlockState(pos).is(farmBlock)) {
            corners.add(pos.immutable());
        }
    }


    // Test every possible pair as opposite corners
    for (BlockPos corner1 : corners) {
        for (BlockPos corner2 : corners) {

            // Ignore same block
            if (corner1.equals(corner2)) {
                continue;
            }

            int minX = Math.min(corner1.getX(), corner2.getX());
            int maxX = Math.max(corner1.getX(), corner2.getX());

            int minZ = Math.min(corner1.getZ(), corner2.getZ());
            int maxZ = Math.max(corner1.getZ(), corner2.getZ());


            // Must actually form a rectangle, not a line
            if (minX == maxX || minZ == maxZ) {
                continue;
            }


            BlockPos corner3 = new BlockPos(
                    minX,
                    corner1.getY(),
                    maxZ
            );

            BlockPos corner4 = new BlockPos(
                    maxX,
                    corner1.getY(),
                    minZ
            );


            // Verify all four corners exist
            if (level.getBlockState(corner3).is(farmBlock)
                    && level.getBlockState(corner4).is(farmBlock)) {

                // Verify crop is inside
                if (cropPos.getX() > minX &&
                    cropPos.getX() < maxX &&
                    cropPos.getZ() > minZ &&
                    cropPos.getZ() < maxZ) {

                    return true;
                }
            }
        }
    }

    return false;
}
}
