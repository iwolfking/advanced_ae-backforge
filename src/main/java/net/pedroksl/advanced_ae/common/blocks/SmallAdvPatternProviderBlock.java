package net.pedroksl.advanced_ae.common.blocks;


import net.pedroksl.advanced_ae.common.definitions.AAEBlockEntities;

import static net.pedroksl.advanced_ae.common.blocks.AdvPatternProviderBlock.CONNECTION_STATE;
import static net.pedroksl.advanced_ae.common.blocks.AdvPatternProviderBlock.PUSH_DIRECTION;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.SmallAdvPatternProviderEntity;

import appeng.block.AEBaseEntityBlock;
import net.pedroksl.advanced_ae.common.blocks.PushDirection;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.util.InteractionUtil;
import appeng.util.Platform;

public class SmallAdvPatternProviderBlock extends AEBaseEntityBlock<SmallAdvPatternProviderEntity> {
    public SmallAdvPatternProviderBlock() {
        super(appeng.block.AEBaseBlock.defaultProps(net.minecraft.world.level.material.Material.METAL));
        this.registerDefaultState(this.defaultBlockState()
                .setValue(PUSH_DIRECTION, PushDirection.ALL)
                .setValue(CONNECTION_STATE, false));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PUSH_DIRECTION);
        builder.add(CONNECTION_STATE);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void neighborChanged(
            BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        var be = this.getBlockEntity(level, pos);
        if (be != null) {
            be.getLogic().updateRedstoneState();

        }
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        var be = this.getBlockEntity(level, pos);
        if (be != null && !level.isClientSide()) {
        }
    }

    @Override
    public InteractionResult onActivated(
            Level level,
            BlockPos pos,
            Player p,
            InteractionHand hand,
            @Nullable ItemStack heldItem,
            BlockHitResult hit) {
        if (InteractionUtil.isInAlternateUseMode(p)) {
            return InteractionResult.PASS;
        }

        if (heldItem != null && InteractionUtil.canWrenchRotate(heldItem)) {
            setSide(level, pos, hit.getDirection());
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        var be = this.getBlockEntity(level, pos);
        if (be != null) {
            if (!level.isClientSide()) {
                MenuOpener.open(AAEMenus.SMALL_ADV_PATTERN_PROVIDER.get(), p, MenuLocators.forBlockEntity(be));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    public void setSide(Level level, BlockPos pos, Direction facing) {
        // push direction unavailable in AE2 1.18
    }

    @Override
    public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(
            net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        return AAEBlockEntities.SMALL_ADV_PATTERN_PROVIDER.get().create(pos, state);
    }
}
