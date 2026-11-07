package net.pedroksl.ae2addonlib.client.widgets;

import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import net.pedroksl.ae2addonlib.network.LibNetworkHandler;
import net.pedroksl.ae2addonlib.network.serverPacket.FluidTankItemUsePacket;
import net.pedroksl.ae2addonlib.util.Colors;

import appeng.api.stacks.AmountFormat;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.AEBaseScreen;
import appeng.core.localization.Tooltips;

/**
 * A fluid tank slot. This slot must be constructed in the screen class, which must implement the {@link net.pedroksl.ae2addonlib.api.IFluidTankScreen} interface.
 * That screen needs to be linked to a menu that implements the {@link net.pedroksl.ae2addonlib.api.IFluidTankHandler} interface.
 * The two interfaces provide functionality to handle item use and playing sounds.
 */
public class FluidTankSlot extends AbstractWidget {

    private final AbstractContainerScreen<?> screen;
    private TextureAtlasSprite fluidTexture;

    @Override
    public void updateNarration(NarrationElementOutput output) {}
    private FluidStack content = FluidStack.EMPTY;
    private final int maxLevel;
    private boolean disableRender = false;
    /**
     * The tank's index.
     */
    public final int index;

    /**
     * Constructs a fluid tank slot with initial values.
     * @param screen The screen the tank is attached to.
     * @param index The tank's index.
     * @param x The left-most coordinate of the tank.
     * @param y The top-most coordinate of the tank.
     * @param width The width of the tank.
     * @param height The height of the tank.
     * @param maxLevel The max level of the tank (in buckets).
     */
    public FluidTankSlot(
            AbstractContainerScreen<?> screen, int index, int x, int y, int width, int height, int maxLevel) {
        super(x, y, width, height, new TextComponent(""));
        this.maxLevel = maxLevel;
        this.screen = screen;
        this.index = index;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                boolean flag = this.clicked(mouseX, mouseY);
                if (flag) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());
                    handleClick(mouseX, mouseY, button);
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    private void handleClick(double mouseX, double mouseY, int button) {
        var stack = screen.getMenu().getCarried();
        if (!stack.isEmpty()) {
            var forgeCap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
            forgeCap.ifPresent(cap -> {
                FluidStack fluidStack = cap.getFluidInTank(0);
                if (fluidStack.getFluid() == this.content.getFluid()
                        || fluidStack.isEmpty()
                        || this.content.isEmpty()) {
                    var actualButton = screen instanceof AEBaseScreen<?> baseScreen
                            ? (baseScreen.isHandlingRightClick() ? 1 : 0)
                            : button;
                    LibNetworkHandler.INSTANCE.sendToServer(new FluidTankItemUsePacket(this.index, actualButton));
                }
            });
        }
    }

    @Override
    public void playDownSound(@NotNull SoundManager handler) {}

    /**
     * Plays sounds depending on if the fluid was inserted or extracted.
     * @param isInsert If the fluid was inserted or extracted.
     */
    public static void playDownSound(boolean isInsert) {
        var handler = Minecraft.getInstance().getSoundManager();
        if (isInsert) {
            handler.play(SimpleSoundInstance.forUI(SoundEvents.BUCKET_EMPTY, 1.0F, 1.0F));
        } else {
            handler.play(SimpleSoundInstance.forUI(SoundEvents.BUCKET_FILL, 1.0F, 1.0F));
        }
    }

    @Override
    public boolean isValidClickButton(int button) {
        return button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
    }

    @Override
    public void render(@NotNull PoseStack guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // rendering not yet ported to 1.18.2
    }

    protected void updateWidgetNarration(@NotNull NarrationElementOutput narration) {}

    /**
     * Updates the displayed {@link FluidStack}.
     * @param fluidStack The new fluid stack.
     */
    public void setFluidStack(FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            this.content = FluidStack.EMPTY;
            this.disableRender = true;
            updateTooltip(fluidStack);
            return;
        }

        this.disableRender = false;
        boolean updateTexture = this.content.isEmpty() || fluidStack.getFluid() != this.content.getFluid();
        this.content = fluidStack;

        updateTooltip(fluidStack);

        if (updateTexture && !this.content.isEmpty()) {
            this.fluidTexture = null;
        }
    }

    private void updateTooltip(FluidStack stack) {
        // setTooltip(...)
    }

    @SuppressWarnings("deprecation")
    private static String getModDisplayNameFromId(String modId) {
        var container = ModList.get().getModContainerById(modId);

        if (modId.equals("c")) {
            return "Common";
        } else if ((container = ModList.get().getModContainerById(modId)).isPresent()) {
            return container.get().getModInfo().getDisplayName();
        } else {
            container = ModList.get().getModContainerById(modId.replace('_', '-'));
            return container.isPresent()
                    ? container.get().getModInfo().getDisplayName()
                    : WordUtils.capitalizeFully(modId.replace('_', ' '));
        }
    }
}
