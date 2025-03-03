package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.utils.CullingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static ca.fxco.moreculling.MoreCulling.DONT_CULL;
import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(value = Block.class, priority = 2500)
public class Block_drawSideMixin implements MoreBlockCulling {

    @Unique
    private boolean allowCulling;

    @Override
    public boolean moreculling$cantCullAgainst(BlockState state, Direction side) {
        return state.is(DONT_CULL);
    }

    @Override
    public boolean moreculling$shouldAttemptToCull(BlockState state, Direction side, BlockGetter level, BlockPos pos) {
        return !((BakedOpacity) blockRenderManager.getBlockModel(state)).moreculling$hasTextureTranslucency(state, side);
    }

    @Override
    public boolean moreculling$canCull() {
        return this.allowCulling;
    }

    @Override
    public void moreculling$setCanCull(boolean canCull) {
        this.allowCulling = canCull;
    }


    /**
     * Many mods use Block.shouldDrawSide() directly so its basically required that we override it (using an inject)
     * If your mixin breaks due to this, please use the API if MoreCulling is present
     */
    @Inject(
            method = "shouldRenderFace",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void moreculling$customShouldDrawSide(BlockState state, BlockGetter world, BlockPos pos,
                                                         Direction side, BlockPos otherPos,
                                                         CallbackInfoReturnable<Boolean> cir) {
        if (MoreCulling.CONFIG.useBlockStateCulling && ((MoreStateCulling) state).moreculling$canCull()) {
            cir.setReturnValue(CullingUtils.shouldDrawSideCulling(state, world, pos, side, otherPos));
        }
    }
}
