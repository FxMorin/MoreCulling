package ca.fxco.moreculling.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

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
     * This is not used if blocks are opaque
     * @since 0.8.0
     */
    @Deprecated
    // Only default so it does not need to be set every time. Actual default is done in Block_drawSideMixin
    default boolean shouldAttemptToCull(BlockState state) {
        return false;
    }

    /**
     * This method allows you to specify if this block should be allowed to cull.
     * By default, it returns true if the blocks model does not have translucency
     * This is not used if blocks are opaque.
     * Allows you to pass the side to check against
     * @since 0.13.0
     */
    // Only default so it does not need to be set every time. Actual default is done in Block_drawSideMixin
    default boolean shouldAttemptToCull(BlockState state, @Nullable Direction side) {
        return false;
    }

    /**
     * This method allows you to specify if this block should be allowed to be culled against.
     * By default, it returns `state.isIn(DONT_CULL)`
     * @since 0.8.0
     */
    @Deprecated
    // Only default so it does not need to be set every time. Actual default is done in Block_drawSideMixin
    default boolean cantCullAgainst(BlockState state) {
        return false;
    }

    /**
     * This method allows you to specify if this block should be allowed to be culled against.
     * By default, it returns `state.isIn(DONT_CULL)`
     * Allows you to pass the side to check against
     * @since 0.14.0
     */
    // Only default so it does not need to be set every time. Actual default is done in Block_drawSideMixin
    default boolean cantCullAgainst(BlockState state, @Nullable Direction side) {
        return false;
    }

    /**
     * This method should not be overridden unless absolutely needed. It will return true if this block can be culled.
     * This returns true if the mod that the block is from allows culling in the config.
     * @since 0.10.0
     */
    // Only default so it does not need to be set every time. Actual default is done in Block_drawSideMixin
    default boolean canCull() {
        return false;
    }

    /**
     * This method should not be used unless absolutely needed. It will set the canCull state of the block.
     * This is used to set the cull state of blocks based on the mods option in the config
     * @since 0.10.0
     */
    // Only default so it does not need to be set every time. Actual default is done in Block_drawSideMixin
    default void setCanCull(boolean canCull) {}
}
