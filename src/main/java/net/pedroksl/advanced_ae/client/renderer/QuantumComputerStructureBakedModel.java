package net.pedroksl.advanced_ae.client.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.pedroksl.advanced_ae.common.blocks.AAEAbstractCraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;

import appeng.client.render.cablebus.CubeBuilder;

public class QuantumComputerStructureBakedModel extends AAECraftingCubeBakedModel {

    private final TextureAtlasSprite face;

    private final TextureAtlasSprite sides;

    private final TextureAtlasSprite poweredSides;

    public QuantumComputerStructureBakedModel(
            TextureAtlasSprite face,
            TextureAtlasSprite sides,
            TextureAtlasSprite poweredSides) {
        super(sides, sides, sides);

        this.face = face;
        this.sides = sides;
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
        boolean powered = state.getValue(AAEAbstractCraftingUnitBlock.POWERED);

        TextureAtlasSprite innerTexture = powered ? this.poweredSides : this.sides;
        if (innerTexture == null) {
            innerTexture = this.face;
        }
        builder.setTexture(innerTexture);
        builder.addCube(x1, y1, z1, x2, y2, z2);

        if (powered) {
            builder.setEmissiveMaterial(true);
            builder.setTexture(this.face);
            builder.addCube(x1, y1, z1, x2, y2, z2);
            builder.setEmissiveMaterial(false);
        }
    }

    protected boolean shouldConnect(net.minecraft.world.level.block.Block block) {
        return block instanceof AAECraftingUnitBlock unit && unit.type == AAECraftingUnitType.STRUCTURE;
    }

    protected boolean shouldBeEmissive(BlockState state) {
        return state.getValue(AAEAbstractCraftingUnitBlock.POWERED);
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.face;
    }
}
