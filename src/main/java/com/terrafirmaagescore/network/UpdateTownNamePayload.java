package com.terrafirmaagescore.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.terrafirmaagescore.block.custom.Town_Center_Statue;
import com.terrafirmaagescore.client.screen.TownNameScreen;

public record UpdateTownNamePayload(BlockPos pos, String newName, String count) implements CustomPacketPayload {
    public static final Type<UpdateTownNamePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("terrafirmaagescore", "update_town_name"));

    public static final StreamCodec<FriendlyByteBuf, UpdateTownNamePayload> STREAM_CODEC = StreamCodec.of(
        (buf, val) -> {
            buf.writeBlockPos(val.pos());
            buf.writeUtf(val.newName() != null ? val.newName() : "");
            buf.writeUtf(val.count() != null ? val.count() : "");
        },
        buf -> new UpdateTownNamePayload(buf.readBlockPos(), buf.readUtf(), buf.readUtf())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
