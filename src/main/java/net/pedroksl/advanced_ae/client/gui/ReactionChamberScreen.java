package net.pedroksl.advanced_ae.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.gui.ReactionChamberMenu;
import net.pedroksl.ae2addonlib.api.IFluidTankScreen;
import net.pedroksl.ae2addonlib.client.widgets.AddonActionButton;
import net.pedroksl.ae2addonlib.client.widgets.AddonActionItems;
import net.pedroksl.ae2addonlib.client.widgets.FluidTankSlot;
import net.pedroksl.ae2addonlib.client.widgets.ToolbarActionButton;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.Blitter;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ProgressBar;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.localization.Tooltips;

public class ReactionChamberScreen extends UpgradeableScreen<ReactionChamberMenu> implements IFluidTankScreen {

    private static final int INPUT_TANK_X = 9;
    private static final int OUTPUT_TANK_X = 151;
    private static final int TANKS_Y = 21;
    private static final int TANKS_WIDTH = 16;
    private static final int TANKS_HEIGHT = 58;

    private final ProgressBar pb;
    private final SettingToggleButton<YesNo> autoExportBtn;
    private final ToolbarActionButton outputConfigure;
    private final AlertWidget powerAlert;

    private FluidTankSlot inputSlot;
    private FluidTankSlot outputSlot;

    public ReactionChamberScreen(
            ReactionChamberMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.pb = new ProgressBar(this.menu, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("progressBar", this.pb);

        this.autoExportBtn = null;

        this.outputConfigure =
                new ToolbarActionButton(AddonActionItems.DIRECTIONAL_OUTPUT, btn -> menu.configureOutput());
        if (this.outputConfigure != null) this.outputConfigure.setVisibility(true);
        this.addToLeftToolbar(this.outputConfigure);

        AddonActionButton clearBtn = new AddonActionButton(AddonActionItems.FLUID_FLUSH, btn -> menu.clearFluid());
        clearBtn.setHalfSize(true);
        clearBtn.setDisableBackground(true);
        widgets.add("clearFluid", clearBtn);

        AddonActionButton clearOutBtn =
                new AddonActionButton(AddonActionItems.FLUID_FLUSH, btn -> menu.clearFluidOut());
        clearOutBtn.setHalfSize(true);
        clearOutBtn.setDisableBackground(true);
        widgets.add("clearFluidOut", clearOutBtn);

        this.powerAlert = new AlertWidget(style.getImage("powerAlert"));
        this.widgets.add("powerAlert", this.powerAlert);
    }

    @Override
    protected void init() {
        this.inputSlot = this.addRenderableWidget(new FluidTankSlot(
                this,
                1,
                this.leftPos + INPUT_TANK_X,
                this.topPos + TANKS_Y,
                TANKS_WIDTH,
                TANKS_HEIGHT,
                this.menu.INPUT_FLUID_SIZE));
        this.outputSlot = this.addRenderableWidget(new FluidTankSlot(
                this,
                0,
                this.leftPos + OUTPUT_TANK_X,
                this.topPos + TANKS_Y,
                TANKS_WIDTH,
                TANKS_HEIGHT,
                this.menu.OUTPUT_FLUID_SIZE));

        super.init();
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        int progress = this.menu.getCurrentProgress() * 100 / this.menu.getMaxProgress();
        this.pb.setFullMsg(new TextComponent(progress + "%"));

        if (this.autoExportBtn != null) this.autoExportBtn.set(getMenu().getAutoExport());
        if (this.outputConfigure != null) this.outputConfigure.setVisibility(getMenu().getAutoExport() == YesNo.YES);

        this.inputSlot.x = this.leftPos + INPUT_TANK_X; this.inputSlot.y = this.topPos + TANKS_Y;
        this.outputSlot.x = this.leftPos + OUTPUT_TANK_X; this.outputSlot.y = this.topPos + TANKS_Y;

        this.powerAlert.visible = this.getMenu().getShowWarning();
    }

    @Override
    public void updateFluidTankContents(int index, FluidStack stack) {
        this.menu.setClientFluid(index, stack);
        if (index == 1) {
            this.inputSlot.setFluidStack(stack);
        } else {
            this.outputSlot.setFluidStack(stack);
        }
    }

    private static class AlertWidget extends AbstractWidget {

        private final Blitter powerAlert;

        public AlertWidget(Blitter powerAlert) {
            super(0, 0, 18, 18, new TextComponent(""));
            this.powerAlert = powerAlert;
        }

        public void renderButton(PoseStack guiGraphics, int i, int i1, float v) {
            this.powerAlert.dest(this.x, this.y).blit(guiGraphics, 255);
        }

        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

        public void updateNarration(NarrationElementOutput narrationElementOutput) {
            this.defaultButtonNarrationText(narrationElementOutput);
        }
    }
}
