package com.terrafirmaagescore;

import org.slf4j.Logger;

import com.terrafirmaagescore.entity.ModEntities;
import com.terrafirmaagescore.entity.client.NeolithicColonistRenderer;
import com.mojang.logging.LogUtils;

import com.terrafirmaagescore.tags.block.ModBlockTagProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import com.terrafirmaagescore.worldgen.placement.PlacementModifiers;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import com.terrafirmaagescore.block.entity.ModBlockEntities;
//import com.terrafirmaagescore.block.custom.farm_block;

import com.terrafirmaagescore.block.custom.Farm_Block;
import com.terrafirmaagescore.block.custom.Town_Center_Statue;
// FIXED: Added the critical missing import for DeferredRegister
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import com.terrafirmaagescore.network.UpdateTownNamePayload;
import com.terrafirmaagescore.network.ServerPacketHandler;
//import com.terrafirmaagescore.network.UpdatePopulationPayload;

import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
//import net.neoforged.neoforge.registries.DeferredBlockEntities;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.minecraft.client.renderer.entity.EntityRenderers;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod("terrafirmaagescore")
public class TerraFirmaAgesCore {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "terrafirmaagescore";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    //public static final DeferredRegister.BlockEntity BLOCK_ENTITIES = DeferredRegister.createBlockEntities(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    



    public TerraFirmaAgesCore(IEventBus modEventBus, ModContainer modContainer) {
        Farm_Block.BLOCKS.register(modEventBus);
        Farm_Block.ITEMS.register(modEventBus);
        Town_Center_Statue.BLOCKS.register(modEventBus);
        Town_Center_Statue.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        PlacementModifiers.PLACEMENT_MODIFIERS.register(modEventBus);
        // FORCE CLASSLOADING CHECK: Tells Java to read your block arrays immediately!
        try {Class.forName(com.terrafirmaagescore.block.custom.Farm_Block.class.getName()); } catch (Exception e) {}
        try {Class.forName(com.terrafirmaagescore.block.custom.Town_Center_Statue.class.getName()); } catch (Exception e) {}

        modEventBus.addListener(this::registerPackets);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::commonSetup);

        // Standard registry system registration
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // com.terrafirmaagescore.block.custom.Farm_Block.register(modEventBus);
        ModEntities.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("terrafirmaagescore");

        registrar.playToServer(
            UpdateTownNamePayload.TYPE,
            UpdateTownNamePayload.STREAM_CODEC,
            ServerPacketHandler::handleTownNameUpdate
        );
    }

    private void gatherData(net.neoforged.neoforge.data.event.GatherDataEvent event) {
        net.minecraft.data.DataGenerator generator = event.getGenerator();
        net.minecraft.data.PackOutput packOutput = generator.getPackOutput();
        net.neoforged.neoforge.common.data.ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        // Registers our blockstate / item model writer to execute on next runData process
        generator.addProvider(event.includeClient(), new com.terrafirmaagescore.datagen.ModBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(),
            new ModBlockTagProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }
    
    
    // private void addCreative(BuildCreativeModeTabContentsEvent event) {
    //     if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
    //         event.accept(FARM_BLOCK);
    //         event.accept(TOWN_CENTER_STATUE);
    //     }
    // }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @EventBusSubscriber(
        modid = TerraFirmaAgesCore.MODID,
        value = Dist.CLIENT
    )
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.NEOLITHIC_COLONIST.get(), NeolithicColonistRenderer::new);
        }
    }

    //public static final DeferredItem<BlockItem> FARM_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("farm_block", Farm_Block);
    
    //public class TerraFirmaAgesCoreCreativeTab {
        //public static final supplier<CreativeModeTab> TFCA_TAB.register(name: "TerraFirmaAges",
            //() -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.GOLD_COIN))
                //.displayItems((itemDisplayParameters, output) -> {
                    //output.accept(ModBlocks.FARM_BLOCK.get());

                //}).build());

                //public static void register(IEventBus eventBus) {CREATIVE_MODE_TAB.register(eventBus); }
    //}
}