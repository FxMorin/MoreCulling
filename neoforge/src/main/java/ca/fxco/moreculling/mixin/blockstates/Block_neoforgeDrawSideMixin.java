package ca.fxco.moreculling.mixin.blockstates;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.block.MoreBlockCulling;
import ca.fxco.moreculling.api.blockstate.MoreStateCulling;
import ca.fxco.moreculling.api.model.BakedOpacity;
import ca.fxco.moreculling.utils.CullingUtils;
import com.mojang.logging.LogUtils;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static ca.fxco.moreculling.MoreCulling.DONT_CULL;
import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Mixin(value = Block.class, priority = 2500)
public class Block_neoforgeDrawSideMixin implements MoreBlockCulling {
    /**
     * Many mods use Block.shouldDrawSide() directly so its basically required that we override it (using an inject)
     * If your mixin breaks due to this, please use the API if MoreCulling is present
     */
    @Inject(
            method = "shouldRenderFace(Lnet/minecraft/world/level/BlockGetter;" +
                    "Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;" +
                    "Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void moreculling$customShouldDrawSide(BlockGetter level, BlockPos pos, BlockState thisState,
                                                         BlockState sideState, Direction side,
                                                         CallbackInfoReturnable<Boolean> cir) {
        if (MoreCulling.CONFIG.useBlockStateCulling && ((MoreStateCulling) thisState).moreculling$canCull()) {
            cir.setReturnValue(CullingUtils.shouldDrawSideCulling(thisState, sideState,
                    EmptyBlockGetter.INSTANCE, pos, side, BlockPos.ZERO));
        }
    }
}
