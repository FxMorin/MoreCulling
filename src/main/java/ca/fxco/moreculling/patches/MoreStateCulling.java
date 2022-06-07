package ca.fxco.moreculling.patches;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public interface MoreStateCulling {

    boolean isSideInvisibleAtPos(BlockState state, Direction direction, BlockPos pos);
}
