package com.terrafirmaagescore.entity.client;

import com.terrafirmaagescore.entity.custom.NeolithicColonistEntity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class NeolithicColonistModel extends GeoModel<NeolithicColonistEntity> {

    @Override
    public ResourceLocation getModelResource(NeolithicColonistEntity entity) {
        System.out.println("USING TERRAFIRMAAGESCORE MODEL");
        return ResourceLocation.fromNamespaceAndPath(
            "terrafirmaagescore",
            "geo/neolithic_colonist.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(NeolithicColonistEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
            "terrafirmaagescore",
            "textures/entity/neolithic_colonist.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(NeolithicColonistEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
            "terrafirmaagescore",
            "animations/neolithic_colonist.animation.json"
        );
    }
}