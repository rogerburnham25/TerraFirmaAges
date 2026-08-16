package com.terrafirmaagescore.datagen;

import com.terrafirmaagescore.TerraFirmaAgesCore;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import com.terrafirmaagescore.block.custom.Farm_Block;

import java.util.Map;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, TerraFirmaAgesCore.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(Farm_Block.FARM_BLOCK.get());
    }

    // @Override
    // protected void registerStatesAndModels() {
    //     // Shared location of your single independent overlay texture file
    //     ResourceLocation overlayTex = ResourceLocation.fromNamespaceAndPath(TerraFirmaAgesCore.MODID, "block/farm_block_overlay");

    //     // Loop through all entries that were registered in our map
    //     for (Map.Entry<ResourceLocation, DeferredBlock<Block>> entry : Farm_Block.DYNAMIC_FARM_BLOCKS.entrySet()) {
    //         ResourceLocation baseId = entry.getKey();
    //         Block farmBlockVariant = entry.getValue().get();
    //         String name = entry.getValue().getId().getPath();

    //         // Dynamic location mapping back to the vanilla base textures (e.g. "minecraft:block/dirt")
    //         ResourceLocation baseTex = ResourceLocation.fromNamespaceAndPath(baseId.getNamespace(), "block/" + baseId.getPath());

    //         // 1. Generate the Block Model using the multi-texture layered cube parent
    //         BlockModelBuilder model = models().withExistingParent(name, ResourceLocation.withDefaultNamespace("block/cube"))
    //             .renderType("minecraft:cutout") // Keeps the overlay transparent zones clean
    //             .texture("particle", baseTex)
    //             .texture("down", baseTex)
    //             .texture("up", overlayTex) // Layer your clear overlay on the top face
    //             .texture("north", baseTex)
    //             .texture("east", baseTex)
    //             .texture("south", baseTex)
    //             .texture("west", baseTex);

    //         // 2. Generate the BlockState JSON mapping to the generated model file
    //         simpleBlock(farmBlockVariant, model);

    //         // 3. Generate the BlockItem inventory model mapping to the block model layout
    //         simpleBlockItem(farmBlockVariant, model);
    //     }
    // }
}
