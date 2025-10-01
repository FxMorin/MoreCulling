package ca.fxco.moreculling.api.renderers;

import net.minecraft.core.BlockPos;

public interface ExtendedPaintingRenderState {

    default BlockPos[][] moreculling$getBlockPoses() {
        return null;
    }

    default void moreculling$setBlockPos(BlockPos[][] pos) {

    }

}