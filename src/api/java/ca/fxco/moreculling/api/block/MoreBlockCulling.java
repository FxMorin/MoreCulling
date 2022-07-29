package ca.fxco.moreculling.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.Optional;

/**
 * MoreBlockCulling is an interface that should be used on classes that extend Block
 * It allows you to implement custom culling techniques on blocks, while still using MoreCulling's blockstate culling
 * @since 0.3.0
 */

public interface MoreBlockCulling {

    /**
     * This method needs to return true in order to use custom culling through the API.
     * @since 0.3.0
     */
    default boolean usesCustomShouldDrawFace(BlockState state) {
        return false;
    }

    /**
     * Use this in order to do custom culling. Returning an empty optional will continue running the draw side checks
     * Returning true will draw the face
     * @since 0.3.0
     */
    default Optional<Boolean> customShouldDrawFace(BlockView view, BlockState thisState, BlockState sideState, BlockPos thisPos, BlockPos sidePos, Direction side) {
        return Optional.empty();
    }

    /**
     * This method allows you to specify if this block should be allowed to cull.
     * By default, it returns true if the blocks model does not have translucency
     * @since 0.8.0
     */
    // Only default so it does not need to be set every time. Actual default is done in Block_drawSideMixin
    default boolean shouldAttemptToCull(BlockState state) {
        return false;
    }

    /**
     * This method allows you to specify if this block should be allowed to be culled against.
     * By default, it returns `state.isIn(DONT_CULL)`
     * @since 0.8.0
     */
    // Only default so it does not need to be set every time. Actual default is done in Block_drawSideMixin
    default boolean cantCullAgainst(BlockState state) {
        return false;
    }
}
