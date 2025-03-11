package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.LeavesCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import ca.fxco.moreculling.utils.CullingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MangroveRootsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = MangroveRootsBlock.class, priority = 1220)
public class MangroveRootsBlock_typesMixin extends Block implements MoreBlockCulling, LeavesCulling {

    public MangroveRootsBlock_typesMixin(Properties settings) {
        super(settings);
    }

    @Inject(
            method = "skipRendering",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void moreculling$skipRendering(BlockState thisState, BlockState sideState,
                                           Direction face, CallbackInfoReturnable<Boolean> cir) {
        if (!MoreCulling.CONFIG.includeMangroveRoots) {
            return;
        }
        if (MoreCulling.CONFIG.leavesCullingMode == LeavesCullingMode.FAST || CullingUtils.areLeavesOpaque()) {
            cir.setReturnValue(sideState.getBlock() instanceof LeavesCulling ||
                    super.skipRendering(thisState, sideState, face));
        }
    }

    @Override
    public boolean moreculling$usesCustomShouldDrawFace(BlockState state) {
        return MoreCulling.CONFIG.includeMangroveRoots &&
                MoreCulling.CONFIG.leavesCullingMode != LeavesCullingMode.DEFAULT; //Fast will skip this call
    }

    @Override
    public Optional<Boolean> moreculling$customShouldDrawFace(BlockGetter view, BlockState thisState,
                                                              BlockState sideState, BlockPos thisPos,
                                                              BlockPos sidePos, Direction side) {
        return switch (MoreCulling.CONFIG.leavesCullingMode) { // Can't use state culling here
            case CHECK -> CullingUtils.shouldDrawFaceCheck(view, sideState, sidePos, thisPos, side);
            case GAP -> CullingUtils.shouldDrawFaceGap(view, sideState, sidePos, side);
            case DEPTH -> CullingUtils.shouldDrawFaceDepth(view, sideState, sidePos, side);
            case RANDOM -> CullingUtils.shouldDrawFaceRandom(view, sideState, sidePos, side);
            default -> Optional.empty();
        };
    }
}
