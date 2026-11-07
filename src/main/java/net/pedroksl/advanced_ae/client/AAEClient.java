package net.pedroksl.advanced_ae.client;

import net.pedroksl.advanced_ae.client.renderer.QuantumArmorRenderer;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.events.AAEClientPlayerEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.client.gui.*;
import net.pedroksl.advanced_ae.client.renderer.ReactionChamberTESR;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.pedroksl.advanced_ae.common.definitions.AAEBlockEntities;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;
import net.pedroksl.advanced_ae.common.definitions.AAECreativeTab;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.gui.QuantumCrafterTermMenu;
import net.pedroksl.advanced_ae.gui.StockExportBusMenu;
import net.pedroksl.ae2addonlib.registry.helpers.LibTags;
import net.pedroksl.ae2addonlib.util.Colors;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.client.render.crafting.CraftingCubeModel;
import appeng.init.client.InitScreens;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class AAEClient extends AdvancedAE {
    private static final Logger LOGGER = LoggerFactory.getLogger(AAEClient.class);

    private static AAEClient INSTANCE;

    public AAEClient() {
        super();

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();


        eventBus.addListener(AAEClient::initItemBlockRenderTypes);
        eventBus.addListener(AAEClient::initRenderers);
        eventBus.addListener(AAEClient::initRenderers);
        eventBus.addListener(AAEClient::initArmorRenderers);

        INSTANCE = this;

        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) -> {
            if (e.phase == TickEvent.Phase.END) {
                AAEHotkeys.INSTANCE.checkHotkeys();
            }
        });

        AAEClientPlayerEvents.init();

        eventBus.addListener(this::clientSetup);
        registerKeyMappings();
    }

    private static void registerKeyMappings() {
        net.pedroksl.advanced_ae.client.AAEHotkeys.INSTANCE.finalizeRegistration(net.minecraftforge.client.ClientRegistry::registerKeyBinding);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            try {
                if (AAECreativeTab.INSTANCE != null) {}

                try {
                    ModelLoaderRegistry.registerLoader(
                            net.pedroksl.advanced_ae.AdvancedAE.makeId("block/crafting/quantum_structure_formed"),
                            new appeng.client.render.SimpleModelLoader(() ->
                                    new appeng.client.render.crafting.CraftingCubeModel(
                                            new net.pedroksl.advanced_ae.client.renderer.AAECraftingUnitModelProvider(
                                                    net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType.STRUCTURE))));
                } catch (Throwable loaderErr) {
                    LOGGER.warn("AAE failed to register quantum formed model loader", loaderErr);
                }
                initScreens();
                InitScreens.init();
            } catch (Throwable e) {
                LOGGER.error("AAE failed postClientSetup", e);
                throw new RuntimeException(e);
            }
        });
    }

    private static void initScreens() {
        InitScreens.register(
                AAEMenus.QUANTUM_COMPUTER.get(), QuantumComputerScreen::new, "/screens/quantum_computer.json");
        InitScreens.register(
                AAEMenus.ADV_PATTERN_PROVIDER.get(),
                AdvPatternProviderScreen::new,
                "/screens/adv_pattern_provider.json");
        InitScreens.register(
                AAEMenus.SMALL_ADV_PATTERN_PROVIDER.get(),
                SmallAdvPatternProviderScreen::new,
                "/screens/small_adv_pattern_provider.json");
        InitScreens.register(
                AAEMenus.ADV_PATTERN_ENCODER.get(), AdvPatternEncoderScreen::new, "/screens/adv_pattern_encoder.json");
        InitScreens.register(
                AAEMenus.QUANTUM_CRAFTER.get(), QuantumCrafterScreen::new, "/screens/quantum_crafter.json");
        InitScreens.register(
                AAEMenus.REACTION_CHAMBER.get(), ReactionChamberScreen::new, "/screens/reaction_chamber.json");
        InitScreens.register(
                AAEMenus.CRAFTER_PATTERN_CONFIG.get(),
                QuantumCrafterConfigPatternScreen::new,
                "/screens/quantum_crafter_pattern_config.json");
        // QCT screen disabled for 1.18 backport due missing terminal style.
        // InitScreens.<QuantumCrafterTermMenu, QuantumCrafterTermScreen<QuantumCrafterTermMenu>>register(
        //         AAEMenus.QUANTUM_CRAFTER_TERMINAL.get(),
        //         QuantumCrafterTermScreen::new,
        //         "/screens/quantum_crafter_terminal.json");
        InitScreens.register(
                AAEMenus.PORTABLE_WORKBENCH.get(), PortableWorkbenchScreen::new, "/screens/portable_workbench.json");

        InitScreens.<StockExportBusMenu, StockExportBusScreen<StockExportBusMenu>>register(
                AAEMenus.STOCK_EXPORT_BUS.get(), StockExportBusScreen::new, "/screens/stock_export_bus.json");
        InitScreens.register(
                AAEMenus.IMPORT_EXPORT_BUS.get(), ImportExportBusScreen::new, "/screens/import_export_bus.json");
        InitScreens.register(
                AAEMenus.ADVANCED_IO_BUS.get(), AdvancedIOBusScreen::new, "/screens/advanced_io_bus.json");
        InitScreens.register(
                AAEMenus.QUANTUM_ARMOR_CONFIG.get(), QuantumArmorConfigScreen::new, "/screens/quantum_armor_config.json");
        InitScreens.register(
                AAEMenus.QUANTUM_ARMOR_FILTER_CONFIG.get(), QuantumArmorFilterConfigScreen::new, "/screens/quantum_armor_filter_config.json");
        InitScreens.register(
                AAEMenus.QUANTUM_ARMOR_MAGNET.get(), QuantumArmorMagnetScreen::new, "/screens/quantum_armor_magnet.json");
        InitScreens.register(
                AAEMenus.QUANTUM_ARMOR_NUM_INPUT.get(), QuantumArmorNumInputConfigScreen::new, "/screens/quantum_armor_num_input_config.json");
        InitScreens.register(
                AAEMenus.QUANTUM_ARMOR_STYLE_CONFIG.get(), QuantumArmorStyleConfigScreen::new, "/screens/quantum_armor_style.json");
    }

    @SuppressWarnings("deprecation")
    private static void initItemBlockRenderTypes(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(AAEFluids.QUANTUM_INFUSION.source(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(AAEFluids.QUANTUM_INFUSION.flowing(), RenderType.translucent());
        });
    }

    @SuppressWarnings("deprecation")
    private static void initItemColours() {
    }

    private static void initArmorRenderers(EntityRenderersEvent.AddLayers event) {
        GeoArmorRenderer.registerArmorRenderer(
                QuantumArmorBase.class,
                QuantumArmorRenderer::new);
    }

    private static void initRenderers(EntityRenderersEvent.RegisterRenderers event) {

    }

    private static ItemColor makeOpaque(ItemColor itemColor) {
        return (stack, tintIndex) -> itemColor.getColor(stack, tintIndex) | 0xFF000000;
    }

    public static AAEClient instance() {
        return INSTANCE;
    }
}
