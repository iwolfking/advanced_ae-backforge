package net.pedroksl.advanced_ae.client.renderer;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroksl.advanced_ae.common.blocks.AAEAbstractCraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.minecraftforge.client.model.data.IModelData;

import appeng.client.render.cablebus.CubeBuilder;

public class QuantumComputerInternalBakedModel extends AAECraftingCubeBakedModel {

    private final TextureAtlasSprite face;

    private final TextureAtlasSprite side;

    private final TextureAtlasSprite poweredSides;

    public QuantumComputerInternalBakedModel(
            TextureAtlasSprite face,
            TextureAtlasSprite side,
            TextureAtlasSprite poweredSides,
            TextureAtlasSprite faceAnimation,
            TextureAtlasSprite faceTopAnimation,
            TextureAtlasSprite faceBottomAnimation) {
        super(side, side, side);

        this.face = face;
        this.side = side;
        this.poweredSides = poweredSides;
    }

    @Override
    protected void addInnerCube(
            Direction facing,
            BlockState state,
            IModelData modelData,
            CubeBuilder builder,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2) {
        builder.setTexture(this.side);
        builder.addCube(x1, y1, z1, x2, y2, z2);
        if (state.getValue(AAEAbstractCraftingUnitBlock.POWERED)) {
            builder.setEmissiveMaterial(true);
            builder.setTexture(this.poweredSides);
            builder.addCube(x1, y1, z1, x2, y2, z2);
            builder.setEmissiveMaterial(false);
        }
    }

    protected boolean shouldConnect(Block block) {
        return block instanceof AAECraftingUnitBlock unit && unit.type != AAECraftingUnitType.STRUCTURE;
    }

    protected boolean shouldBeEmissive(BlockState state) {
        return state.getValue(AAEAbstractCraftingUnitBlock.POWERED);
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.face;
    }
}
