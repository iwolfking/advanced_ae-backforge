package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.client.gui.widgets.AAESettingToggleButton;
import net.pedroksl.advanced_ae.gui.AdvancedIOBusMenu;

import appeng.api.config.YesNo;
import appeng.client.gui.style.ScreenStyle;
import net.pedroksl.advanced_ae.mixins.AbstractContainerScreenAccessor;

public class AdvancedIOBusScreen extends StockExportBusScreen<AdvancedIOBusMenu> {

    private final AAESettingToggleButton<YesNo> regulateButton;

    public AdvancedIOBusScreen(AdvancedIOBusMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.regulateButton = AAESettingToggleButton.serverButton(AAESettings.REGULATE_STOCK, YesNo.YES);
        this.addToLeftToolbar(this.regulateButton);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.regulateButton.set(getMenu().getRegulate());
    }


    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        assert this.minecraft != null;

        if (this.minecraft.options.keyPickItem.matchesMouse(btn)) {
            Slot slot = ((AbstractContainerScreenAccessor)this).invokeFindSlot(xCoord, yCoord);
            if (this.isValidSlot(slot)) {
                this.menu.openAmountMenu(slot.index);
            }
        }

        return super.mouseClicked(xCoord, yCoord, btn);
    }
}