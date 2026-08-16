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

public class TownNameScreen extends Screen {
    private EditBox inputField;
    private Button submitButton;
    public Boolean named;
    public static String ColonyName;
    public final TownCenterBlockEntity blockEntity;
    public File townFile = new TownCenterBlockEntity(file);

    public TownNameScreen(TownCenterBlockEntity blockEntity) {
        super(Component.literal("Set Town Name"));
        this.blockEntity = blockEntity;
    }

    @Override
    protected void init() {
        if (!named) {
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
            
            Boolean named = true;
            this.addRenderableWidget(this.submitButton);
        } else if (named) {
            try {
                String content = File.readString(file.toPath());
                String minifiedJson = content.relpaceAll("\\s", " ").trim();

                MinecraftClient client = MinecraftClient.getInstance();
                if (client.player != null) {
                    client.player.sendChatMessage(minifiedJson, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void submit() {
        String ColonyName = this.inputField.getValue();
        Level level = this.blockEntity.getLevel();
        BlockPos pos = this.blockEntity.getBlockPos();
        String count = "0";
        if (level != null && level.getBlockEntity(pos) instanceof TownCenterBlockEntity blockEntity) {
            count = String.valueOf(Town_Center_Statue.colonistsInColony(level, pos));
            PacketDistributor.sendToServer(new UpdateTownNamePayload(this.blockEntity.getBlockPos(), ColonyName, count));
            // PacketDistributor.sendToServer(new UpdatePopulationPayload(this.blockEntity.getBlockPos()));
            this.onClose();
        };
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 55, 0xFFFFFF);
    }
}
