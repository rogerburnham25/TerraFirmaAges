package com.terrafirmaagescore.entity.client;

import software.bernie.geckolib.renderer.GeoEntityRenderer;

import com.terrafirmaagescore.entity.custom.NeolithicColonistEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class NeolithicColonistRenderer extends GeoEntityRenderer<NeolithicColonistEntity> {

    public NeolithicColonistRenderer(EntityRendererProvider.Context context) {
        super(context, new NeolithicColonistModel());
    }
}