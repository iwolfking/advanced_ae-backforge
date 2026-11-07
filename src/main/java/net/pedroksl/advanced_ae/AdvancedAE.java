package net.pedroksl.advanced_ae;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ScreenEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.pedroksl.advanced_ae.common.definitions.*;
import net.pedroksl.advanced_ae.common.items.armors.IGridLinkedItem;
import net.pedroksl.advanced_ae.common.patterns.AdvPatternDetailsDecoder;
import net.pedroksl.advanced_ae.events.AAELivingEntityEvents;
import net.pedroksl.advanced_ae.events.AAEPlayerEvents;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.recipes.InitRecipeSerializers;
import net.pedroksl.advanced_ae.recipes.InitRecipeTypes;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.features.GridLinkables;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;

public class AdvancedAE {
    public static final String MOD_ID = "advanced_ae";

    static AdvancedAE INSTANCE;

    public AdvancedAE() {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
        INSTANCE = this;

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        AAEConfig.register(MOD_ID);

        AAEItems.INSTANCE.register(eventBus);
        AAEBlocks.INSTANCE.register(eventBus);
        AAEBlockEntities.INSTANCE.register(eventBus);
        AAEFluids.INSTANCE.register(eventBus);
        AAEMenus.INSTANCE.register(eventBus);

        eventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        eventBus.addListener(AdvancedAE::initUpgrades);
        eventBus.addGenericListener(RecipeType.class, (RegistryEvent.Register event) -> {
            InitRecipeTypes.init(event.getRegistry());
        });
        eventBus.addGenericListener(RecipeSerializer.class, (RegistryEvent.Register<RecipeSerializer<?>> event) -> {
            InitRecipeSerializers.init(event.getRegistry());
        });

        AAEHotkeysRegistry.INSTANCE.init();
        AAENbt.init();
    }

    public static AdvancedAE instance() {
        return INSTANCE;
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        AAENetworkHandler.INSTANCE.init();
        PatternDetailsHelper.registerDecoder(AdvPatternDetailsDecoder.INSTANCE);
        initGridLinkables();
        AAEPlayerEvents.init();
    }

    public static void initGridLinkables() {


    }

    private static void initUpgrades(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Upgrades.add(AEItems.SPEED_CARD, AAEBlocks.QUANTUM_CRAFTER, 4);
            Upgrades.add(AEItems.REDSTONE_CARD, AAEBlocks.QUANTUM_CRAFTER, 1);
            Upgrades.add(AEItems.SPEED_CARD, AAEItems.STOCK_EXPORT_BUS.get(), 4);
            Upgrades.add(AEItems.CAPACITY_CARD, AAEItems.STOCK_EXPORT_BUS.get(), 4);
            Upgrades.add(AEItems.REDSTONE_CARD, AAEItems.STOCK_EXPORT_BUS.get(), 1);
            Upgrades.add(AEItems.SPEED_CARD, AAEItems.IMPORT_EXPORT_BUS.get(), 4);
            Upgrades.add(AEItems.CAPACITY_CARD, AAEItems.IMPORT_EXPORT_BUS.get(), 4);
            Upgrades.add(AEItems.REDSTONE_CARD, AAEItems.IMPORT_EXPORT_BUS.get(), 1);
            Upgrades.add(AEItems.SPEED_CARD, AAEItems.ADVANCED_IO_BUS.get(), 4);
            Upgrades.add(AEItems.CAPACITY_CARD, AAEItems.ADVANCED_IO_BUS.get(), 4);
            Upgrades.add(AEItems.REDSTONE_CARD, AAEItems.ADVANCED_IO_BUS.get(), 1);
        });
    }

    public static ResourceLocation makeId(String id) {
        return new ResourceLocation(MOD_ID, id);
    }


    @SubscribeEvent
    public void onAdvancedAEScreenOpened(ScreenEvent.InitScreenEvent.Post event) {
        Object screen = event.getScreen();
        if (screen == null) return;
        try {
            Class<?> aeBase = Class.forName("appeng.client.gui.AEBaseScreen");
            if (!aeBase.isInstance(screen)) return;
            Field toolbarField = aeBase.getDeclaredField("verticalToolbar");
            toolbarField.setAccessible(true);
            Object toolbar = toolbarField.get(screen);
            if (toolbar == null) return;
            Field posField = toolbar.getClass().getDeclaredField("position");
            posField.setAccessible(true);
            if (posField.get(toolbar) != null) return;
            Field styleField = aeBase.getDeclaredField("style");
            styleField.setAccessible(true);
            Object style = styleField.get(screen);
            if (style == null) return;
            Method getBounds = aeBase.getDeclaredMethod("getBounds", boolean.class);
            getBounds.setAccessible(true);
            Object bounds = getBounds.invoke(screen, Boolean.TRUE);
            Method getWidget = style.getClass().getMethod("getWidget", String.class);
            Object widgetStyle = getWidget.invoke(style, "verticalToolbar");
            if (widgetStyle == null) return;
            Method resolve = widgetStyle.getClass().getMethod("resolve", bounds.getClass());
            Object point = resolve.invoke(widgetStyle, bounds);
            if (point != null) posField.set(toolbar, point);
        } catch (Throwable ignored) {
        }
    }

    @SubscribeEvent
    public void onRightClickWirelessAccessPoint(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isClientSide()) return;
        net.minecraft.world.entity.player.Player player = event.getPlayer();
        net.minecraft.core.BlockPos pos = event.getPos();
        net.minecraft.world.level.block.entity.BlockEntity be = event.getWorld().getBlockEntity(pos);
        if (!(be instanceof appeng.api.implementations.blockentities.IWirelessAccessPoint)) return;
        for (var stack : player.getInventory().items) {
            if (stack != null && stack.getItem() instanceof net.pedroksl.advanced_ae.common.items.armors.IGridLinkedItem) {
                net.pedroksl.advanced_ae.common.items.armors.IGridLinkedItem.LINKABLE_HANDLER.link(stack, pos.asLong());
            }
        }
        for (var stack : player.getInventory().armor) {
            if (stack != null && stack.getItem() instanceof net.pedroksl.advanced_ae.common.items.armors.IGridLinkedItem i) {
                net.pedroksl.advanced_ae.common.items.armors.IGridLinkedItem.LINKABLE_HANDLER.link(stack, pos.asLong());
            }
        }
    }
}
