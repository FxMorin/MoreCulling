package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.utils.CullingUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static ca.fxco.moreculling.MoreCulling.DONT_CULL;
import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(value = Block.class, priority = 2500)
public class Block_drawSideMixin implements MoreBlockCulling {

    private boolean allowCulling;

    @Override
    public boolean cantCullAgainst(BlockState state) {
        return state.isIn(DONT_CULL);
    }

    @Override
    public boolean cantCullAgainst(BlockState state, Direction side) {
        return state.isIn(DONT_CULL);
    }

    @Override
    public boolean shouldAttemptToCull(BlockState state) {
        return !((BakedOpacity)blockRenderManager.getModel(state)).hasTextureTranslucency(state);
    }

    @Override
    public boolean shouldAttemptToCull(BlockState state, Direction side) {
        return !((BakedOpacity)blockRenderManager.getModel(state)).hasTextureTranslucency(state, side);
    }

    @Override
    public boolean canCull() {
        return this.allowCulling;
    }

    @Override
    public void setCanCull(boolean canCull) {
        this.allowCulling = canCull;
    }


    /**
     * Many mods use Block.shouldDrawSide() directly so its basically required that we override it (using an inject)
     * If your mixin breaks due to this, please use the API if MoreCulling is present
     */
    @Inject(
            method = "shouldDrawSide",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void customShouldDrawSide(BlockState state, BlockView world, BlockPos pos, Direction side,
                                       BlockPos otherPos, CallbackInfoReturnable<Boolean> cir) {
        if (MoreCulling.CONFIG.useBlockStateCulling && ((MoreStateCulling)state).canCull()) {
            cir.setReturnValue(CullingUtils.shouldDrawSideCulling(state, world, pos, side, otherPos));
        }
    }
}
