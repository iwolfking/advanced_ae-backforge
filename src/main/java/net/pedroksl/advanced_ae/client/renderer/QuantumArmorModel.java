package net.pedroksl.advanced_ae.client.renderer;

import net.minecraft.resources.ResourceLocation;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

import software.bernie.geckolib3.model.AnimatedGeoModel;

public class QuantumArmorModel
        extends AnimatedGeoModel<QuantumArmorBase> {

    @Override
    public ResourceLocation getModelLocation(QuantumArmorBase animatable) {
        return AdvancedAE.makeId("geo/item/quantum_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(QuantumArmorBase animatable) {
        return AdvancedAE.makeId("textures/item/quantum_armor.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(QuantumArmorBase animatable) {
        return AdvancedAE.makeId("animations/item/quantum_armor.animation.json");
    }
}