package net.pedroksl.advanced_ae.common.definitions;

import java.util.List;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.ae2addonlib.registry.helpers.LibBlockDefinition;
import net.pedroksl.ae2addonlib.registry.helpers.LibItemDefinition;

public final class AAECreativeTab extends CreativeModeTab {

    public static final AAECreativeTab INSTANCE = new AAECreativeTab();

    private AAECreativeTab() {
        super(AdvancedAE.MOD_ID);
    }

    @Override
    public ItemStack makeIcon() {
        return AAEBlocks.ADV_PATTERN_PROVIDER.stack();
    }

    @Override
    public void fillItemList(net.minecraft.core.NonNullList<ItemStack> items) {
        for (LibItemDefinition<?> itemDef : AAEItems.getItems(AdvancedAE.MOD_ID)) {
            items.add(itemDef.stack());
        }
        for (LibBlockDefinition<?> blockDef : AAEBlocks.getBlocks(AdvancedAE.MOD_ID)) {
            items.add(new ItemStack(blockDef.asItem()));
        }
    }
}
