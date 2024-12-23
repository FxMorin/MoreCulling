package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EndRodBlock.class)
public abstract class EndRodBlock_cullMixin implements MoreBlockCulling {
    @Override
    public boolean moreculling$shouldAttemptToCull(BlockState state, Direction side, BlockGetter level, BlockPos pos) {
        return side == state.getValue(RodBlock.FACING);
    }
}
