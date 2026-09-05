package com.terrafirmaagescore.network;

import com.terrafirmaagescore.client.screen.TownNameScreen; // Replace with your actual path
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class ClientPacketHandler {

    public static void handleSyncRoads(SyncRoadsPayload payload) {
        Screen currentScreen = Minecraft.getInstance().screen;
            
            // Check if the player still has the TownNameScreen open
        if (currentScreen instanceof TownNameScreen townScreen) {
                // Update the variables inside the active screen object!
            townScreen.updateRoadStrings(
                payload.r1(), 
                payload.r2(), 
                payload.r3(), 
                payload.r4(), 
                payload.r5()
            );
        }
    }
}