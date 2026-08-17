package com.terrafirmaagescore.block.custom;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import com.terrafirmaagescore.block.entity.ModBlockEntities;
import com.terrafirmaagescore.block.custom.Farm_Block;
import com.terrafirmaagescore.TerraFirmaAgesCore;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import com.terrafirmaagescore.client.screen.TownNameScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.neoforged.fml.loading.FMLPaths;
import java.io.BufferedWriter;
import java.io.File;

import java.util.*;

public class TownCenterBlockEntity extends BlockEntity {
    private String town_name = "";
    private String population = "";
    public String cleanName = (this.town_name == null || this.town_name.isEmpty()) ? "unnamed_town" : this.town_name;
    public Path exportDir = FMLPaths.GAMEDIR.get().resolve("colonies");
    public File file = exportDir.resolve(cleanName + ".json").toFile();
    public Boolean named = false;
    // private File Colony;

    public TownCenterBlockEntity(BlockPos pos, BlockState state) {
        super(com.terrafirmaagescore.block.entity.ModBlockEntities.COLONY.get(), pos, state);
    }

    public String getTownName() {
        return this.town_name;
    }

    public String updateTownNameAndSave(String ColonyName, String count) {
        this.town_name = ColonyName;
        this.population = count;
        this.setChanged();
        this.exportDataToTextFile(); 
        return this.town_name;
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

        if (cleanName == "unnamed_town") {
            try {
                String jsonPayload = "{\n" + "\"town_name\": \"" + this.town_name + "\",\n \"population\": " + this.population + "\n}";

                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(jsonPayload);
                System.out.println("Successfully saved town name to file!");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to save town name to file!");
            }
        } else if (cleanName != "unnamed_town") {
            try {
                String content = Files.readString(file.toPath());
                String minifiedJson = content.replaceAll("\\s", " ").trim();

                Minecraft client = Minecraft.getInstance();
                if (client.player != null) {
                    //client.player.sendMessage(minifiedJson);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // file.getParentFile().mkdirs();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("town_name", this.town_name != null ? this.town_name : "");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.town_name = tag.getString("town_name");
    }
}