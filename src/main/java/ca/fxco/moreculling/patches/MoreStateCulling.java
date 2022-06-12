package ca.fxco.moreculling.patches;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public interface MoreStateCulling {
    boolean isSideInvisibleAtPos(BlockState state, Direction direction, BlockPos pos);
    boolean usesCustomShouldDrawFace();
    boolean customShouldDrawFace(BlockView view, BlockState sideState, BlockPos thisPos, BlockPos sidePos, Direction side);
}
