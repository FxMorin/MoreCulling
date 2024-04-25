package ca.fxco.moreculling.api.blockstate;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.Optional;

/**
 * MoreStateCulling is an interface that allows you to call MoreCulling's block culling checks directly from blockstate
 *
 * @since 0.7.0
 */
public interface MoreStateCulling {

    /**
     * This will allow you to check if the state uses a custom should draw face check
     *
     * @since 0.7.0
     * @deprecated As of v0.25.0, you should now be using {@link #moreculling$usesCustomShouldDrawFace}
     */
    @Deprecated(forRemoval = true)
    default boolean usesCustomShouldDrawFace() {
        return moreculling$usesCustomShouldDrawFace();
    }

    /**
     * This will allow you to check if the state uses a custom should draw face check
     *
     * @since 0.25.0
     */
    boolean moreculling$usesCustomShouldDrawFace();

    /**
     * Calling this method will check if the face should be drawn.
     * Returns an optional boolean, when empty it should run the vanilla block culling checks
     *
     * @since 0.7.0
     * @deprecated As of v0.25.0, you should now be using {@link #moreculling$customShouldDrawFace}
     */
    @Deprecated(forRemoval = true)
    default Optional<Boolean> customShouldDrawFace(BlockView view, BlockState sideState, BlockPos thisPos,
                                                   BlockPos sidePos, Direction side) {
        return moreculling$customShouldDrawFace(view, sideState, thisPos, sidePos, side);
    }

    /**
     * Calling this method will check if the face should be drawn.
     * Returns an optional boolean, when empty it should run the vanilla block culling checks
     *
     * @since 0.25.0
     */
    Optional<Boolean> moreculling$customShouldDrawFace(BlockView view, BlockState sideState, BlockPos thisPos,
                                                       BlockPos sidePos, Direction side);

    /**
     * This method allows you to check if a state should be allowed cull
     * By default, it returns true if the states model does not have translucency.
     * Allows you to pass the side to check against
     *
     * @since 0.13.0
     * @deprecated As of v0.25.0, you should now be using {@link #moreculling$shouldAttemptToCull}
     */
    @Deprecated(forRemoval = true)
    default boolean shouldAttemptToCull(Direction side) {
        return moreculling$shouldAttemptToCull(side);
    }

    /**
     * This method allows you to check if a state should be allowed cull
     * By default, it returns true if the states model does not have translucency.
     * Allows you to pass the side to check against
     *
     * @since 0.25.0
     */
    boolean moreculling$shouldAttemptToCull(Direction side);

    /**
     * This method allows you to check if a state should be allowed to be culled against.
     * By default, it returns `state.isIn(DONT_CULL)`
     *
     * @since 0.14.0
     * @deprecated As of v0.25.0, you should now be using {@link #moreculling$cantCullAgainst}
     */
    @Deprecated(forRemoval = true)
    default boolean cantCullAgainst(Direction side) {
        return moreculling$cantCullAgainst(side);
    }

    /**
     * This method allows you to check if a state should be allowed to be culled against.
     * By default, it returns `state.isIn(DONT_CULL)`
     *
     * @since 0.25.0
     */
    boolean moreculling$cantCullAgainst(Direction side);

    /**
     * This returns true if the mod that the block is from allows culling in the config.
     *
     * @since 0.13.0
     * @deprecated As of v0.25.0, you should now be using {@link #moreculling$canCull}
     */
    @Deprecated(forRemoval = true)
    default boolean canCull() {
        return moreculling$canCull();
    }

    /**
     * This returns true if the mod that the block is from allows culling in the config.
     *
     * @since 0.25.0
     */
    default boolean moreculling$canCull() {
        return false;
    }
}
