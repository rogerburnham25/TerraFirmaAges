package com.terrafirmaagescore.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import com.terrafirmaagescore.TerraFirmaAgesCore;
import com.terrafirmaagescore.block.custom.TownCenterBlockEntity;
import com.terrafirmaagescore.block.custom.Town_Center_Statue;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.terrafirmaagescore.network.UpdateTownNamePayload;
import net.minecraft.world.level.Level;
import java.io.File;
import java.nio.file.Files;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StringWidget;
import com.mojang.blaze3d.platform.InputConstants;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class TownNameScreen extends Screen {
    private EditBox inputField;
    private Button submitButton;
    private Button yesButton;
    private Button noButton;
    public static String ColonyName;
    private StringWidget currentName;
    public final TownCenterBlockEntity blockEntity;
    private static int messageDelay = 0;
    private static String pendingName = null;

    public TownNameScreen(TownCenterBlockEntity blockEntity) {
        super(Component.literal("Set Town Name"));
        this.blockEntity = blockEntity;
    }

    @Override
    public void init() {
        System.out.println(blockEntity.named == true);
        if (blockEntity.named == false) {
            super.init();

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
            
            blockEntity.named = true;
            System.out.println(blockEntity.named == true);
        } else if (blockEntity.named == true) {
            try {
                super.init();

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

            String content = Files.readString(blockEntity.file.toPath());
            String minifiedJson = content.replaceAll("\\s", " ").trim();

            this.currentName = new StringWidget(
                this.width / 2 - 100,
                this.height / 2 - 35,
                200,
                20,
                Component.literal("Current Town Name: " + minifiedJson),
                this.font
            );

            this.addRenderableWidget(this.currentName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submit() {
        String ColonyName = this.inputField.getValue();
        Level level = this.blockEntity.getLevel();
        BlockPos pos = this.blockEntity.getBlockPos();
        String count = "0";

        if (level != null && level.getBlockEntity(pos) instanceof TownCenterBlockEntity blockEntity) {
            count = String.valueOf(
                Town_Center_Statue.colonistsInColony(level, pos)
            );

            PacketDistributor.sendToServer(
                new UpdateTownNamePayload(
                    this.blockEntity.getBlockPos(),
                    ColonyName,
                    count
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
