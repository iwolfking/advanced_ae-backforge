package net.pedroksl.advanced_ae.gui.patternencoder;

import java.util.LinkedHashMap;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.inventory.AdvPatternEncoderHost;
import net.pedroksl.advanced_ae.common.patterns.AdvPatternDetailsEncoder;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.AdvPatternEncoderPacket;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.OutputSlot;
import appeng.menu.slot.RestrictedInputSlot;

public class AdvPatternEncoderMenu extends AEBaseMenu {

    private final RestrictedInputSlot inputSlot;
    private final OutputSlot outputSlot;
    private final AdvPatternEncoderHost host;

    public AdvPatternEncoderMenu(int id, Inventory playerInventory, AdvPatternEncoderHost host) {
        super(AAEMenus.ADV_PATTERN_ENCODER.get(), id, playerInventory, host);
        this.createPlayerInventorySlots(playerInventory);
        this.host = host;

        this.addSlot(
                this.inputSlot = new RestrictedInputSlot(
                        RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN, host.getInventory(), 0),
                SlotSemantics.ENCODED_PATTERN);
        this.addSlot(this.outputSlot = new OutputSlot(host.getInventory(), 1, null), SlotSemantics.MACHINE_OUTPUT);

        this.host.setInventoryChangedHandler(this::onChangeInventory);
        if (this.inputSlot.hasItem()) {
            decodeInputPattern();
        }
    }

    public AdvPatternEncoderHost getHost() {
        return this.host;
    }

    public void onChangeInventory(InternalInventory inv, int slot) {
        if (inv == host.getInventory()) {
            if (slot == 0) {
                if (this.inputSlot.hasItem() && !this.outputSlot.hasItem()) {
                    decodeInputPattern();
                } else if (!this.inputSlot.hasItem()) {
                    clearDecodedPattern();
                    this.outputSlot.set(ItemStack.EMPTY);
                }
            } else if (slot == 1 && !this.outputSlot.hasItem()) {
                clearDecodedPattern();
                this.inputSlot.set(ItemStack.EMPTY);
            }
        }
    }

    private void decodeInputPattern() {
        ItemStack stack = this.inputSlot.getItem();

        IPatternDetails details =
                PatternDetailsHelper.decodePattern(stack, this.getPlayer().level, false);

        if (details == null) return;

        if (!(details instanceof AEProcessingPattern pattern)) return;
        var sparseInputs = pattern.getSparseInputs();

        LinkedHashMap<AEKey, Direction> inputList = new LinkedHashMap<>();
        for (GenericStack input : sparseInputs) {
            if (input == null) {
                continue;
            }

            if (!inputList.containsKey(input.what())) {
                Direction dir = null;
                inputList.put(input.what(), dir);
            }
        }

        var newAdvPattern = AdvPatternDetailsEncoder.encodeProcessingPattern(
                pattern.getSparseInputs(), pattern.getSparseOutputs(), inputList);
        System.out.println("[AAE] setting output=" + newAdvPattern);
        this.outputSlot.set(newAdvPattern);

        if (this.getPlayer() instanceof ServerPlayer sp) {
            AAENetworkHandler.INSTANCE.sendTo(new AdvPatternEncoderPacket(inputList), sp);
        }
    }

    private void clearDecodedPattern() {
        if (this.getPlayer() instanceof ServerPlayer sp) {
            AAENetworkHandler.INSTANCE.sendTo(new AdvPatternEncoderPacket(), sp);
        }
    }

    public void update(AEKey key, Direction dir) {
        if (!this.outputSlot.hasItem()) {
            copyItemToOutputSlot();
        }

        if (!this.outputSlot.hasItem()) {
            return;
        }

        ItemStack updated = net.pedroksl.advanced_ae.common.patterns.AdvPatternDetailsEncoder.updateDirection(
                this.outputSlot.getItem(), key, dir);
        this.outputSlot.set(updated);

        if (this.getPlayer() instanceof ServerPlayer sp) {
            var dirMap = net.pedroksl.advanced_ae.common.patterns.AdvPatternEncoding.getInputDirections(
                    updated.getOrCreateTag());
            AAENetworkHandler.INSTANCE.sendTo(new net.pedroksl.advanced_ae.network.packet.AdvPatternEncoderPacket(dirMap), sp);
        }

    }

    public void copyItemToOutputSlot() {
        ItemStack stack = this.inputSlot.getItem();

        IPatternDetails details =
                PatternDetailsHelper.decodePattern(stack, this.getPlayer().level, false);

        if (details == null) return;

        if (!(details instanceof AEProcessingPattern pattern)) return;
        this.outputSlot.set(stack);
    }

    public interface inventoryChangedHandler {
        void handleChange(InternalInventory inv, int slot);
    }
}
