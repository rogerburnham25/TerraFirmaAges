package com.terrafirmaagescore.worldgen.placement;

import com.terrafirmaagescore.worldgen.ContinentFilter;
import com.terrafirmaagescore.worldgen.placement.PlacementModifiers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

public class TfcContinentPlacementFilter extends PlacementModifier {
    
    public static final MapCodec<TfcContinentPlacementFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        instance.group(
            Codec.INT.fieldOf("plant_id").forGetter(filter -> filter.plant_id)
        ).apply(instance, TfcContinentPlacementFilter::new)
    );

    private final int plant_id;

    public TfcContinentPlacementFilter(int plant_id) {
        this.plant_id = plant_id;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        boolean isValidCrop = ContinentFilter.canPlantSpawn(context.getLevel(), pos, context.generator(), this.plant_id);

        if (isValidCrop) {
            java.util.List<BlockPos> patchCluster = new java.util.ArrayList<>();

            patchCluster.add(pos);

            int cropBlockCount = 6;

            for (int i = 0; i < cropBlockCount; i++) {
                int offsetX = random.nextInt(9) - 4;
                int offsetZ = random.nextInt(9) - 4;

                BlockPos offsetPos = pos.offset(offsetX, 0, offsetZ);

                patchCluster.add(offsetPos);
            }
            return patchCluster.stream();
        }

        return Stream.empty();
    }

    @Override 
    public PlacementModifierType<?> type() {
        return PlacementModifiers.TFC_CONTINENT_FILTER.get();
    }
}
