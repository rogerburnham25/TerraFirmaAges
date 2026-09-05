package com.terrafirmaagescore.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RequestRoadCheckPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<RequestRoadCheckPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("terrafirmaagescore", "request_road_check"));

    public static final StreamCodec<FriendlyByteBuf, RequestRoadCheckPayload> STREAM_CODEC = StreamCodec.of(
        (buf, val) -> buf.writeBlockPos(val.pos()),
        buf -> new RequestRoadCheckPayload(buf.readBlockPos())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
