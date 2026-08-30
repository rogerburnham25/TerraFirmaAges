package com.terrafirmaagescore.block.custom;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import java.util.List;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.io.BufferedReader;
import java.io.FileReader;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

import com.terrafirmaagescore.entity.custom.NeolithicColonistEntity;

import net.neoforged.fml.loading.FMLPaths;
import java.io.BufferedWriter;
import java.io.File;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class TownCenterBlockEntity extends BlockEntity {
    private String town_name = "";
    private int population;
    public Boolean named = false;

    public Path exportDir = FMLPaths.GAMEDIR.get().resolve("colonies");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // private File Colony;

    public TownCenterBlockEntity(BlockPos pos, BlockState state) {
        super(com.terrafirmaagescore.block.entity.ModBlockEntities.COLONY.get(), pos, state);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putString("TownName", this.town_name);
        tag.putInt("TownPopulation", this.population);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(net.minecraft.network.Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        CompoundTag tag = packet.getTag();
        if (tag != null) {
            this.loadAdditional(tag, registries);
        }
    }

    public void setTownName(String town_name) {
        this.town_name = town_name;
    }

    // 3. Getter method to retrieve the name dynamically
    public String getTownName() { 
        return this.town_name; 
    }

    // 4. Setter for population tracking
    public void setPopulation(int population) {
        this.population = population;
    }

    public static int colonistsInColony(Level level, BlockPos newPos) {
        int COLONY_RADIUS = 100;
        double squaredRadius = (double) COLONY_RADIUS * COLONY_RADIUS;

        AABB searchBox = new AABB(newPos).inflate(COLONY_RADIUS, 10, COLONY_RADIUS);
        List<? extends NeolithicColonistEntity> colonists = level.getEntitiesOfClass(
            NeolithicColonistEntity.class,
                searchBox,
                colonist -> true
            );

            int population = 0;

            for (NeolithicColonistEntity colonist : colonists) {
                double distanceSquared = colonist.distanceToSqr(newPos.getX(), newPos.getY(), newPos.getZ());
                if (distanceSquared <= squaredRadius) {
                    population++;
                }
            }
        return population;
    } 
    // public String updatePopulationAndSave() {
    //     this.population = count;
    //     this.setChanged();
    //     this.exportDataToTextFile(); 
    //     return this.town_name;
    // }
    

    public void exportDataToTextFile() {
        
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        
        this.town_name = getTownName();
        String cleanName = (this.town_name == null || this.town_name.isEmpty()) ? "unnamed_town" : this.town_name;
        this.population = colonistsInColony(this.level, this.worldPosition);

        try {
            TownData data = new TownData(this.town_name, this.population);
            String safeFileName = cleanName.replaceAll("[^a-zA-Z0-9_\\-]", "");
            File file = exportDir.resolve(safeFileName + ".json").toFile();

            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                GSON.toJson(data, writer);
                System.out.println("Successfully saved town name to file!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save town name to file!");
        }
    }

    public TownData importDataFromTextFile() {
        this.town_name = getTownName();
        if (this.town_name == null || this.town_name.isEmpty() || "unnamed_town".equals(this.town_name)) {
            System.out.println("Town is currently unnamed. Skipping file import.");
            return null;
        }

        String safeFileName = this.town_name.replaceAll("[^a-zA-Z0-9_\\-]", "");
        File activeFile = exportDir.resolve(safeFileName + ".json").toFile();

        if (!activeFile.exists()) {
            System.out.println("Town data does not exist for: " + safeFileName);
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(activeFile))) {
            TownData data = GSON.fromJson(reader, TownData.class);
            System.out.println("Read file successfully");
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File read failed");
            return null;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("TownName", this.town_name);
        tag.putInt("TownPopulation", this.population);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("TownName")) {
            this.town_name = tag.getString("TownName");
        }
        if (tag.contains("TownPopulation")) {
            this.population = tag.getInt("TownPopulation");
        }
    }
}