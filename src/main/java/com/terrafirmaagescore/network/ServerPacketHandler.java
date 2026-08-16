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
                    town_center_statue.updateTownNameAndSave(payload.newName(), payload.count());
                } else {
                    System.out.println("Warning: Received update packet but TownCenterBlockEntity was not found at " + payload.pos());
                }
            }
        });
    };
}
