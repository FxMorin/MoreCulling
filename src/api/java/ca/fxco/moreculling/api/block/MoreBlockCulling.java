package ca.fxco.moreculling.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface MoreBlockCulling {

    /**
     * An isSideInvisible() call which passes position which can be used for more complicated invisible side checks
     */
    default boolean isSideInvisibleAtPos(BlockState state, BlockState sideState, Direction direction, BlockPos pos) {
        return state.isSideInvisible(sideState, direction);
    }
}
