package com.terrafirmaagescore.network;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.terrafirmaagescore.block.custom.TownCenterBlockEntity;

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
}
