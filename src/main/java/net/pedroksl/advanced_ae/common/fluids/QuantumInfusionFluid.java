package net.pedroksl.advanced_ae.common.fluids;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;

public abstract class QuantumInfusionFluid extends ForgeFlowingFluid {
    public static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(
                    AAEFluids.QUANTUM_INFUSION::flowing,
                    AAEFluids.QUANTUM_INFUSION::source,
                    FluidAttributes.builder(
                            new ResourceLocation("minecraft:block/water_still"),
                            new ResourceLocation("minecraft:block/water_flow"))
                    .color(0xFF7362D3))
            .bucket(AAEFluids.QUANTUM_INFUSION::bucketItem)
            .block(AAEFluids.QUANTUM_INFUSION::block);

    protected QuantumInfusionFluid(Properties properties) {
        super(properties);
    }

    @Override
    public Fluid getFlowing() {
        return AAEFluids.QUANTUM_INFUSION.flowing();
    }

    @Override
    public Fluid getSource() {
        return AAEFluids.QUANTUM_INFUSION.source();
    }

    public Item getBucket() {
        return AAEFluids.QUANTUM_INFUSION.bucketItem();
    }

    protected boolean canConvertToSource(Level pLevel) {
        return false;
    }

    public static class Flowing extends QuantumInfusionFluid {
        public Flowing() {
            super(PROPERTIES);
        }

        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> pBuilder) {
            super.createFluidStateDefinition(pBuilder);
            pBuilder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState pState) {
            return pState.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState pState) {
            return false;
        }
    }

    public static class Source extends QuantumInfusionFluid {
        public Source() {
            super(PROPERTIES);
        }

        @Override
        public int getAmount(FluidState pState) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState pState) {
            return true;
        }
    }
}
