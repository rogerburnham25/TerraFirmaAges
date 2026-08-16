package com.terrafirmaagescore.tags.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
//import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import com.terrafirmaagescore.TerraFirmaAgesCore;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.terrafirmaagescore.block.custom.Town_Center_Statue;

//public class ModTags {
    //public static final TagKey<Item> SEEDS = ItemTags.create(
        //ResourceLocation.fromNamespaceAndPath("terrafirmacraft", "c:seeds")
    //);
//}


public class ModBlockTags {
    // Reference to a block tag (#mymod:valuable_blocks)
    public static final TagKey<Block> TOWN_NAME = BlockTags.create(
        ResourceLocation.fromNamespaceAndPath("terrafirmaagescore", "Town_Center_Statue")
    );
}