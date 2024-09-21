package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import ca.fxco.moreculling.utils.CullingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

import static ca.fxco.moreculling.config.option.LeavesCullingMode.FAST;
import static ca.fxco.moreculling.config.option.LeavesCullingMode.VERTICAL;

@Mixin(value = LeavesBlock.class, priority = 1220)
public class LeavesBlock_typesMixin extends Block implements MoreBlockCulling {

    @Shadow
    @Final
    public static IntegerProperty DISTANCE;

    public LeavesBlock_typesMixin(Properties settings) {
        super(settings);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState stateFrom, Direction direction) {
        if (MoreCulling.CONFIG.leavesCullingMode == FAST || CullingUtils.areLeavesOpaque() ||
                (MoreCulling.CONFIG.leavesCullingMode == VERTICAL && direction.getAxis() == Direction.Axis.Y)) {
            return stateFrom.getBlock() instanceof LeavesBlock || super.skipRendering(state, stateFrom, direction);
        }
        return super.skipRendering(state, stateFrom, direction);
    }

    @Override
    public boolean moreculling$usesCustomShouldDrawFace(BlockState state) {
        return MoreCulling.CONFIG.leavesCullingMode != LeavesCullingMode.DEFAULT; //Fast & Vertical will skip this call
    }

    @Override
    public Optional<Boolean> moreculling$customShouldDrawFace(BlockGetter view, BlockState thisState,
                                                              BlockState sideState, BlockPos thisPos,
                                                              BlockPos sidePos, Direction side) {
        return switch (MoreCulling.CONFIG.leavesCullingMode) {
            case STATE -> sideState.getBlock() instanceof LeavesBlock && sideState.getValue(DISTANCE) % 3 != 1 ?
                    Optional.of(false) : Optional.empty();
            case CHECK -> CullingUtils.shouldDrawFaceCheck(view, sideState, thisPos, sidePos, side);
            case GAP -> CullingUtils.shouldDrawFaceGap(view, sideState, sidePos, side);
            case DEPTH -> CullingUtils.shouldDrawFaceDepth(view, sideState, sidePos, side);
            case RANDOM -> CullingUtils.shouldDrawFaceRandom(view, sideState, sidePos, side);
            default -> Optional.empty();
        };
    }

    @Override
    public boolean moreculling$cantCullAgainst(BlockState state, Direction side) {
        return true;
    }
}
