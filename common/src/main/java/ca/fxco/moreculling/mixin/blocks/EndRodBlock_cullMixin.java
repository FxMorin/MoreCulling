package ca.fxco.moreculling.mixin.blocks;

import ca.fxco.moreculling.api.block.MoreBlockCulling;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EndRodBlock.class)
public abstract class EndRodBlock_cullMixin implements MoreBlockCulling {
    @Override
    public boolean moreculling$shouldAttemptToCull(BlockState state, Direction side) {
        return side == state.getValue(RodBlock.FACING);
    }
}
