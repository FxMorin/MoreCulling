package ca.fxco.moreculling.api.renderers;

import net.minecraft.core.BlockPos;

/**
 * This interface allows you to access the block positions stored within the render state by MoreCulling.
 *
 * @since 1.2.4
 */
public interface ExtendedPaintingRenderState {

    /**
     * Gets a 2D array containing the block positions which the painting is in.
     *
     * @return A 2D array of block positions
     * @since 1.2.4
     */
    BlockPos[][] moreculling$getPaintingPositions();

    /**
     * Sets the 2D array of block positions which the painting is in.
     * This is an internal method and shouldn't be used outside extracting the render states.
     *
     * @since 1.2.4
     */
    void moreculling$setPaintingPositions(BlockPos[][] positions);
}