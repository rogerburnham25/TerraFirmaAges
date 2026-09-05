package com.terrafirmaagescore.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncRoadsPayload(String r1, String r2, String r3, String r4, String r5) implements CustomPacketPayload {
    public static final Type<SyncRoadsPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("terrafirmaagescore", "sync_roads"));

    public static final StreamCodec<FriendlyByteBuf, SyncRoadsPayload> STREAM_CODEC = StreamCodec.of(
        (buf, val) -> {
            buf.writeUtf(val.r1());
            buf.writeUtf(val.r2());
            buf.writeUtf(val.r3());
            buf.writeUtf(val.r4());
            buf.writeUtf(val.r5());
        },
        buf -> new SyncRoadsPayload(buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf(), buf.readUtf())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}