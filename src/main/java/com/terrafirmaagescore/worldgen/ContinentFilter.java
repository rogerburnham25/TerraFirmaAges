package com.terrafirmaagescore.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.dries007.tfc.world.TFCChunkGenerator;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
// import net.dries007.tfc.world.chunkdata.ChunkData;

import java.util.Objects;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ContinentFilter {
    private final Map<Long, String> labelCache = new ConcurrentHashMap<>();

    private static final String[] PREFIXES = {"North ", "South ", "New ", "Old ", "Great ", "Grand ", "East ", "West ", ""};
    private static final String[] ROOTS = {"Ainti", "Broul", "Crein", "Dasuil", "Enler", "Folok", "Gean", "Heite", "Ielt", "Jeloik", "Karnn", "Loken", "Morhel", "Niutld", "Oludle", "Poiyet", "Qiuls", "Roule", "Suled", "Tikea", "Ukule", "Voula", "Woples", "Xelets", "Yolest", "Zition"};
    private static final String[] SUFFIXES = {"gard", "vale", "moor", "wood", "shire", "crag", "ford", "crest", "rock", "land"};

    public String getCellLabel(long worldSeed, float x, float z) {
        // 1. Calculate the organic cellular region hash manually using our grid math
        long continentHash = getVoronoiCellHash((int) x, (int) z, worldSeed);

        // 2. Look up the hash in the cache. If it doesn't exist, generate the name permanently.
        return labelCache.computeIfAbsent(continentHash, hashKey -> {
            // 3. Use the locked continent hash as the seed. 
            // This guarantees the name is the same across the ENTIRE organic territory.
            Random rand = new Random(hashKey);

            String prefix = (rand.nextFloat() < 0.3) ? PREFIXES[rand.nextInt(PREFIXES.length)] : "";
            String root = ROOTS[rand.nextInt(ROOTS.length)];
            String suffix = SUFFIXES[rand.nextInt(SUFFIXES.length)];

            return prefix + root + suffix;
        });
    }


    public static boolean canPlantSpawn(WorldGenLevel level, BlockPos pos, ChunkGenerator generator, int plantUniqueHash) {
        //System.out.println("[CRITICAL TEST] Filter is being executed by the game engine!");
        if (generator instanceof TFCChunkGenerator) {
            long worldSeed = level.getSeed();
            long continentHash = getVoronoiCellHash(pos.getX(), pos.getZ(), worldSeed);
            float temp = ((Math.abs(continentHash) % 400) / 10.0f) - 10.0f;

            int finalId = CropTemperature(plantUniqueHash, temp, continentHash);

            if (finalId != plantUniqueHash) {
                // 1. Find a valid replacement block state from your allowed pool
                var allowedBlock = BuiltInRegistries.BLOCK.stream()
                    .filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals("tfc"))
                    .filter(block -> {
                        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
                        return path.contains("crop") || path.contains("plant");
                    })
                    .skip(Math.abs(finalId) % 15)
                    .findFirst();

                if (allowedBlock.isPresent()) {
                    BlockState replacementState = allowedBlock.get().defaultBlockState();
                    level.setBlock(pos, replacementState, 2 | 16);
                }
                return false; 
            }
        }   
        return true; // If it's allowed, return true so the engine handles placement normally
    }  

    private static long getVoronoiCellHash(int blockX, int blockZ, long seed) {
        // Mapped to match TFC's continental sizes (approx 4096 blocks per grid sector)
        double scale = 4096.0;
        
        double xOffset = blockX / scale;
        double zOffset = blockZ / scale;

        int cellX = (int) Math.floor(xOffset);
        int cellZ = (int) Math.floor(zOffset);

        double minDistance = Double.MAX_VALUE;
        int closestCellX = cellX;
        int closestCellZ = cellZ;

        // Check the 9 neighboring grid sectors to calculate the closest organic cellular nucleus
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int neighborX = cellX + i;
                int neighborZ = cellZ + j;

                // Deterministically generate a random nucleus point inside this grid sector
                long cellSeed = Objects.hash(seed, neighborX, neighborZ);
                Random cellRand = new Random(cellSeed);
                
                double nucleusX = neighborX + cellRand.nextDouble();
                double nucleusZ = neighborZ + cellRand.nextDouble();

                // Check distance from current block position to this nucleus point
                double dx = nucleusX - xOffset;
                double dz = nucleusZ - zOffset;
                double distance = dx * dx + dz * dz;

                if (distance < minDistance) {
                    minDistance = distance;
                    closestCellX = neighborX;
                    closestCellZ = neighborZ;
                }
            }
        }

        // Return a locked, high-entropy signature for the closest organic nucleus
        return Objects.hash(seed, closestCellX, closestCellZ);
    }

    private static int CropTemperature(int plant_id, float temp, long continentHash) {
        Random continentRandom = new Random(continentHash + (plant_id * 31L));
        List<Integer> currentZonePool;

        if (temp < 8.0f) {
            currentZonePool = new ArrayList<>(List.of(1, 3, 4, 5, 6, 8, 20, 23));
        } else if (temp >= 8.0f && temp < 12.5f) {
            currentZonePool = new ArrayList<>(List.of(2, 14, 18, 24, 25, 28));
        } else if (temp >= 12.5f && temp <= 18.0f) {
            currentZonePool = new ArrayList<>(List.of(8, 10, 11, 12, 13, 15, 19, 27));
        } else { // Handles everything > 18.0f
            currentZonePool = new ArrayList<>(List.of(7, 16, 17, 21, 22, 26, 29));
        }

    if (!currentZonePool.contains(plant_id)) {
        return plant_id;
    }

    // 3. Shuffle the current thermal pool down to a unique subset of 3 allowed crops
    Collections.shuffle(currentZonePool, continentRandom);
    List<Integer> allowedSelection = currentZonePool.subList(0, Math.min(3, currentZonePool.size()));

    // 4. If the original plant trying to spawn is already allowed here, return it directly!
    if (allowedSelection.contains(plant_id)) {
        return plant_id;
    }

    // 5. REPLACEMENT FALLBACK: The crop is disallowed. 
    // Pick the first crop out of the continent's allowed selection to swap it with.
    int swappedPlantId = allowedSelection.get(0);

    System.out.println("[CROP SWAPPER] Disallowed Crop ID " + plant_id + " is being replaced with Allowed Crop ID " + swappedPlantId);
        return swappedPlantId;
    }
}
