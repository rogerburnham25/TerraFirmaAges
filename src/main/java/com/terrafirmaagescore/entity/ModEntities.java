package com.terrafirmaagescore.entity;

import java.util.function.Supplier;

import com.terrafirmaagescore.entity.custom.NeolithicColonistEntity;
import com.terrafirmaagescore.TerraFirmaAgesCore;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
    DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, TerraFirmaAgesCore.MODID);

    public static final Supplier<EntityType<NeolithicColonistEntity>> NEOLITHIC_COLONIST = ENTITY_TYPES.register("neolithic_colonist", () -> EntityType.Builder.of(NeolithicColonistEntity::new, MobCategory.CREATURE).sized(0.75F, 1.75F).build("neolithic_colonist"));

    public static void register (IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}