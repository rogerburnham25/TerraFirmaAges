package com.terrafirmaagescore.entity.client;

import com.terrafirmaagescore.entity.custom.TradeCaravanEntity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class TradeCaravanModel extends GeoModel<TradeCaravanEntity> {

    @Override
    public ResourceLocation getModelResource(TradeCaravanEntity entity) {
        //System.out.println("USING TERRAFIRMAAGESCORE MODEL");
        return ResourceLocation.fromNamespaceAndPath(
            "terrafirmaagescore",
            "geo/trade_caravan.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(TradeCaravanEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
            "terrafirmaagescore",
            "textures/entity/neolithic_colonist.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(TradeCaravanEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(
            "terrafirmaagescore",
            "animations/trade_caravan.animation.json"
        );
    }
}