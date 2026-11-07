package net.pedroksl.ae2addonlib.client.render;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;

public abstract class ConnectedTexturesBaseBakedModel implements BakedModel {
    protected TextureAtlasSprite face;

    protected ConnectedTexturesBaseBakedModel() {}

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return face;
    }

    @Override
    public net.minecraft.client.renderer.block.model.ItemTransforms getTransforms() {
        return net.minecraft.client.renderer.block.model.ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public net.minecraft.client.renderer.block.model.ItemOverrides getOverrides() {
        return net.minecraft.client.renderer.block.model.ItemOverrides.EMPTY;
    }
}
