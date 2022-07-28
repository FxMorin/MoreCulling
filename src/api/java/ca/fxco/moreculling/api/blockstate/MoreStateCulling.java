package ca.fxco.moreculling.api.blockstate;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.Optional;

/**
 * MoreStateCulling is an interface that allows you to call MoreCulling's block culling checks directly from blockstate
 * @since 0.7.0
 */

public interface MoreStateCulling {

    /**
     * This will allow you to check if the state uses a custom should draw face check
     * @since 0.7.0
     */
    boolean usesCustomShouldDrawFace();

    /**
     * Calling this method will check if the face should be drawn.
     * Returns an optional boolean, when empty it should run the vanilla block culling checks
     * @since 0.7.0
     */
    Optional<Boolean> customShouldDrawFace(BlockView view, BlockState sideState, BlockPos thisPos,
                                           BlockPos sidePos, Direction side);
}
