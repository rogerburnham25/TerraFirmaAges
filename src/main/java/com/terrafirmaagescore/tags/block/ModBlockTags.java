package com.terrafirmaagescore.tags.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
//import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

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