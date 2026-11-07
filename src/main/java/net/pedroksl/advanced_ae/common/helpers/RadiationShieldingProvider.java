package net.pedroksl.advanced_ae.common.helpers;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import appeng.api.implementations.items.IAEItemPowerStorage;


public class RadiationShieldingProvider extends PoweredItemCapabilities {
    public RadiationShieldingProvider(ItemStack is, IAEItemPowerStorage item) {
        super(is, item);
    }

    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (false) return (LazyOptional<T>) LazyOptional.empty();
        return super.getCapability(capability, facing);
    }

    public double getRadiationShielding() {
        return 0.25;
    }
}
