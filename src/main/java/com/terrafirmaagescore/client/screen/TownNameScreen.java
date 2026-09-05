package com.terrafirmaagescore.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import com.terrafirmaagescore.block.custom.TownCenterBlockEntity;
import com.terrafirmaagescore.block.custom.TownData;

import net.minecraft.core.BlockPos;
import com.terrafirmaagescore.network.UpdateTownNamePayload;
import net.minecraft.world.level.Level;
import java.nio.file.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StringWidget;
import com.mojang.blaze3d.platform.InputConstants;
import com.terrafirmaagescore.network.RequestRoadCheckPayload;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class TownNameScreen extends Screen {
    private EditBox inputField;
    private Button submitButton;
    private Button yesButton;
    private Button noButton;
    private Button RenameButton;
    private Button RoadButton;
    private Button backButton;
    private String loadedTownName;
    private String road;
    private String loadedRoad1 = "loading...";
    private String loadedRoad2 = "loading...";
    private String loadedRoad3 = "loading...";
    private String loadedRoad4 = "loading...";
    private String loadedRoad5 = "loading...";
    private int loadedPopulation;
    public static String ColonyName;
    private StringWidget currentName;
    private StringWidget currentPopulation;
    private StringWidget road1;
    private StringWidget road2;
    private StringWidget road3;
    private StringWidget road4;
    private StringWidget road5;
    public final TownCenterBlockEntity blockEntity;
    private static int messageDelay = 0;
    private static String pendingName = null;
    //private String population;

    public TownNameScreen(TownCenterBlockEntity blockEntity, String townName, int population) {
        super(Component.literal("Set Town Name"));
        this.blockEntity = blockEntity;
        this.loadedTownName = townName;
        this.loadedPopulation = population;
    }

    public void updateRoadStrings(String r1, String r2, String r3, String r4, String r5) {
        this.loadedRoad1 = r1;
        this.loadedRoad2 = r2;
        this.loadedRoad3 = r3;
        this.loadedRoad4 = r4;
        this.loadedRoad5 = r5;

        if (this.road1 != null) this.road1.setMessage(r1.isEmpty() ? Component.empty() : Component.literal("Road 1: " + r1));
        if (this.road2 != null) this.road2.setMessage(r2.isEmpty() ? Component.empty() : Component.literal("Road 2: " + r2));
        if (this.road3 != null) this.road3.setMessage(r3.isEmpty() ? Component.empty() : Component.literal("Road 3: " + r3));
        if (this.road4 != null) this.road4.setMessage(r4.isEmpty() ? Component.empty() : Component.literal("Road 4: " + r4));
        if (this.road5 != null) this.road5.setMessage(r5.isEmpty() ? Component.empty() : Component.literal("Road 5: " + r5));
    }

    @Override
    public void init() {
        super.init();
        if (this.blockEntity != null) {
            if (this.loadedTownName != null && !this.loadedTownName.isEmpty() && !"unnamed_town".equals(this.loadedTownName)) {
                blockEntity.named = true;

                this.currentName = new StringWidget(
                    this.width / 2 - 200,
                    this.height / 2 - 35,
                    200,
                    20,
                    Component.literal("Town Name: " + this.loadedTownName),
                    this.font
                );
                this.addRenderableWidget(currentName);

                this.currentPopulation = new StringWidget(
                    this.width / 2,
                    this.height / 2 - 35,
                    200,
                    20,
                    Component.literal("Town Population: " + this.loadedPopulation),
                    this.font
                );

                this.addRenderableWidget(currentPopulation);
                
                this.RenameButton = Button.builder(
                    Component.literal("rename"),
                    button -> this.rename()
                )
                .bounds(
                    this.width / 2 - 200,
                    this.height / 2,
                    200, 
                    20
                )
                .build();

                this.addRenderableWidget(this.RenameButton);

                this.RoadButton = Button.builder(
                    Component.literal("Roads"),
                    button -> this.road()
                )
                .bounds(
                    this.width / 2 - 0,
                    this.height / 2,
                    200, 
                    20
                )
                .build();

                this.addRenderableWidget(this.RoadButton);
            } else {
                blockEntity.named = false;
                this.inputField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 10, 200, 20, Component.literal("Input"));
                this.inputField.setMaxLength(256);
                this.addRenderableWidget(this.inputField);
                this.setInitialFocus(this.inputField);

                this.submitButton = Button.builder(
                    Component.literal("Submit"),
                    button -> this.submit()
                )
                .bounds(
                    this.width / 2 - 100,
                    this.height / 2 + 20,
                    200,
                    20
                )
                .build();
                this.addRenderableWidget(this.submitButton);
            }
        }
    }

    public void road() {
        try {
            if (net.minecraft.client.Minecraft.getInstance().getConnection() != null) {
                net.minecraft.client.Minecraft.getInstance().getConnection().send(
                    new RequestRoadCheckPayload(this.blockEntity.getBlockPos())
                );
                System.out.println("[TownMod-Client] Successfully fired RequestRoadCheckPayload to server!");
            } else {
                System.out.println("[TownMod-Client] Error: Client connection pipeline is null!");
            }

            this.clearWidgets();

            this.road1 = new StringWidget(
                this.width / 2,
                this.height / 2 - 40,
                200,
                20,
                Component.literal("Road 1: " + this.loadedRoad1),
                this.font
            );

            this.addRenderableWidget(road1);

            this.road2 = new StringWidget(
                this.width / 2,
                this.height / 2 - 20,
                200,
                20,
                Component.literal("Road 2: " + this.loadedRoad2),
                this.font
            );

            this.addRenderableWidget(road2);

            this.road3 = new StringWidget(
                this.width / 2,
                this.height / 2 - 0,
                200,
                20,
                Component.literal("Road 3: " + this.loadedRoad3),
                this.font
            );

            this.addRenderableWidget(road3);

            this.road4 = new StringWidget(
                this.width / 2,
                this.height / 2 + 20,
                200,
                20,
                Component.literal("Road 4: " + this.loadedRoad4),
                this.font
            );

            this.addRenderableWidget(road4);

            this.road5 = new StringWidget(
                this.width / 2,
                this.height / 2 + 40,
                200,
                20,
                Component.literal("Road 5: " + this.loadedRoad5),
                this.font
            );

            this.addRenderableWidget(road5);

            this.backButton = Button.builder(
                Component.literal("back"),
                button -> this.back()
            )
            .bounds(
                this.width / 2 - 200,
                this.height / 2,
                200, 
                20
            )
            .build();

            this.addRenderableWidget(this.backButton);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void back() {
        try {
            this.clearWidgets();

            this.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void rename() {
        try {
            super.init();
            this.removeWidget(this.RoadButton);
            this.removeWidget(this.RenameButton);
            this.removeWidget(this.currentName);
            this.removeWidget(this.currentPopulation);

            this.yesButton = Button.builder(
                Component.literal("Yes, Rename"),
                button -> this.yes()
            )
            .bounds(
                this.width / 2 - 200,
                this.height / 2 + 20,
                200,
                20
            )
            .build();

            this.noButton = Button.builder(
                Component.literal("Cancel Rename"),
                button -> this.onClose()
            )
            .bounds(
                this.width / 2 - 200,
                this.height / 2 - 20,
                200,
                20
            )
            .build();

            this.addRenderableWidget(this.yesButton);
            this.addRenderableWidget(this.noButton);
                
            Minecraft client = minecraft.getInstance();
            if (client.player != null) {
                //client.player.sendSystemMessage(Component.literal("Town renamed to " + minifiedJson));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void yes() {
        try {
            this.removeWidget(this.yesButton);
            this.removeWidget(this.noButton);

            this.inputField = new EditBox(this.font, this.width / 2 - 100, this.height / 2 - 10, 200, 20, Component.literal("Input"));
            this.inputField.setMaxLength(256);

            this.addRenderableWidget(this.inputField);
            this.setInitialFocus(this.inputField);

            this.submitButton = Button.builder(
                Component.literal("Submit"),
                button -> this.submit()
            )
            .bounds(
                this.width / 2 - 100,
                this.height / 2 + 20,
                200,
                20
            )
            .build();

            this.addRenderableWidget(this.submitButton);
            // this.currentName = new StringWidget(
            //     this.width / 2 - 100,
            //     this.height / 2 - 35,
            //     200,
            //     20,
            //     Component.literal("Current Town Name: " + townName),
            //     this.font
            // );

            //this.addRenderableWidget(this.currentName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submit() {
        String ColonyName = this.inputField.getValue();
        Level level = this.blockEntity.getLevel();
        BlockPos pos = this.blockEntity.getBlockPos();

        if (level != null && level.getBlockEntity(pos) instanceof TownCenterBlockEntity blockEntity) {

            PacketDistributor.sendToServer(
                new UpdateTownNamePayload(
                    this.blockEntity.getBlockPos(),
                    ColonyName
                )
            );

            pendingName = ColonyName;
            messageDelay = 20; // 20 ticks = 1 second

            this.onClose();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 55, 0xFFFFFF);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (messageDelay > 0) {
            messageDelay--;

            if (messageDelay == 0 && pendingName != null) {
                Minecraft client = Minecraft.getInstance();

                if (client.player != null) {
                    client.player.sendSystemMessage(
                        Component.literal("Town renamed to " + pendingName)
                    );
                }

                pendingName = null;
            }
        }
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_RETURN ||
            keyCode == InputConstants.KEY_NUMPADENTER) {

            if (this.inputField != null) {
                this.submit();
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}