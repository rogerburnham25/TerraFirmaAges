package com.terrafirmaagescore.block.entity; // Adjust package to match your structure

import com.terrafirmaagescore.block.custom.PerimeterDetectorBlockEntity;
import com.terrafirmaagescore.block.custom.Farm_Block; // Adjust to your blocks registry file
import com.terrafirmaagescore.block.custom.Town_Center_Statue;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.terrafirmaagescore.block.custom.TownCenterBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "terrafirmaagescore");

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PerimeterDetectorBlockEntity>> PERIMETER_DETECTOR =
            BLOCK_ENTITIES.register("perimeter_detector", () ->
                    BlockEntityType.Builder.of(PerimeterDetectorBlockEntity::new, Farm_Block.FARM_BLOCK.get()).build(null)
        );

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TownCenterBlockEntity>> COLONY =
            BLOCK_ENTITIES.register("colony", () ->
                BlockEntityType.Builder.of(
                    TownCenterBlockEntity::new, 
                    Town_Center_Statue.TOWN_CENTER_STATUE.get()
                ).build(null)
        );
        // public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CityRadiusBlockEntity>> CITY_RADIUS =
        //     BLOCK_ENTITIES.register("city_radius", () ->
        //             BlockEntityType.Builder.of(CityRadiusBlockEntity::new, Town_Center_Statue.TOWN_CENTER_STATUE.get()).build(null)
        // );
}
