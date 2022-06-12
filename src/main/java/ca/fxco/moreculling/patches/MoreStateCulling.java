package ca.fxco.moreculling.patches;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.Optional;

public interface MoreStateCulling {
    boolean usesCustomShouldDrawFace();
    Optional<Boolean> customShouldDrawFace(BlockView view, BlockState sideState, BlockPos thisPos, BlockPos sidePos, Direction side);
}
