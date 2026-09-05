package com.terrafirmaagescore.block.custom;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.AABB;
import net.minecraft.client.Minecraft;
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
import net.minecraft.server.level.ServerLevel;

import com.terrafirmaagescore.entity.custom.NeolithicColonistEntity;

import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.io.BufferedWriter;
import java.io.File;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import java.lang.reflect.Proxy;


public class TownCenterBlockEntity extends BlockEntity {
    private String town_name = "";
    private int population = 0;
    private String road1 = "None";
    private String road2 = "None";
    private String road3 = "None";
    private String road4 = "None";
    private String road5 = "None";
    public Boolean named = false;

    public final Path exportDir = (Path) Proxy.newProxyInstance(Path.class.getClassLoader(), new Class<?>[]{Path.class}, (p, m, a) -> m.invoke(ServerLifecycleHooks.getCurrentServer() != null ? ServerLifecycleHooks.getCurrentServer().getWorldPath(new LevelResource("colonies")) : Path.of("colonies_fallback"), a));
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String getWorldFolderName(net.minecraft.world.level.Level level) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            return serverLevel.getServer().getWorldData().getLevelName();
        }
        return "client_active_world";
    }

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
        return this.town_name != null ? this.town_name : "unnamed_town";
    }

    public String getRoad1() { 
        return this.road1; 
    }

    public String getRoad2() { 
        return this.road2; 
    }

    public String getRoad3() { 
        return this.road3; 
    }

    public String getRoad4() { 
        return this.road4; 
    }

    public String getRoad5() { 
        return this.road5; 
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
        this.road1 = getRoad1();
        this.road2 = getRoad2();
        this.road3 = getRoad3();
        this.road4 = getRoad4();
        this.road5 = getRoad5();
        String cleanName = (this.town_name == null || this.town_name.isEmpty()) ? "unnamed_town" : this.town_name;
        this.population = colonistsInColony(this.level, this.worldPosition);

        try {
            TownData data = new TownData(this.town_name, this.population, this.road1, this.road2, this.road3, this.road4, this.road5);
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
        tag.putString("TownName", this.town_name != null ? this.town_name : "unnamed_town");
        tag.putInt("TownPopulation", this.population);
        tag.putString("Road1", this.road1 != null ? this.road1 : "None");
        tag.putString("Road2", this.road2 != null ? this.road2 : "None");
        tag.putString("Road3", this.road3 != null ? this.road3 : "None");
        tag.putString("Road4", this.road4 != null ? this.road4 : "None");
        tag.putString("Road5", this.road5 != null ? this.road5 : "None");
        this.setChanged();
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
        if (tag.contains("Road1")) {
            this.road1 = tag.getString("Road1");
        }
        if (tag.contains("Road2")) {
            this.road2 = tag.getString("Road2");
        }
        if (tag.contains("Road3")) {
            this.road3 = tag.getString("Road3");
        }
        if (tag.contains("Road4")) {
            this.road4 = tag.getString("Road4");
        }
        if (tag.contains("Road5")) {
            this.road5 = tag.getString("Road5");
        }

        try {
            TownData data = this.importDataFromTextFile();
            if (data != null) {
                this.town_name = data.getTownName();
                this.population = data.getPopulation();
                this.road1 = data.getRoad1();
                this.road2 = data.getRoad2();
                this.road3 = data.getRoad3();
                this.road4 = data.getRoad4();
                this.road5 = data.getRoad5();
            
            // Set your naming boolean flag to true so the block registers as named on the server
                this.named = true;
                System.out.println("[TownMod-Server] Automatically loaded town name from file: " + this.town_name);
            }
        } catch (Exception e) {
            System.out.println("[TownMod-Server] Error reading town text file during server loadAdditional processing.");
            e.printStackTrace();
        }
        
        com.terrafirmaagescore.util.ColonyNetworkManager.registerStatue(this.getBlockPos());
    }
}