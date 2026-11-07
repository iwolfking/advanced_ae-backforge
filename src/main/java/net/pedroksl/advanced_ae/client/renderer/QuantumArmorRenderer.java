package net.pedroksl.advanced_ae.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.helpers.AAEColor;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.util.EModelRenderCycle;

public class QuantumArmorRenderer
        extends GeoArmorRenderer<QuantumArmorBase> {

    public static final String HUD_BONE = "hud";
    public static final String LEFT_BLADE_BONE = "blade_left";
    public static final String RIGHT_BLADE_BONE = "blade_right";
    public static final String FACE_SHIELD_BONE = "face_shield";
    public static final String RIGHT_ARM = "armorRightArm";
    public static final String LEFT_ARM = "armorLeftArm";

    private static final ResourceLocation TINT_TEXTURE =
            AdvancedAE.makeId("textures/item/quantum_armor_tint.png");

    private int tintColor = AAEColor.PURPLE.argb();

    public QuantumArmorRenderer() {
        super(new QuantumArmorModel());
    }

    public void setBoneVisible(String boneName, boolean visible) {
        getGeoModelProvider()
                .getBone(boneName)
                .setHidden(!visible);
    }

    public void setTintColor(int color) {
        this.tintColor = color;
    }

    @Override
    public void render(
            GeoModel model,
            QuantumArmorBase animatable,
            float partialTick,
            RenderType renderType,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        setCurrentRTB(bufferSource);

        renderEarly(
                animatable,
                poseStack,
                partialTick,
                bufferSource,
                buffer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );

        if (bufferSource != null) {
            buffer = bufferSource.getBuffer(renderType);
        }

        renderLate(
                animatable,
                poseStack,
                partialTick,
                bufferSource,
                buffer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );

        for (var bone : model.topLevelBones) {
            renderRecursively(
                    bone,
                    poseStack,
                    buffer,
                    packedLight,
                    packedOverlay,
                    red,
                    green,
                    blue,
                    alpha
            );
        }

        MultiBufferSource.BufferSource tintBufferSource =
                Minecraft.getInstance().renderBuffers().bufferSource();

        AAEColor color = AAEColor.ofArgb(this.tintColor);

        RenderType tintRenderType = RenderType.entityCutout(TINT_TEXTURE);

        VertexConsumer tintBuffer =
                tintBufferSource.getBuffer(tintRenderType);

        renderTintPass(
                model,
                poseStack,
                tintBuffer,
                packedLight,
                packedOverlay,
                color.r(),
                color.g(),
                color.b(),
                color.a()
        );

        tintBufferSource.endBatch(tintRenderType);

        // Emissive armor rendering
        RenderType emissiveRenderType = RenderType.eyes(TINT_TEXTURE);

        VertexConsumer emissiveBuffer =
                tintBufferSource.getBuffer(emissiveRenderType);

        renderTintPass(
                model,
                poseStack,
                emissiveBuffer,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                color.r(),
                color.g(),
                color.b(),
                color.a()
        );

        tintBufferSource.endBatch(emissiveRenderType);

        setCurrentModelRenderCycle(EModelRenderCycle.REPEATED);
    }

    private void renderTintPass(
            GeoModel model,
            PoseStack poseStack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        for (var bone : model.topLevelBones) {
            renderRecursively(
                    bone,
                    poseStack,
                    buffer,
                    packedLight,
                    packedOverlay,
                    red,
                    green,
                    blue,
                    alpha
            );
        }
    }
}