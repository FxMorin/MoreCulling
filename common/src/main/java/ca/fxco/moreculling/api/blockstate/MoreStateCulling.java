package ca.fxco.moreculling.api.blockstate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

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
     * @since 0.25.0
     */
    boolean moreculling$usesCustomShouldDrawFace();

    /**
     * Calling this method will check if the face should be drawn.
     * Returns an optional boolean, when empty it should run the vanilla block culling checks
     *
     * @since 0.25.0
     */
    Optional<Boolean> moreculling$customShouldDrawFace(BlockGetter view, BlockState sideState, BlockPos thisPos,
                                                       BlockPos sidePos, Direction side);

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
     * @since 0.25.0
     */
    boolean moreculling$cantCullAgainst(Direction side);

    /**
     * This returns true if the mod that the block is from allows culling in the config.
     *
     * @since 0.25.0
     */
    default boolean moreculling$canCull() {
        return false;
    }
}
