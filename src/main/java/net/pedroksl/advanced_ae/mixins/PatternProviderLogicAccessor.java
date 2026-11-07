package net.pedroksl.advanced_ae.mixins;

import appeng.helpers.iface.PatternProviderLogic;
import appeng.helpers.iface.PatternProviderLogicHost;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PatternProviderLogic.class, remap = false)
public interface PatternProviderLogicAccessor {
     @Accessor("host")
     PatternProviderLogicHost getHost();
}
