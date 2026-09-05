package com.terrafirmaagescore.network;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.terrafirmaagescore.block.custom.TownCenterBlockEntity;
import com.terrafirmaagescore.util.RoadDetector; // Replace with your actual path
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

public class ServerPacketHandler {
    public static void handleTownNameUpdate(final UpdateTownNamePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();

            if (level != null && level.isLoaded(payload.pos())) {
                BlockEntity be = level.getBlockEntity(payload.pos());
            
                if (be instanceof TownCenterBlockEntity town_center_statue) {
                    town_center_statue.setTownName(payload.newName());
                    town_center_statue.exportDataToTextFile();
                    level.sendBlockUpdated(payload.pos(), town_center_statue.getBlockState(), town_center_statue.getBlockState(), 3);
                } else {
                    System.out.println("Warning: Received update packet but TownCenterBlockEntity was not found at " + payload.pos());
                }
            }
        });
    };
    public static void handleRoadCheck(RequestRoadCheckPayload payload, ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        BlockPos startPos = payload.pos();

        List<BlockEntity> nearbyColonies = RoadDetector.coloniesInRange(level, startPos);
        String[] connectedRoads = {"", "", "", "", ""};
        int slot = 0;

        for (BlockEntity colony : nearbyColonies) {
            if (slot >= 5) break; 

            BlockPos targetPos = colony.getBlockPos();
    
            if (targetPos.equals(startPos) || targetPos.equals(startPos.above())) {
                continue;
            }

            BlockState targetState = level.getBlockState(targetPos);
            if (targetState.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                var half = targetState.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF);
                if (half == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER) {
                    continue; // Skip the upper half cycle completely!
                }
            }

            boolean connected = RoadDetector.isRoadConnected(level, startPos, targetPos);
    
            if (connected) {
                String displayName = "Unnamed Town";
                if (colony instanceof com.terrafirmaagescore.block.custom.TownCenterBlockEntity targetTown) {
                    String townName = targetTown.getTownName(); 
                    if (townName != null && !townName.isEmpty() && !"unnamed_town".equals(townName)) {
                        displayName = townName;
                    }
                }
                connectedRoads[slot] = displayName;
                slot++;
            }
        }

    // Explicitly send the unbundled values down the client pipe
        player.connection.send(new SyncRoadsPayload(
            connectedRoads[0], 
            connectedRoads[1], 
            connectedRoads[2], 
            connectedRoads[3], 
            connectedRoads[4]
        ));
    }
}
