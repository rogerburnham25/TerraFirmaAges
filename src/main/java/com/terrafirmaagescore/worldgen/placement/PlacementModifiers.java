package com.terrafirmaagescore.worldgen.placement;

import com.mojang.serialization.MapCodec;
import com.terrafirmaagescore.TerraFirmaAgesCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PlacementModifiers {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS = 
        DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, "terrafirmaagescore");
    
    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<TfcContinentPlacementFilter>> TFC_CONTINENT_FILTER = 
        PLACEMENT_MODIFIERS.register("tfc_continent_filter", () -> register(TfcContinentPlacementFilter.CODEC));

    // Mojang's standard static registration bridge format for 1.21.1 world-gen parameters
    private static <P extends PlacementModifier> PlacementModifierType<P> register(MapCodec<P> codec) {
        return () -> codec;
    }
}
