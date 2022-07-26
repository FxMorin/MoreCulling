package ca.fxco.moreculling.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.Optional;

/**
 * MoreBlockCulling is an interface that should be used on classes that extend Block
 * It allows you to implement custom culling techniques on blocks, while still using MoreCulling's blockstate culling
 */

public interface MoreBlockCulling {

    /**
     * This method needs to return true in order to use custom culling through the API.
     */
    default boolean usesCustomShouldDrawFace(BlockState state) {
        return false;
    }

    /**
     * Use this in order to do custom culling. Returning an empty optional will continue running the draw side checks
     * Returning true will draw the face
     */
    default Optional<Boolean> customShouldDrawFace(BlockView view, BlockState thisState, BlockState sideState, BlockPos thisPos, BlockPos sidePos, Direction side) {
        return Optional.empty();
    }
}
