package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import ca.fxco.moreculling.utils.CullingUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

import static ca.fxco.moreculling.MoreCulling.DONT_CULL;
import static ca.fxco.moreculling.MoreCulling.blockRenderManager;
import static ca.fxco.moreculling.config.option.LeavesCullingMode.FAST;
import static ca.fxco.moreculling.config.option.LeavesCullingMode.VERTICAL;

@Mixin(value = LeavesBlock.class, priority = 1220)
public class LeavesBlock_typesMixin extends Block implements MoreBlockCulling {

    @Shadow
    @Final
    public static IntProperty DISTANCE;

    public LeavesBlock_typesMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (MoreCulling.CONFIG.leavesCullingMode == FAST || CullingUtils.areLeavesOpaque() ||
                (MoreCulling.CONFIG.leavesCullingMode == VERTICAL && direction.getAxis() == Direction.Axis.Y)) {
            return stateFrom.getBlock() instanceof LeavesBlock || super.isSideInvisible(state, stateFrom, direction);
        }
        return super.isSideInvisible(state, stateFrom, direction);
    }

    @Override
    public boolean usesCustomShouldDrawFace(BlockState state) {
        return MoreCulling.CONFIG.leavesCullingMode != LeavesCullingMode.DEFAULT; //Fast & Vertical will skip this call
    }

    @Override
    public Optional<Boolean> customShouldDrawFace(BlockView view, BlockState thisState, BlockState sideState,
                                                  BlockPos thisPos, BlockPos sidePos, Direction side) {
        return switch (MoreCulling.CONFIG.leavesCullingMode) {
            case STATE -> sideState.getBlock() instanceof LeavesBlock && sideState.get(DISTANCE) % 3 != 1 ?
                    Optional.of(false) : Optional.empty();
            case CHECK -> CullingUtils.shouldDrawFaceCheck(view, sideState, thisPos, sidePos, side);
            case GAP -> CullingUtils.shouldDrawFaceGap(view, sideState, sidePos, side);
            case DEPTH -> CullingUtils.shouldDrawFaceDepth(view, sideState, sidePos, side);
            case RANDOM -> CullingUtils.shouldDrawFaceRandom(view, sideState, sidePos, side);
            default -> Optional.empty();
        };
    }

    @Override
    public boolean shouldAttemptToCull(BlockState state) {
        return CullingUtils.areLeavesOpaque() &&
                !((BakedOpacity) blockRenderManager.getModel(state)).hasTextureTranslucency(state);
    }

    @Override
    public boolean cantCullAgainst(BlockState state, Direction side) {
        return true;
    }
}
