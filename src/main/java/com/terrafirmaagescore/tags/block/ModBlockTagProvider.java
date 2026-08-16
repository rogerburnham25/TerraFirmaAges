package com.terrafirmaagescore.tags.block;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
//import net.minecraft.data.tags.ModBlockTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import com.terrafirmaagescore.TerraFirmaAgesCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import com.terrafirmaagescore.tags.block.ModBlockTags;
import com.terrafirmaagescore.block.custom.Town_Center_Statue; // Imports your tag name

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "terrafirmaagescore", existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        this.tag(ModBlockTags.TOWN_NAME)
            .add(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("terrafirmaagescore", "town_center_statue")));
    }
}
