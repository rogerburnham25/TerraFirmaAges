package com.terrafirmaagescore.entity.client;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

import com.terrafirmaagescore.entity.custom.TradeCaravanEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class TradeCaravanRenderer extends GeoEntityRenderer<TradeCaravanEntity> {

    public TradeCaravanRenderer(EntityRendererProvider.Context context) {
        super(context, new TradeCaravanModel());
    }
}