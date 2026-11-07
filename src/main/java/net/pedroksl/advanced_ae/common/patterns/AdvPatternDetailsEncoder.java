package net.pedroksl.advanced_ae.common.patterns;

import java.util.HashMap;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.patterns.AdvPatternEncoding;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;

public final class AdvPatternDetailsEncoder {

    public static ItemStack encodeProcessingPattern(
            GenericStack[] sparseInputs, GenericStack[] sparseOutputs, HashMap<AEKey, Direction> dirMap) {
        ItemStack pattern = AAEItems.ADV_PROCESSING_PATTERN.stack();
        AdvPatternEncoding.encodeProcessingPattern(pattern.getOrCreateTag(), sparseInputs, sparseOutputs, dirMap);
        return pattern;
    }


    public static ItemStack updateDirection(ItemStack stack, AEKey key, net.minecraft.core.Direction dir) {
        net.minecraft.nbt.CompoundTag nbt = stack.getOrCreateTag();
        appeng.api.stacks.GenericStack[] inputs = net.pedroksl.advanced_ae.common.patterns.AdvPatternEncoding.getProcessingInputs(nbt);
        appeng.api.stacks.GenericStack[] outputs = net.pedroksl.advanced_ae.common.patterns.AdvPatternEncoding.getProcessingOutputs(nbt);
        java.util.LinkedHashMap<AEKey, net.minecraft.core.Direction> dirMap =
                net.pedroksl.advanced_ae.common.patterns.AdvPatternEncoding.getInputDirections(nbt);
        dirMap.put(key, dir);
        return encodeProcessingPattern(inputs, outputs, dirMap);
    }

}
