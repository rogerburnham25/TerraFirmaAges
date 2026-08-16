package com.terrafirmaagescore.event;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

import com.terrafirmaagescore.entity.ModEntities;
import com.terrafirmaagescore.entity.custom.NeolithicColonistEntity;
import com.terrafirmaagescore.TerraFirmaAgesCore;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.Minecraft;

@EventBusSubscriber(modid = TerraFirmaAgesCore.MODID)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.NEOLITHIC_COLONIST.get(), NeolithicColonistEntity.createAttributes().build());
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        TerraFirmaAgesCore.LOGGER.info("HELLO FROM CLIENT SETUP");
        TerraFirmaAgesCore.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
    
}