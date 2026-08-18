package com.terrafirmaagescore.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.util.climate.Climate;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ContinentFilter {
    public static boolean canPlantSpawn(WorldGenLevel level, BlockPos pos, ChunkGenerator generator, int plantUniqueHash) {
        //System.out.println("[CRITICAL TEST] Filter is being executed by the game engine!");
        if (generator instanceof TFCChunkGenerator tfcGenerator) {
            float tfcContinentalness = tfcGenerator.settings().continentalness();
            if (tfcContinentalness < 0.35f) {
                return false;
            }
            long worldSeed = level.getSeed();

            long continentX = pos.getX() >> 14;
            long continentZ = pos.getZ() >> 14;

            long continentHash = worldSeed;
            continentHash = 31 * continentHash + continentX;
            continentHash = 31 * continentHash + continentZ;

            float continentBaseTemp = ((Math.abs(continentHash) % 400) / 10.0f) - 10.0f;
            
            boolean canSpawn = CropTemperature(plantUniqueHash, continentBaseTemp, continentHash);

            if (canSpawn) {
                System.out.println("[CONTINENT MOD] Crop ID " + plantUniqueHash + " spawned at temp: " + continentBaseTemp + " at X=" + pos.getX() + " at Z=" + pos.getZ());
            }

            return canSpawn;
        }   
        return true;
    }    

    private static boolean CropTemperature(int plant_id, float temp, long continentHash) {
        Random continentRandom = new Random(continentHash + (plant_id * 31L));

        if (temp < 8.0f) {
            return checkCrop(plant_id, new ArrayList<>(List.of(1, 3, 4, 5, 6, 8, 20, 23)), continentRandom);
            // int maxCrops = 3;

            // List<Integer> coldCrops = new ArrayList<>(List.of(1, 3, 4, 5, 6, 8, 20, 23));
            // if (!coldCrops.contains(plant_id)) return false;

            // Collections.shuffle(coldCrops, continentRandom);

            // List<Integer> allowedSelection = coldCrops.subList(0, Math.min(maxCrops, coldCrops.size()));
            // return allowedSelection.contains(plant_id);
        }

        if (temp >= 6.0f && temp <= 12.0f) {
            return checkCrop(plant_id, new ArrayList<>(List.of(2, 14, 18,  24, 25, 28)), continentRandom);
            // int maxCrops = 3;

            // List<Integer> coolCrops = new ArrayList<>(List.of(2, 14, 18,  24, 25, 28));
            // if (!coolCrops.contains(plant_id)) return false;

            // Collections.shuffle(coolCrops, continentRandom);

            // List<Integer> allowedSelection = coolCrops.subList(0, Math.min(maxCrops, coolCrops.size()));
            // return allowedSelection.contains(plant_id);
        }

        if (temp >= 12.5f && temp <= 18.0f) {
            return checkCrop(plant_id, new ArrayList<>(List.of(8, 10, 11, 12, 13, 15, 19, 27)), continentRandom);
            // int maxCrops = 3;

            // List<Integer> warmCrops = new ArrayList<>(List.of(8, 10, 11, 12, 13, 15, 19, 27));
            // if (!warmCrops.contains(plant_id)) return false;

            // Collections.shuffle(warmCrops, continentRandom);

            // List<Integer> allowedSelection = warmCrops.subList(0, Math.min(maxCrops, warmCrops.size()));
            // return allowedSelection.contains(plant_id);
        }

        if (temp > 18.0f) {
            return checkCrop(plant_id, new ArrayList<>(List.of(7, 16, 17, 21, 22, 26, 29)), continentRandom);
            // int maxCrops = 3;

            // List<Integer> hotCrops = new ArrayList<>(List.of(7, 16, 17, 21, 22, 26, 29));
            // if (!hotCrops.contains(plant_id)) return false;

            // Collections.shuffle(hotCrops, continentRandom);

            // List<Integer> allowedSelection = hotCrops.subList(0, Math.min(maxCrops, hotCrops.size()));
            // return allowedSelection.contains(plant_id);
        }
        return false;
    }

    private static boolean checkCrop(int plant_id, List<Integer> cropPool, Random random) {
        if (!cropPool.contains(plant_id)) return false;

        Collections.shuffle(cropPool, random);
        List<Integer> allowedSelection = cropPool.subList(0, Math.min(3, cropPool.size()));
        return allowedSelection.contains(plant_id);
    }
}
