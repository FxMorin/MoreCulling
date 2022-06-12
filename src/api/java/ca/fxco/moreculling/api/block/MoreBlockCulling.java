package ca.fxco.moreculling.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public interface MoreBlockCulling {

    /**
     * An isSideInvisible() call which passes position which can be used for more complicated invisible side checks
     * This provides more opportunities for aggressive culling at the cost of cpu performance over gpu performance
     */
    default boolean isSideInvisibleAtPos(BlockState state, BlockState sideState, Direction direction, BlockPos pos) {
        return state.isSideInvisible(sideState, direction);
    }

    /**
     * This method needs to return true in order to use custom culling through the API.
     */
    default boolean usesCustomShouldDrawFace(BlockState state) {
        return false;
    }

    /**
     * Use this in order to do custom culling. Returning true will draw the face.
     */
    default boolean customShouldDrawFace(BlockView view, BlockState thisState, BlockState sideState, BlockPos thisPos, BlockPos sidePos, Direction side) {
        return true;
    }
}
